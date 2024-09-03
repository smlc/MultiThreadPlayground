package com.mars.learn.deferredcallback;

public class Callback {
    private final String message;
    public long executeAt;

    public Callback(long executeAfter, String message) {
        this.executeAt = System.currentTimeMillis() + (executeAfter * 1000);
        this.message = message;
    }

    public void execute() {
        System.out.println(message);
    }



}
