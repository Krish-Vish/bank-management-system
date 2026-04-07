# 🏦 Bank Account Management System

A robust and extensible **Bank Account Management System** built in Java, designed to simulate real-world banking operations with clean architecture and strong object-oriented design principles.

---

## 🚀 Features

### 🧾 Account Types

* **Checking Account**

  * No minimum balance
  * First 10 transactions/month are free
  * $2.50 fee per transaction after limit

* **Savings Account**

  * Minimum balance: $100
  * Earns **2% monthly interest**
  * Maximum **5 withdrawals per month**

---

### 💸 Supported Transactions

* **Deposit**
* **Withdrawal**
* **Transfer (between accounts)**

Each transaction records:

* Unique transaction ID
* Timestamp
* Transaction type
* Amount
* Balance before & after
* Status (SUCCESS / FAILED)
* Failure reason (if any)

---

## 🧠 System Design Highlights

* **Object-Oriented Design**

  * Abstract `Account` class with polymorphic behavior
  * Specialized account types (`SavingsAccount`, `CheckingAccount`)

* **Encapsulation & Validation**

  * Business rules enforced within domain models
  * Custom exception handling for edge cases

* **Transaction Integrity**

  * Every operation results in a `Transaction` object
  * Ensures traceability and auditability

* **Extensible Architecture**

  * Easy to add new account types or transaction rules

---

## 📂 Project Structure

```
src/main/java/bank
│
├── model
│   ├── Account.java
│   ├── SavingsAccount.java
│   ├── CheckingAccount.java
│   ├── Transaction.java
│   ├── TransactionType.java
│   └── TransactionStatus.java
│
├── service
│   └── Bank.java
│
├── exception
│   ├── BankException.java
│   ├── InsufficientFundsException.java
│   ├── MinimumBalanceViolationException.java
│   ├── WithdrawalLimitExceededException.java
│   └── InvalidTransactionException.java
│
└── Main.java
```

---

## ⚙️ How to Run

### Prerequisites

* Java 8+
* Maven

### Steps

```bash
git clone https://github.com/<your-username>/bank-management-system.git
cd bank-management-system
mvn clean install
mvn exec:java -Dexec.mainClass="bank.Main"
```

---

## 🧪 Example Scenarios

* Deposit money into account
* Withdraw with insufficient balance → throws exception
* Transfer between accounts
* Savings withdrawal limit enforcement
* Monthly transaction fee for checking accounts

---

## ⚡ Future Improvements

* Add REST APIs (Spring Boot)
* Persistent storage (PostgreSQL / MongoDB)
* Authentication & user roles
* Transaction history UI (React)
* Concurrency handling for multi-user access

