package bank.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends BankException {
    private final BigDecimal attemptedAmount;
    private final BigDecimal availableBalance;

    public InsufficientFundsException(BigDecimal attemptedAmount, BigDecimal availableBalance) {
        super("Insufficient funds: attempted " + attemptedAmount + ", available " + availableBalance);
        this.attemptedAmount = attemptedAmount;
        this.availableBalance = availableBalance;
    }

    public BigDecimal getAttemptedAmount() {
        return attemptedAmount;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
}