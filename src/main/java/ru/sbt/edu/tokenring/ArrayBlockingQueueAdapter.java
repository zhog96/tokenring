package ru.sbt.edu.tokenring;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ArrayBlockingQueueAdapter implements Queue {
    private final ArrayBlockingQueue<Package> queue;

    public ArrayBlockingQueueAdapter(ArrayBlockingQueue<Package> queue) {
        this.queue = queue;
    }

    @Override
    public void enq(Package value) {
        queue.add(value);
    }

    @Override
    public Package deq() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            return queue.take();
        }
        throw new InterruptedException();
    }
}
