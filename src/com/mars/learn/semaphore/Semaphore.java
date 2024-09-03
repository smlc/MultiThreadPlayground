package com.mars.learn.semaphore;

import com.mars.learn.deferredcallback.Callback;
import com.mars.learn.deferredcallback.DeferredCallback;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Semaphore {

    private int maxPermit;
    private int permit;
    ReentrantLock lock = new ReentrantLock();
    Condition waitForPermit = lock.newCondition();
    public Semaphore(int permit) {
        this.maxPermit = permit;
        this.permit = maxPermit;
    }

    public void gainPermit() throws InterruptedException {
        lock.lock();
        while(permit == 0)
            waitForPermit.await();

        permit--;

        lock.unlock();

    }

    public void returnPermit() {
        lock.lock();

        while(permit != maxPermit)
            permit++;

        waitForPermit.signal();
        lock.unlock();
    }

    public static void main( String args[] ) throws Exception{
        final Semaphore semaphore = new Semaphore(2);
        Set<Thread> allThreads = new HashSet<Thread>();



        for (int i = 0; i < 4; i++) {
            Thread thread = Thread.ofVirtual().unstarted(() -> {
                try {
                    semaphore.gainPermit();
                    System.out.println("Gain access thread " + Thread.currentThread().getName());
                    Thread.sleep(Duration.ofSeconds(4));
                    semaphore.returnPermit();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.setName("Thread_" + (i + 1));
            thread.start();
            allThreads.add(thread);
            Thread.sleep(1000);
        }

        for (Thread t : allThreads) {
            t.join();
        }
    }
}
