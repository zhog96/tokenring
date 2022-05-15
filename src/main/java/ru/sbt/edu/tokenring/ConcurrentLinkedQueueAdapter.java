package ru.sbt.edu.tokenring;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueAdapter implements Queue {
    private final ConcurrentLinkedQueue<Package> queue;

    public ConcurrentLinkedQueueAdapter(ConcurrentLinkedQueue<Package> queue) {
        this.queue = queue;
    }

    @Override
    public void enq(Package value) {
        queue.add(value);
    }

    @Override
    public Package deq() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            Package value = queue.poll();
            if (value == null) {
                continue;
            }
            return value;
        }
        throw new InterruptedException();
    }
}
