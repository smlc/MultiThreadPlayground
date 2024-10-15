# Locks

### What is the main difference in the approach used by the Filter lock algorithm compared to the Bakery lock algorithm for ensuring mutual exclusion?

The Filter lock algorithm use an array of level, where the thread at level N-1 will have access to the critical section. Only one thread can be at N-1 which assure mutual exclusion.
The Bakery algorithm use a ticketing system, where each thread will have a number in the order they arrive to the doorway section. The thread with the lower number then have access to the critical section.


### In the Filter lock algorithm, what does the "level" represent?
It represents the waiting room the thread have to go through before accessing the critical section.

### Explain how the Filter lock algorithm achieves mutual exclusion.
The algorithm keep each thread at a different level, and only one thread will move to the critical section level. 
At each level threads will try to eliminate each other until only one remain. 

### How does the Bakery lock algorithm handle the scenario when two threads get the same ticket number?
First, it compares the ticket numbers. If they are equal, it then compares the thread IDs. 
The thread with the smaller ID among those with equal ticket numbers gets priority.

### Doorway
Why do we need to define a doorway section, and why cannot we  define FCFS (First come first serve) in a mutual exclusion algorithm based on the 
order in which the first  instruction in the lock() method was executed? Argue your answer in a case-bycase manner 
based on the nature of the first instruction executed by the lock():  
a read or a write, to separate locations or the same location.

We need to defined a doorway section to create precedence order, the doorway section should consist in a number of limited steps
in that way the first thread executing the instruction first can't be caught by another thread.
FCFS cannot be easily define because the thread could concurrently execute the doorway section, then we will need a way
to know who come first.




