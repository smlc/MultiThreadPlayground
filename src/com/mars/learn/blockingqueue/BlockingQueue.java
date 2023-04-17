package com.mars.learn.blockingqueue;

public class BlockingQueue<T> {

    T[] array;
    int size = 0;
    int capacity;
    int head = 0;
    int tail = 0;

    private final Object lock = new Object();

    public BlockingQueue(int capacity) {
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
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
                    q.enqueue(i);
                    System.out.println("enqueued " + i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread firstDequeued = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 0; i < 25; i++) {
                    System.out.println("Thread 2 dequeued: " + q.dequeue());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread secondDequeued = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 0; i < 25; i++) {
                    System.out.println("Thread 3 dequeued: " + q.dequeue());
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