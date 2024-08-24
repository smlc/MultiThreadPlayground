package com.mars.learn.ratelimit;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimitTokenBucket {


    int capacity;
    int numberOfAvailableToken = 0;

    private long lastRequestTime = System.currentTimeMillis();

    int head = 0;
    Lock reentrantLock = new ReentrantLock();
    public RateLimitTokenBucket(int capacity) {
        this.capacity = capacity;
    }

    public void getToken() throws InterruptedException {
        reentrantLock.lock();
        // Divide by a 1000 to get granularity at the second level.
        numberOfAvailableToken += (System.currentTimeMillis() - lastRequestTime) / 1000;

        if(numberOfAvailableToken > capacity) numberOfAvailableToken = capacity;

        try {

            if(numberOfAvailableToken == 0) {
                Thread.sleep(1000);
            }

            numberOfAvailableToken--;
            lastRequestTime = System.currentTimeMillis();
        } finally {
            //unlock in any case
            reentrantLock.unlock();
        }

        System.out.println("Granting " + Thread.currentThread().getName() + " token at " + (System.currentTimeMillis() / 1000));

    }


    public static void main( String args[] ) throws Exception{
        final RateLimitTokenBucket q = new RateLimitTokenBucket(3);

        Thread tokenConsummer = Thread.ofVirtual().unstarted(() -> {
            try {
                Thread.currentThread().setName("consumer 1");
                for (int i = 0; i < 2; i++) {
                    q.getToken();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread tokenConsummer2 = Thread.ofVirtual().unstarted(() -> {
            try {
                Thread.currentThread().setName("consumer 2");
                for (int i = 0; i < 2; i++) {
                    q.getToken();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread tokenConsummer3 = Thread.ofVirtual().unstarted(() -> {
            try {
                Thread.currentThread().setName("consumer 3");
                for (int i = 0; i < 2; i++) {
                    q.getToken();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        Thread.sleep(4000);
        tokenConsummer.start();
        tokenConsummer2.start();
        tokenConsummer3.start();

        tokenConsummer2.join();
        tokenConsummer.join();
        tokenConsummer3.join();
    }
}
