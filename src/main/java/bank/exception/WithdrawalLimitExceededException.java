package bank.exception;

public class WithdrawalLimitExceededException extends BankException {
    public WithdrawalLimitExceededException(int limit) {
        super("Monthly withdrawal limit exceeded: maximum " + limit + " withdrawals allowed per month.");
    }
}