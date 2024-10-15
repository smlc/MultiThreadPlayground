package com.mars.learn.readwritelock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteLock {

    Lock lock = new ReentrantLock();
    Condition writting = lock.newCondition();
    Condition reading = lock.newCondition();
    boolean isWriting = false;
    private int numbersOfReader = 0;
    public void acquireReadLock() throws InterruptedException {
        lock.lock();
        while(isWriting)
            reading.await();

        numbersOfReader++;

        lock.unlock();
    }

    public void releaseReadLock() {
        lock.lock();
        numbersOfReader--;
        if (numbersOfReader == 0) {
            writting.signal();
        }
        lock.unlock();

    }

    public void acquireWriteLock() throws InterruptedException {
        lock.lock();
        while (isWriting || numbersOfReader != 0) {
            writting.await();
        }
        isWriting = true;
        lock.unlock();

    }
    public void releaseWriteLock() {
        lock.lock();
        isWriting = false;
        reading.signalAll();
        writting.signal();
        lock.unlock();
    }


    public static void main(String args[]) throws Exception {

        final ReadWriteLock rwl = new ReadWriteLock();

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.currentThread().setName("thread-t1");
                    System.out.println("Attempting to acquire write lock in t1: " + System.currentTimeMillis());
                    rwl.acquireWriteLock();
                    System.out.println("write lock acquired t1: " + +System.currentTimeMillis());

                    // Simulates write lock being held indefinitely
                    for (; ; ) {
                        Thread.sleep(500);
                    }

                } catch (InterruptedException ie) {

                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                Thread.currentThread().setName("thread-t2");
                System.out.println("Attempting to acquire write lock in t2: " + System.currentTimeMillis());
                try {
                    rwl.acquireWriteLock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("write lock acquired t2: " + System.currentTimeMillis());

            }
        });

        Thread tReader1 = new Thread(new Runnable() {

            @Override
            public void run() {
                Thread.currentThread().setName("thread-tReader1");
                try {
                    rwl.acquireReadLock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Read lock acquired: " + System.currentTimeMillis());

            }
        });

        Thread tReader2 = new Thread(new Runnable() {

            @Override
            public void run() {
                Thread.currentThread().setName("thread-tReader2");
                System.out.println("Read lock about to release: " + System.currentTimeMillis());
                rwl.releaseReadLock();
                System.out.println("Read lock released: " + System.currentTimeMillis());
            }
        });

        tReader1.start();
        t1.start();
        Thread.sleep(3000);
        tReader2.start();
        Thread.sleep(1000);
        t2.start();
        tReader1.join();
        tReader2.join();
        t2.join();
    }
}
