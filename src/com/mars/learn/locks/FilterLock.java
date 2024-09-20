package com.mars.learn.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;



// https://en.m.wikipedia.org/wiki/Peterson%27s_algorithm
// https://compas.cs.stonybrook.edu/~nhonarmand/courses/fa17/cse306/slides/11-locks.pdf
// https://medium.com/furkankamaci/implementing-filter-and-bakery-locks-in-java-234cbf545354
public class FilterLock {
    private int n = 4;

    //First I use volatile for the array type to manage share memory, but Java volatile doesn't work on array element
    // but only on the array reference
    //private volatile int[] level = new int[N];
    private AtomicInteger[] level;
    private AtomicInteger[] lastToEnter;


    public FilterLock(int n) {
        this.n = n;
        level = new AtomicInteger[n];
        lastToEnter = new AtomicInteger[n];
        for (int i = 0; i < n; i++) {
            level[i] = new AtomicInteger();
            lastToEnter[i] = new AtomicInteger();
        }
    }

    /**
     * Exclusion : The level array is how mutual exclusion is enforce.
     * Stravation-fee: As long thread can be moved up into the critical section level, every thread at some point should reach N-1 level
     * Deadlock-fee: LastToEnter make sure 2 threads won't be block on the wait section + the stravation-fee property
     */
    public void lock() {

       int id = (int) Thread.currentThread().threadId();
        for(int l = 0;  l < n; l++) {
            //Doorway section
            level[id].set(l); //Save at which level the thread trying to acquire the lock is.
            lastToEnter[l].set(id); //Save which thread enter the last at the specific level


            for(int k = 0; k < n; k++) //K is other thread ID
            {
                //Waiting section
                //Those conditions check :
                    // k != id - Make sure we not check the current thread
                    // lastToEnter[l] == id - Make sure the current thread is the last one to enter the current level
                    // level[k] >= l - Make sure other thread K are not at a superior level, if the case the current thread K should wait at this level
                while((k != id) && lastToEnter[l].get() == id && level[k].get() >= l) {
                    //Spin wait
                    }
            }
        }
    }


    public void unlock() {
        int id = (int) Thread.currentThread().threadId();
        level[id].set(0);
    }

}
