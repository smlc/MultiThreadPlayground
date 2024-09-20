# Locks

### What is the main difference in the approach used by the Filter lock algorithm compared to the Bakery lock algorithm for ensuring mutual exclusion?

The Filter lock algorithm use an array of level, where the thread at level N-1 will have access to the critical section. Only one thread can be at N-1 which assure mutual exclusion.
The Bakery algorithm use a ticketing system, where each thread will have a number in the order they arrive to the doorway section. The thread with the lower number then have access to the critical section.


### In the Filter lock algorithm, what does the "level" represent?
It represent the waiting room the thread have to go through before accessing the critical section.