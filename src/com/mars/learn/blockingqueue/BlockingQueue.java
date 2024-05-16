package com.mars.learn.blockingqueue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {

    T[] array;
    int size = 0;
    int capacity;
    int head = 0;
    int tail = 0;

    Lock reentrantLock = new ReentrantLock();
    private final Condition queueFull = reentrantLock.newCondition();
    private final Condition queueEmpty = reentrantLock.newCondition();
    private final Object lock = new Object();

    public BlockingQueue(int capacity) {
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enqueueReentrantLock(T item) throws InterruptedException {
        // How the synchronized (lock)  should be replace ? Use lock() function
        reentrantLock.lock();
        try {

            while(size == capacity) {
                //Queue is full wait before enqueue new item, how do we wait using reentrantLock ?
                // We need a condition variable from the reentrantLock
                queueFull.await();
            }

            if(tail == capacity) {
                tail = 0;
            }
            //Enqueue item
            array[tail] = item;
            tail++;
            size++;

            //How do I notify other block thread ?
            // Use signalAll to notify that the queue is not empty anymore.
            queueEmpty.signalAll();
        } finally {
            //unlock in any case
            reentrantLock.unlock();
        }
    }

    public T dequeueReentrantLock() throws InterruptedException {

        reentrantLock.lock();
        T returnItem = null;
        try {

            while(size == 0) {
                //The queue is empty let's wait signal, that a new item was added
                queueEmpty.await();
            }

            if(head == capacity) {
                head = 0;
            }

            returnItem = array[head];
            array[head] = null;
            head++;
            size--;

            //Notify thread waiting for the queue not being full anymore.
            queueFull.signalAll();
            return returnItem;
        } finally {
            //unlock in any case
            reentrantLock.unlock();
        }
    }

    public void enqueue(T item) throws InterruptedException {


        synchronized (lock) {

            while(size == capacity) {
                lock.wait();
            }

            if(tail == capacity) {
                tail = 0;
            }
            //Enqueue item
            array[tail] = item;
            tail++;
            size++;

            //Notify blocking consumer that new item available
            lock.notifyAll();
        }
    }

    public T dequeue() throws InterruptedException {
        T returnItem = null;
        synchronized (lock) {

            while(size == 0) {
                lock.wait();
            }
            if(head == capacity) {
                head = 0;
            }
            returnItem = array[head];
            array[head] = null;
            head++;
            size--;
            lock.notifyAll();
        }
        return returnItem;
    }

    public static void main( String args[] ) throws Exception{
        final BlockingQueue<Integer> q = new BlockingQueue<Integer>(5);

        Thread enqueue = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 0; i < 50; i++) {
                    q.enqueueReentrantLock(i);
                    System.out.println("enqueued " + i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread firstDequeued = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 0; i < 25; i++) {
                    System.out.println("Thread 2 dequeued: " + q.dequeueReentrantLock());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread secondDequeued = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 0; i < 25; i++) {
                    System.out.println("Thread 3 dequeued: " + q.dequeueReentrantLock());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        enqueue.start();
        Thread.sleep(4000);
        firstDequeued.start();

        firstDequeued.join();

        secondDequeued.start();
        enqueue.join();
        secondDequeued.join();
    }

}