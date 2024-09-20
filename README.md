# Virtual thread playground.

This is a repo I use to play with Java virtual thread to understand their limitation and advantage.
I will implement some classical multithreading problems using virtual thread and normal thread.


## Blocking queue

To implement a blocking queue we mainly need to block the different thread to not access the queue at the same time. 
So this is mainly a synchronization problem.

The different way to do synchronization of threads in Java are :

- `synchronized`
- `ReentrantLock`
- `ReadWriteLock`
- `Atomic Classes`
- `Semaphore`

### Synchronized and virtual thread

Synchronization made using the `synchronized` keyword can block (pinned) a virtual thread. 
That why if we are using virtual thread we should avoid anything implement using this kind of Synchronization.

Why `synchronized` pinned virtual thread and not ReentrantLock ?
When virtual thread enter a synchronized block the carrier thread will get blocked, it's not the case for ReentrantLock,
because they are not implemented with synchronized.

What does it mean the lock are reentrant ?

Reentrancy means a thread can acquire the same lock it already holds. This is useful when a synchronized method calls another synchronized method of the same object. 
Without reentrancy, such calls would deadlock.

ReentrantLock resources: 
- https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/ReentrantLock.html
- https://stackoverflow.com/a/11821900/7672189
- https://web.mit.edu/6.005/www/fa15/classes/23-locks/

Scheduler resources :
https://akhiilgupta.medium.com/design-a-multi-threaded-task-scheduler-lld-multi-threaded-construct-eb090c5a8727


Ressources : 
- The art of multiprocessor programming : https://www.goodreads.com/book/show/3131525-the-art-of-multiprocessor-programming
- Seven concurrency problem in seven week : https://www.goodreads.com/book/show/18467564-seven-concurrency-models-in-seven-weeks
- The java.util.concurrent Synchronizer Framework, Doug Lea