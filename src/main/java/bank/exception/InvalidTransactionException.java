package bank.exception;

import java.math.BigDecimal;

public class InvalidTransactionException extends BankException {

    public InvalidTransactionException(String message) {
        super(message);
    }

    public static InvalidTransactionException nonPositiveAmount(BigDecimal amount) {
        return new InvalidTransactionException("Invalid transaction: amount must be greater than zero. Attempted: " + amount);
    }

    public static InvalidTransactionException feeTooHigh(BigDecimal amount, BigDecimal fee) {
        return new InvalidTransactionException("Invalid transaction: fee of " + fee + " exceeds or equals the amount of " + amount);
    }
}   