package bank.exception;
 
import java.math.BigDecimal;

public class MinimumBalanceViolationException extends BankException {
    private final BigDecimal attemptedAmount;
    private final BigDecimal availableBalance;
    private final BigDecimal minimumBalance;

    public MinimumBalanceViolationException(BigDecimal attemptedAmount, BigDecimal availableBalance, BigDecimal minimumBalance) {
        super("Minimum balance violation: attempted " + attemptedAmount + ", available " + availableBalance + ", minimum required " + minimumBalance);
        this.attemptedAmount = attemptedAmount;
        this.availableBalance = availableBalance;
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getAttemptedAmount() {
        return attemptedAmount;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }
}