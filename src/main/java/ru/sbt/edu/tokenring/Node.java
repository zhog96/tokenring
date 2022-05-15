package ru.sbt.edu.tokenring;

public class Node implements Runnable {
    private Node next;
    private final Queue queue;
    private final long warmUp;
    private long iter;
    private long latency;
    private long sendCount;
    private long startTime;
    private long sumTime;

    public Node(Queue queue, long warmUp, NodeInitializer initializer) {
        this.queue = queue;
        initializer.init(queue);
        this.warmUp = warmUp;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void sendOne() throws InterruptedException {
        Package value = queue.deq();
        long time = System.nanoTime();
        if (iter > warmUp) {
            if (startTime == 0)
                startTime = System.nanoTime();
            sendCount++;
            if (value.getStartTime() != null)
                latency += time - value.getStartTime();
        }
        value.setStartTime(time);
        next.queue.enq(value);
        iter++;
    }

    public long getLatency() {
        return latency;
    }

    public long getSendCount() {
        return sendCount;
    }

    public long getSumTime() {
        return sumTime;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                sendOne();
            }
        } catch (InterruptedException ignored) {
        } finally {
            sumTime = System.nanoTime() - startTime;
        }
    }
}
