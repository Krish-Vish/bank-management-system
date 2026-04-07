package bank.exception;

public class AccountNotFoundException extends BankException {
    private final String accountId;

    public AccountNotFoundException(String accountId) {
        super("Account Not Found: " + accountId);
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }
}