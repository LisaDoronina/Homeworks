package com.mipt.elizavetadoronina.multithreading;

public class Bank {

  public void sendToAccountDeadlock(BankAccount from, BankAccount to, int amount) {
    synchronized (from) {
      System.out.println(Thread.currentThread().getName() + " locked account " + from.getId());

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      synchronized (to) {
        System.out.println(Thread.currentThread().getName() + " locked account " + to.getId());

        if (from.getBalance() < amount) {
          throw new IllegalArgumentException("Insufficient funds");
        }

        from.withdraw(amount);
        to.deposit(amount);

        System.out.println(Thread.currentThread().getName() + " transferred " + amount +
                " from account " + from.getId() + " to account " + to.getId());
      }
    }
  }

  public void sendToAccount(BankAccount from, BankAccount to, int amount) {
    if (from == null || to == null) {
      throw new IllegalArgumentException("Accounts cannot be null");
    }

    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    BankAccount firstLock = from.getId() < to.getId() ? from : to;
    BankAccount secondLock = from.getId() < to.getId() ? to : from;

    synchronized (firstLock) {
      synchronized (secondLock) {
        if (from.getBalance() < amount) {
          throw new IllegalArgumentException("Insufficient funds in account " + from.getId());
        }

        from.withdraw(amount);
        to.deposit(amount);

        System.out.println(Thread.currentThread().getName() + " transferred " + amount +
                " from account " + from.getId() + " to account " + to.getId());
      }
    }
  }
}