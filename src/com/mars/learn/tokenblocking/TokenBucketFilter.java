package com.mars.learn.tokenblocking;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TokenBucketFilter {
    private static final int MAX_TOKEN = 10;
    private volatile int usedPermits = MAX_TOKEN;
    private int maxCount;
    public synchronized void getToken() throws InterruptedException {
        while (usedPermits == 0)
            wait();

        notify();
        usedPermits--;

    }

    public void addToken() {
        ScheduledExecutorService executorService = Executors
                .newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() ->{
            System.out.println("scheduleAtFixedRate");
            if(usedPermits <= MAX_TOKEN) {
                System.out.println("Adding new token");
                usedPermits++;
                synchronized (this) {
                    notify();
                }
            }
        }, 1,1, TimeUnit.SECONDS);
    }

    public static void main( String args[] ) throws Exception{
        final TokenBucketFilter tokenBucketFilter = new TokenBucketFilter();

        //Start adding token periodically
        tokenBucketFilter.addToken();
        Thread firstTokenConsumer = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                   tokenBucketFilter.getToken();
                    System.out.println("FirstTokenConsumer token " + i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread secondTokenConsumer = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    tokenBucketFilter.getToken();
                    System.out.println("SecondTokenConsumer token " + i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        firstTokenConsumer.start();
        secondTokenConsumer.start();

        firstTokenConsumer.join();
        secondTokenConsumer.join();

    }


}
