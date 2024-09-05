package com.mars.learn.readwritelock;

import com.mars.learn.ratelimit.RateLimitTokenBucket;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteLock {

    Lock writeLock = new ReentrantLock();
    Condition writting = writeLock.newCondition();
    Lock readLock = new ReentrantLock();

    private int numbersOfReader = 0;
    public void acquireReadLock() {
        readLock.lock();
        numbersOfReader++;
        if(numbersOfReader == 1) writeLock.lock();
        readLock.unlock();
    }

    public void releaseReadLock() {
        readLock.lock();
        numbersOfReader--;
        if(numbersOfReader == 0) writeLock.unlock();
        readLock.unlock();

    }

    public void acquireWriteLock() {
        writeLock.lock();
    }
    public void releaseWriteLock() {
        writeLock.unlock();
    }


    public static void main(String args[]) throws Exception {

        final ReadWriteLock rwl = new ReadWriteLock();

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

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

                System.out.println("Attempting to acquire write lock in t2: " + System.currentTimeMillis());
                rwl.acquireWriteLock();
                System.out.println("write lock acquired t2: " + System.currentTimeMillis());

            }
        });

        Thread tReader1 = new Thread(new Runnable() {

            @Override
            public void run() {

                rwl.acquireReadLock();
                System.out.println("Read lock acquired: " + System.currentTimeMillis());

            }
        });

        Thread tReader2 = new Thread(new Runnable() {

            @Override
            public void run() {
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
