package bank.model;

import bank.exception.BankException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final String transactionId;
    private final String accountId;
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceBefore;
    private final BigDecimal balanceAfter;
    private final TransactionStatus status;
    private final String failureReason;

    private Transaction(
        String transactionId,
        String accountId,
        LocalDateTime timestamp,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        TransactionStatus status,
        String failureReason
    ) {
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId must not be null");
        this.accountId = Objects.requireNonNull(accountId, "accountId must not be null");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.failureReason = Objects.requireNonNull(failureReason, "failureReason must not be null");

        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(balanceBefore, "balanceBefore must not be null");
        Objects.requireNonNull(balanceAfter, "balanceAfter must not be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankException("amount must be > 0");
        }
        if (balanceBefore.compareTo(BigDecimal.ZERO) < 0) {
            throw new BankException("balanceBefore must be >= 0");
        }
        if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
            throw new BankException("balanceAfter must be >= 0");
        }

        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
    }

    public static Transaction success(String accountId, TransactionType type,
                                        BigDecimal amount,
                                        BigDecimal balanceBefore,
                                        BigDecimal balanceAfter) {
        return new Transaction(
                UUID.randomUUID().toString(),
                accountId,
                LocalDateTime.now(),
                type,
                amount,
                balanceBefore,
                balanceAfter,
                TransactionStatus.SUCCESS,
                ""
        );
    }

    public static Transaction failed(String accountId, TransactionType type,
                                      BigDecimal amount,
                                      BigDecimal balanceBefore,
                                      String failureReason) {
        return new Transaction(
                UUID.randomUUID().toString(),
                accountId,
                LocalDateTime.now(),
                type,
                amount,
                balanceBefore,
                balanceBefore, // balanceAfter is same as balanceBefore for failed transactions
                TransactionStatus.FAILED,
                failureReason
        );
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "transactionId='" + transactionId + '\'' +
            ", accountId='" + accountId + '\'' +
            ", timestamp=" + timestamp +
            ", type=" + type +
            ", amount=" + amount +
            ", balanceBefore=" + balanceBefore +
            ", balanceAfter=" + balanceAfter +
            ", status=" + status +
            ", failureReason='" + failureReason + '\'' +
            '}';
    }
}