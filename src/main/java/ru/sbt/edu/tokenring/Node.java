package ru.sbt.edu.tokenring;

import org.openjdk.jmh.annotations.*;

public class Node implements Runnable {
    private Node next;
    private final Queue<Package> queue;

    public Node(Queue<Package> queue, NodeInitializer<Package> initializer) {
        this.queue = queue;
        initializer.init(queue);
    }

    public void setNext(Node next) {
        this.next = next;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void sendOne() throws InterruptedException {
        Package x = queue.deq();
        next.queue.enq(x);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                sendOne();
            }
        } catch (InterruptedException ignored) {
        }
    }
}
