package com.mars.learn.deferredcallback;

import java.time.Duration;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DeferredCallback {

    PriorityQueue<Callback> queue = new PriorityQueue<Callback>((o1, o2) -> (int) (o1.executeAt - o2.executeAt));

    // Lock to guard critical sections
    ReentrantLock lock = new ReentrantLock();

    // Condition to make execution thread wait on
    Condition newCallbackArrived = lock.newCondition();

    // Run by the Executor Thread
    public void start() throws InterruptedException {
        while (true) {
            lock.lock();
        }
    }

    public void registerCallback(Callback callback) throws InterruptedException {

        // How to add mutex around the add method ?
        // We will use rentrant lock as mutex
        lock.lock();
        queue.add(callback);
        newCallbackArrived.signal();
        lock.unlock();

    }
}
