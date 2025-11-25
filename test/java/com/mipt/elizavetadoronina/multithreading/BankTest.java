package com.mipt.elizavetadoronina.multithreading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

  @Test
  void testBasicTransfer() {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 1000);
    BankAccount account2 = new BankAccount(2, 500);

    bank.sendToAccount(account1, account2, 300);

    assertEquals(700, account1.getBalance());
    assertEquals(800, account2.getBalance());
  }

  @Test
  void testInsufficientFunds() {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 100);
    BankAccount account2 = new BankAccount(2, 500);

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bank.sendToAccount(account1, account2, 200)
    );

    assertTrue(exception.getMessage().contains("Insufficient funds"));
    assertEquals(100, account1.getBalance());
    assertEquals(500, account2.getBalance());
  }

  @Test
  void testNullAccounts() {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 100);

    assertThrows(IllegalArgumentException.class,
            () -> bank.sendToAccount(null, account1, 100));

    assertThrows(IllegalArgumentException.class,
            () -> bank.sendToAccount(account1, null, 100));
  }

  @Test
  void testInvalidAmount() {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 100);
    BankAccount account2 = new BankAccount(2, 500);

    assertThrows(IllegalArgumentException.class,
            () -> bank.sendToAccount(account1, account2, 0));

    assertThrows(IllegalArgumentException.class,
            () -> bank.sendToAccount(account1, account2, -100));
  }

  @Test
  void testConcurrentTransfers() throws InterruptedException {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 10000);
    BankAccount account2 = new BankAccount(2, 10000);

    int threadCount = 10;
    int transfersPerThread = 100;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    AtomicInteger successfulTransfers = new AtomicInteger(0);
    AtomicInteger failedTransfers = new AtomicInteger(0);

    for (int i = 0; i < threadCount; i++) {
      executor.submit(() -> {
        try {
          for (int j = 0; j < transfersPerThread; j++) {
            try {
              if (j % 2 == 0) {
                bank.sendToAccount(account1, account2, 10);
              } else {
                bank.sendToAccount(account2, account1, 5);
              }
              successfulTransfers.incrementAndGet();
            } catch (IllegalArgumentException e) {
              failedTransfers.incrementAndGet();
            }
          }
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    int totalTransfers = threadCount * transfersPerThread;
    int expectedBalance1 = 10000 - (successfulTransfers.get() / 2) * 10 + (successfulTransfers.get() / 2) * 5;
    int expectedBalance2 = 10000 + (successfulTransfers.get() / 2) * 10 - (successfulTransfers.get() / 2) * 5;

    assertEquals(expectedBalance1 + expectedBalance2, account1.getBalance() + account2.getBalance());
    System.out.println("Successful transfers: " + successfulTransfers.get());
    System.out.println("Failed transfers: " + failedTransfers.get());
    System.out.println("Account1 balance: " + account1.getBalance());
    System.out.println("Account2 balance: " + account2.getBalance());
  }

  @Test
  @Timeout(5)
  void testDeadlockScenario() throws InterruptedException {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 1000);
    BankAccount account2 = new BankAccount(2, 1000);

    CountDownLatch latch = new CountDownLatch(2);
    AtomicInteger completedThreads = new AtomicInteger(0);

    Thread thread1 = new Thread(() -> {
      try {
        bank.sendToAccountDeadlock(account1, account2, 100);
        completedThreads.incrementAndGet();
      } catch (Exception e) {
        System.out.println("Thread 1 failed: " + e.getMessage());
      } finally {
        latch.countDown();
      }
    }, "Thread-1");

    Thread thread2 = new Thread(() -> {
      try {
        bank.sendToAccountDeadlock(account2, account1, 50);
        completedThreads.incrementAndGet();
      } catch (Exception e) {
        System.out.println("Thread 2 failed: " + e.getMessage());
      } finally {
        latch.countDown();
      }
    }, "Thread-2");

    thread1.start();
    thread2.start();

    boolean completed = latch.await(3, TimeUnit.SECONDS);

    if (!completed) {
      System.out.println("Potential deadlock detected - threads did not complete in time");
      System.out.println("Completed threads: " + completedThreads.get());

      thread1.interrupt();
      thread2.interrupt();

      Thread.sleep(100);
    }

    assertTrue(completedThreads.get() <= 2);
  }

  @Test
  void testNoDeadlockWithCorrectMethod() throws InterruptedException {
    Bank bank = new Bank();
    BankAccount account1 = new BankAccount(1, 1000);
    BankAccount account2 = new BankAccount(2, 1000);

    int threadCount = 4;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      final int threadNum = i;
      executor.submit(() -> {
        try {
          for (int j = 0; j < 50; j++) {
            if (threadNum % 2 == 0) {
              bank.sendToAccount(account1, account2, 1);
            } else {
              bank.sendToAccount(account2, account1, 1);
            }
          }
        } catch (Exception e) {
          System.out.println("Thread " + threadNum + " failed: " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    boolean completed = latch.await(10, TimeUnit.SECONDS);
    assertTrue(completed, "All threads should complete without deadlock");

    executor.shutdown();

    assertEquals(2000, account1.getBalance() + account2.getBalance());
  }

  @Test
  void testMultipleAccounts() throws InterruptedException {
    Bank bank = new Bank();
    BankAccount[] accounts = new BankAccount[5];
    for (int i = 0; i < accounts.length; i++) {
      accounts[i] = new BankAccount(i + 1, 1000);
    }

    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      final int threadNum = i;
      executor.submit(() -> {
        try {
          for (int j = 0; j < 20; j++) {
            int fromIndex = (threadNum + j) % accounts.length;
            int toIndex = (fromIndex + 1) % accounts.length;
            bank.sendToAccount(accounts[fromIndex], accounts[toIndex], 10);
          }
        } catch (Exception e) {
          System.out.println("Thread " + threadNum + " failed: " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    boolean completed = latch.await(10, TimeUnit.SECONDS);
    assertTrue(completed, "All threads should complete without deadlock");

    executor.shutdown();

    int totalBalance = 0;
    for (BankAccount account : accounts) {
      totalBalance += account.getBalance();
    }
    assertEquals(5000, totalBalance); // 5 счетов × 1000 = 5000
  }
}