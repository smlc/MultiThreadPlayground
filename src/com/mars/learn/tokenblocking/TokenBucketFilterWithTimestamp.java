package com.mars.learn.tokenblocking;

public class TokenBucketFilterWithTimestamp {

    private static final int MAX_TOKEN = 10;
    private volatile int usedPermits = MAX_TOKEN;


    /**
     * This is the starting point when we create the tokenBuckerFilter
     */
    private long lastTimeTokenRequest = System.currentTimeMillis();

    public synchronized void getToken() throws InterruptedException {

        usedPermits += (System.currentTimeMillis() - lastTimeTokenRequest) / 1000;

        if(usedPermits > MAX_TOKEN) {
            usedPermits = MAX_TOKEN;
        }
        if(usedPermits == 0) {
            //Waiting for a new token to be available, as all the other thread
            // will wait the current thread will take the token after sleep end.
            Thread.sleep(1000);
        } else {
            usedPermits--;
        }

        lastTimeTokenRequest = System.currentTimeMillis();
    }

    public static void main( String args[] ) throws Exception{
        final TokenBucketFilterWithTimestamp tokenBucketFilter = new TokenBucketFilterWithTimestamp();

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
