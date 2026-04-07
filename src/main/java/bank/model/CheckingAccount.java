package bank.model;

import java.math.BigDecimal;

import bank.exception.*;

public class CheckingAccount extends Account {
    private static final int FREE_TRANSACTIONS_PER_MONTH = 10;
    private static final BigDecimal TRANSACTION_FEE = new BigDecimal("2.50");

    private int transactionCount;

    public CheckingAccount(String accountId, String accountHolderName, BigDecimal initialBalance) {
        super(accountId, accountHolderName, initialBalance);
        this.transactionCount = 0;
    }

    @Override
    public Transaction deposit(BigDecimal amount) {
        BigDecimal fee = (transactionCount < FREE_TRANSACTIONS_PER_MONTH) ? BigDecimal.ZERO : TRANSACTION_FEE;
        BigDecimal balanceBefore = balance;

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            Transaction tx = Transaction.failed(accountId, TransactionType.DEPOSIT, amount, balanceBefore, "Deposit amount must be greater than zero.");
            addTransaction(tx);
            throw InvalidTransactionException.nonPositiveAmount(amount);
        }

        BigDecimal netAmount = amount.subtract(fee);
        if (netAmount.compareTo(BigDecimal.ZERO) <= 0) {
            Transaction tx = Transaction.failed(accountId, TransactionType.DEPOSIT, amount, balanceBefore, "Deposit amount must exceed fee.");
            addTransaction(tx);
            throw InvalidTransactionException.feeTooHigh(amount, fee);
        }

        balance = balance.add(netAmount);
        Transaction tx = Transaction.success(accountId, TransactionType.DEPOSIT, amount, balanceBefore, balance);
        addTransaction(tx);

        if (tx.getStatus() == TransactionStatus.SUCCESS) {
            transactionCount++;
        }

        return tx;
    }

    @Override
    public Transaction withdraw(BigDecimal amount) {
        BigDecimal fee = (transactionCount < FREE_TRANSACTIONS_PER_MONTH) ? BigDecimal.ZERO : TRANSACTION_FEE;
        BigDecimal balanceBefore = balance;

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            Transaction tx = Transaction.failed(accountId, TransactionType.WITHDRAWAL, amount, balanceBefore, "Withdrawal amount must be greater than zero.");
            addTransaction(tx);
            throw InvalidTransactionException.nonPositiveAmount(amount);
        }

        BigDecimal netAmount = amount.add(fee);
        if (netAmount.compareTo(balance) > 0) {
            Transaction tx = Transaction.failed(accountId, TransactionType.WITHDRAWAL, amount, balanceBefore, "Insufficient funds for withdrawal.");
            addTransaction(tx);
            throw new InsufficientFundsException(amount, balance);
        }

        balance = balance.subtract(netAmount);
        Transaction tx = Transaction.success(accountId, TransactionType.WITHDRAWAL, amount, balanceBefore, balance);
        addTransaction(tx);

        if (tx.getStatus() == TransactionStatus.SUCCESS) {
            transactionCount++;
        }
        return tx;
    }

    @Override
    public void applyMonthlyProcessing() {
        transactionCount = 0;
    }
}