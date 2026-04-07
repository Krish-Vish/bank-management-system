package bank.service;

import bank.exception.AccountNotFoundException;
import bank.exception.BankException;
import bank.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Bank {
    private final String bankName;
    private final ConcurrentHashMap<String, Account> accounts;

    public Bank(String bankName) {
        if (bankName == null || bankName.isBlank())
            throw new BankException("bankName must not be blank");
        this.bankName = bankName;
        this.accounts = new ConcurrentHashMap<>();
    }

    private Account requireAccount(String accountId) {
        if (accountId == null) {
            throw new BankException("accountId must not be null");
        }
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }
        return account;
    }

    public String openAccount(String accountHolderName, AccountType type, BigDecimal initialDeposit) {
        if(accountHolderName == null || accountHolderName.isEmpty()) {
            throw new BankException("Account holder name must not be null or empty.");
        }
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new BankException("Initial deposit must be non-negative.");
        }

        String accountId = UUID.randomUUID().toString();
        Account newAccount;

        switch (type) {
            case SAVINGS:
                newAccount = new SavingsAccount(accountId, accountHolderName, initialDeposit);
                break;
            case CHECKING:
                newAccount = new CheckingAccount(accountId, accountHolderName, initialDeposit);
                break;
            default:
                throw new BankException("Invalid account type: " + type);
        }

        addAccount(newAccount);
        return accountId;
    }

    public void addAccount(Account account) {
        if (account == null) {
            throw new BankException("account must not be null");
        }
        if (accounts.containsKey(account.getAccountId())) {
            throw new BankException("Account with ID " + account.getAccountId() + " already exists.");
        }
        accounts.put(account.getAccountId(), account);
        System.out.println("Account with ID " + account.getAccountId() + " added successfully.");
    }

    public void closeAccount(String accountId) {
        Account account = requireAccount(accountId);

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BankException("Cannot close account with non-zero balance.");
        }

        accounts.remove(accountId);
        System.out.println("Account with ID " + accountId + " closed successfully.");
    }

    public Transaction deposit(String accountId, BigDecimal amount) {
        Account account = requireAccount(accountId);
        return account.deposit(amount);
    }

    public Transaction withdraw(String accountId, BigDecimal amount) {
        Account account = requireAccount(accountId);
        return account.withdraw(amount);
    }

    public Transaction[] transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        Account fromAccount = requireAccount(fromAccountId);
        Account toAccount = requireAccount(toAccountId);

        if (fromAccountId.equals(toAccountId)) {
            throw new BankException("Cannot transfer to the same account.");
        }

        // Prevent deadlocks by locking accounts in a consistent order
        Account firstLock = fromAccountId.compareTo(toAccountId) <= 0 ? fromAccount : toAccount;
        Account secondLock = fromAccountId.compareTo(toAccountId) <= 0 ? toAccount : fromAccount;

        synchronized (firstLock) {
            synchronized (secondLock) {
                Transaction debit;
                try {
                    debit = fromAccount.sendTransfer(amount);
                } catch (BankException ex) {
                    Transaction tx = Transaction.failed(fromAccountId, TransactionType.WITHDRAWAL, amount, fromAccount.getBalance(), ex.getMessage());
                    return new Transaction[]{tx, null};
                }

                Transaction credit;
                try {
                    credit = toAccount.receiveTransfer(amount);
                } catch (BankException ex) {
                    // Rollback withdrawal without adding a rollback transaction to history.
                    fromAccount.rollbackTransaction(debit);

                    Transaction tx = Transaction.failed(toAccountId, TransactionType.DEPOSIT, amount, toAccount.getBalance(), ex.getMessage());
                    return new Transaction[]{debit, tx};
                }

                return new Transaction[]{debit, credit};
            }
        }
    }

    public void applyMonthlyInterest() {
        for(Account account : accounts.values()) {
            BigDecimal balanceBefore = account.getBalance();
            account.applyMonthlyProcessing();

            if(account instanceof SavingsAccount) {
                System.out.println("Applied monthly interest to Savings Account ID " + account.getAccountId() + ". Balance before: $" + balanceBefore + ", Balance after: $" + account.getBalance());
            }
            else {
                System.out.println("Monthly counter reset for Account ID  " + account.getAccountId());
            }
        }
    }

    public List<Transaction> getTransactionHistory(String accountId, LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new BankException("from and to timestamps must not be null");
        }
        if (from.isAfter(to)) {
            throw new BankException("from timestamp must be before to timestamp");
        }

        return requireAccount(accountId).getTransactionHistory(from, to);
    }

    public void generateMonthlyStatement(String accountId) {

        Account account = requireAccount(accountId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("\nMonthly Statement for Account ID " + accountId + " (" + account.getAccountHolderName() + ")");
        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-16s %-10s %12s %12s %-8s%n", "Date/Time", "Type", "Amount", "Balance", "Status");
        System.out.println("--------------------------------------------------------------");

        List<Transaction> transactions = account.getTransactionHistory();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction tx : transactions) {
                String dateTime = tx.getTimestamp().format(formatter);
                String amount = tx.getAmount().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
                String balance = tx.getBalanceAfter().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
                System.out.printf("%-16s %-10s %12s %12s %-8s%n",
                        dateTime,
                        tx.getType(),
                        amount,
                        balance,
                        tx.getStatus());
            }
        }
        System.out.println("--------------------------------------------------------------");
        System.out.println("Ending balance: $" + account.getBalance().setScale(2, java.math.RoundingMode.HALF_UP));
    }

    public Account getAccount(String accountId) {
        return requireAccount(accountId);
    }

    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }
}