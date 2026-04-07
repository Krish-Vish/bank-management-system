package bank.model;

import java.math.BigDecimal;

import bank.exception.*;

public class SavingsAccount extends Account {
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("100.00");
    private static final int MAX_WITHDRAWALS_PER_MONTH = 5;
    private static final BigDecimal MONTHLY_INTEREST_RATE = new BigDecimal("0.02");

    private int withdrawalCount;

    public SavingsAccount(String accountId, String accountHolderName, BigDecimal initialBalance) {
        super(accountId, accountHolderName, initialBalance);
        this.withdrawalCount = 0;
    }

    @Override
    public Transaction withdraw(BigDecimal amount) {
        BigDecimal balanceBefore = balance;

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            Transaction tx = Transaction.failed(accountId, TransactionType.WITHDRAWAL, amount, balanceBefore, "Withdrawal amount must be greater than zero.");
            transactionHistory.add(tx);
            throw InvalidTransactionException.nonPositiveAmount(amount);
        }
        
        if(withdrawalCount >= MAX_WITHDRAWALS_PER_MONTH) {
            Transaction tx = Transaction.failed(accountId, TransactionType.WITHDRAWAL, amount, balanceBefore, "Maximum number of withdrawals reached for the month.");
            transactionHistory.add(tx);
            throw new WithdrawalLimitExceededException(MAX_WITHDRAWALS_PER_MONTH);
        }

        if (balance.subtract(amount).compareTo(MINIMUM_BALANCE) < 0) {
            Transaction tx = Transaction.failed(accountId, TransactionType.WITHDRAWAL, amount, balanceBefore, "Withdrawal would cause balance to drop below minimum required balance of $" + MINIMUM_BALANCE);
            transactionHistory.add(tx);
            throw new MinimumBalanceViolationException(amount, balance, MINIMUM_BALANCE);
        }

        Transaction tx = super.withdraw(amount);
        if(tx.getStatus() == TransactionStatus.SUCCESS) {
            withdrawalCount++;
        }

        return tx;
    }

    @Override
    public void applyMonthlyProcessing() {
        BigDecimal balanceBefore = balance;
        BigDecimal interest = balance.multiply(MONTHLY_INTEREST_RATE);

        balance = balance.add(interest);

        Transaction tx = Transaction.success(accountId, TransactionType.DEPOSIT, interest, balanceBefore, balance);
        transactionHistory.add(tx);

        withdrawalCount = 0;
    }
}