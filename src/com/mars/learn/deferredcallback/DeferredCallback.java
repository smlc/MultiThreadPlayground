package com.mars.learn.deferredcallback;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DeferredCallback {

    PriorityQueue<Callback> queue = new PriorityQueue<Callback>((o1, o2) -> (int) (o1.executeAt - o2.executeAt));

    // Lock to guard critical sections
    ReentrantLock lock = new ReentrantLock();

    // Condition to make execution thread wait on
    Condition newCallbackArrived = lock.newCondition();
    Future<?> future;
    ScheduledExecutorService scheduler;
    // Run by the Executor Thread
    public void start() throws InterruptedException, ExecutionException {

        long sleepFor = 0;
        while(true) {
            lock.lock();
            while(queue.isEmpty()) {
                //The queue is empty wait for the first callback
                newCallbackArrived.await();
            }

            //Check for all callback
            while(!queue.isEmpty()) {
                //Got first callback, check how long we need to wait
                sleepFor = calculateSleepTime();

                if(sleepFor < 0)
                    break;

                newCallbackArrived.await(sleepFor, TimeUnit.MILLISECONDS);

            }

            queue.poll().execute();

            lock.unlock();
        }

    }

    private long calculateSleepTime() {
        long currentTime = System.currentTimeMillis();
        return  queue.peek().executeAt - currentTime;
    }


    public void registerCallback(Callback callback) throws InterruptedException {
        //Do I need to sync this method ?
        // How to add mutex around the add method ?
        // We will use rentrant lock as mutex
        lock.lock();
        queue.add(callback);
        newCallbackArrived.signalAll();
        lock.unlock();

    }

    public static void main( String args[] ) throws Exception{
        final DeferredCallback deferredCallbackExecutor = new DeferredCallback();
        Set<Thread> allThreads = new HashSet<Thread>();

        Thread service = Thread.ofVirtual().unstarted(() -> {
            try {
                deferredCallbackExecutor.start();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        service.start();

        for (int i = 0; i < 10; i++) {
            Thread thread = Thread.ofVirtual().unstarted(() -> {

                Callback cb = new Callback(1, "Hello this is " + Thread.currentThread().getName());
                try {
                    deferredCallbackExecutor.registerCallback(cb);
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
