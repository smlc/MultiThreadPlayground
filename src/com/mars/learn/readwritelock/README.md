#Read and Write lock


Do I need two separate lock, one for read and another one for write ?
No only one reentrant lock is enough, using 2 different lock for read and write will bring us to illegal monitor state
because the different thread may try to unlock while the monitor is own by another thread. 

Why would you need a read and write lock ?


How do you make sure the thread who own the lock, will unlock it ?
Use one lock will 2 condition and the thread owning the monitor should be able to unlock it.

## Ressources : 
- https://en.wikipedia.org/wiki/Readers%E2%80%93writer_lock