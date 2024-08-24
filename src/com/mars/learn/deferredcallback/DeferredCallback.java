package com.mars.learn.deferredcallback;

import java.time.Duration;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
        scheduler = Executors.newSingleThreadScheduledExecutor();
        if(queue.isEmpty()) return;

        Callback callback = queue.peek();
        future = scheduler.schedule(() -> {

            callback.execute();
            queue.remove(callback);
        }, callback.executeAt, TimeUnit.SECONDS);

        future.get(); // Wait for the scheduled task to finish
        scheduler.shutdown();

    }

    public void registerCallback(Callback callback) throws InterruptedException {
        //Do I need to sync this method ?
        // How to add mutex around the add method ?
        // We will use rentrant lock as mutex
        lock.lock();
        if(!queue.isEmpty() && (queue.peek().executeAt >  callback.executeAt)) {
            queue.add(callback);
        } else if (!queue.isEmpty() && (queue.peek().executeAt < callback.executeAt)) {

        }
        queue.add(callback);


        newCallbackArrived.signalAll();
        lock.unlock();

    }
}
