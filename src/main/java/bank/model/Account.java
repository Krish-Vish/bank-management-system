package bank.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bank.exception.*;

public abstract class Account {
    protected final String accountId;
    protected final String accountHolderName;
    protected BigDecimal balance;
    protected final List<Transaction> transactionHistory;

    public Account(String accountId, String accountHolderName, BigDecimal initialBalance) {
        if (accountId == null) {
            throw new BankException("accountId must not be null");
        }
        if (accountHolderName == null) {
            throw new BankException("accountHolderName must not be null");
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BankException("initialBalance must be >= 0");
        }

        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }
    
    public Transaction deposit(BigDecimal amount) {
        BigDecimal balanceBefore = balance;

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            Transaction tx = Transaction.failed(accountId, TransactionType.DEPOSIT, amount, balanceBefore, "Deposit amount must be greater than zero.");
            transactionHistory.add(tx);
            throw InvalidTransactionException.nonPositiveAmount(amount);
        }
        balance = balance.add(amount);
        Transaction tx = Transaction.success(accountId, TransactionType.DEPOSIT, amount, balanceBefore, balance);
        transactionHistory.add(tx);
        return tx;
    }

    public Transaction withdraw(BigDecimal amount) {
        BigDecimal balanceBefore = balance;

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            Transaction tx = Transaction.failed(accountId, TransactionType.WITHDRAWAL, amount, balanceBefore, "Withdrawal amount must be greater than zero.");
            transactionHistory.add(tx);
            throw InvalidTransactionException.nonPositiveAmount(amount);
        }
        if (amount.compareTo(balance) > 0) {
            Transaction tx = Transaction.failed(accountId, TransactionType.WITHDRAWAL, amount, balanceBefore, "Insufficient funds for withdrawal.");
            transactionHistory.add(tx);
            throw new InsufficientFundsException(amount, balance);
        }
        balance = balance.subtract(amount);
        Transaction tx = Transaction.success(accountId, TransactionType.WITHDRAWAL, amount, balanceBefore, balance);
        transactionHistory.add(tx);
        return tx;
    }
    
    public Transaction sendTransfer(BigDecimal amount) {
        return withdraw(amount);
    }

    public Transaction receiveTransfer(BigDecimal amount) {
        return deposit(amount);
    }

    public void applyMonthlyProcessing() {
        // Default implementation does nothing. Override in SavingsAccount.
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactionHistory() {
        return List.copyOf(transactionHistory);
    }

    public List<Transaction> getTransactionHistory(LocalDateTime from, LocalDateTime to) {
        return transactionHistory.stream()
                .filter(tx -> !tx.getTimestamp().isBefore(from) && !tx.getTimestamp().isAfter(to))
                .toList();
    }

    protected void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    /**
     * Rolls back a previously recorded transaction by removing it from history and restoring the balance.
     *
     * <p>This is intended for internal rollback use (e.g., when a transfer debit succeeds but the credit fails).</p>
     */
    public void rollbackTransaction(Transaction transaction) {
        if (transaction == null) {
            return;
        }

        if (transactionHistory.remove(transaction)) {
            balance = transaction.getBalanceBefore();
        }
    }
}
