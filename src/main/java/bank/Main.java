package bank;

import bank.model.*;
import bank.service.Bank;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Bank Application!");

        Bank bank = new Bank("MyBank");

        // Create 4 accounts
        String aliceSavingsId = bank.openAccount("Alice", AccountType.SAVINGS, new BigDecimal("1000.00"));
        String bobCheckingId = bank.openAccount("Bob", AccountType.CHECKING, new BigDecimal("500.00"));
        String carolSavingsId = bank.openAccount("Carol", AccountType.SAVINGS, new BigDecimal("1200.00"));
        String daveCheckingId = bank.openAccount("Dave", AccountType.CHECKING, new BigDecimal("750.00"));

        Account aliceSavings = bank.getAccount(aliceSavingsId);
        Account bobChecking = bank.getAccount(bobCheckingId);
        Account carolSavings = bank.getAccount(carolSavingsId);
        Account daveChecking = bank.getAccount(daveCheckingId);

        // Alice: 5 transactions (1 failed)
        safe("Alice deposit $200", () -> aliceSavings.deposit(new BigDecimal("200.00")));
        safe("Alice withdraw $150", () -> aliceSavings.withdraw(new BigDecimal("150.00")));
        safe("Alice deposit $350", () -> aliceSavings.deposit(new BigDecimal("350.00")));
        safe("Alice withdraw $50", () -> aliceSavings.withdraw(new BigDecimal("50.00")));
        safe("Alice withdraw $2500", () -> aliceSavings.withdraw(new BigDecimal("2500.00"))); // Should fail due to insufficient funds

        // Bob: 5 transactions (2 failed)
        safe("Bob deposit $300", () -> bobChecking.deposit(new BigDecimal("300.00")));
        safe("Bob withdraw $100", () -> bobChecking.withdraw(new BigDecimal("100.00")));
        safe("Bob withdraw $800", () -> bobChecking.withdraw(new BigDecimal("800.00"))); // Should fail due to insufficient funds
        safe("Bob deposit $150", () -> bobChecking.deposit(new BigDecimal("150.00")));
        safe("Bob withdraw $200", () -> bobChecking.withdraw(new BigDecimal("200.00")));

        // Carol: 5 transactions (1 failed)
        safe("Carol withdraw $100", () -> carolSavings.withdraw(new BigDecimal("100.00")));
        safe("Carol deposit $400", () -> carolSavings.deposit(new BigDecimal("400.00")));
        safe("Carol withdraw $200", () -> carolSavings.withdraw(new BigDecimal("200.00")));
        safe("Carol withdraw $2000", () -> carolSavings.withdraw(new BigDecimal("2000.00"))); // Should fail due to insufficient funds
        safe("Carol deposit $50", () -> carolSavings.deposit(new BigDecimal("50.00")));

        // Dave: 5 transactions (1 failed)
        safe("Dave deposit $500", () -> daveChecking.deposit(new BigDecimal("500.00")));
        safe("Dave withdraw $300", () -> daveChecking.withdraw(new BigDecimal("300.00")));
        safe("Dave withdraw $600", () -> daveChecking.withdraw(new BigDecimal("600.00"))); // Should fail due to insufficient funds
        safe("Dave deposit $250", () -> daveChecking.deposit(new BigDecimal("250.00")));
        safe("Dave withdraw $100", () -> daveChecking.withdraw(new BigDecimal("100.00")));

        // Transfer between Alice and Bob
        System.out.println("\nPerforming transfer: Alice -> Bob ($100)");
        safe("Transfer Alice -> Bob $100", () -> bank.transfer(aliceSavingsId, bobCheckingId, new BigDecimal("100.00")));

        // Print monthly statements
        bank.generateMonthlyStatement(aliceSavingsId);
        bank.generateMonthlyStatement(bobCheckingId);
        bank.generateMonthlyStatement(carolSavingsId);
        bank.generateMonthlyStatement(daveCheckingId);
    }

    private static void safe(String description, Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            System.out.println("[FAILED] " + description + ": " + e.getMessage());
        }
    }
}
