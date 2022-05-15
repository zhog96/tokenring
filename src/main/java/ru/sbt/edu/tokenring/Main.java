package ru.sbt.edu.tokenring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        int n = 16, cap = 50000;
        List<Node> nodes = new ArrayList<>();
        for (int idx = 0; idx < n; idx++) {
            nodes.add(
                    new Node(
                            // new LockQueue(cap, new ReentrantLock(), new ReentrantLock()),
                            // new LockFreeQueue(),
                            // new ConcurrentLinkedQueueAdapter(new ConcurrentLinkedQueue<>()),
                            new ArrayBlockingQueueAdapter(new ArrayBlockingQueue<>(cap)),
                            1_000,
                            idx == 0 ? queue -> {
                                try {
                                    for (int i = 0; i < cap; i++)
                                        queue.enq(new Package("123123123"));
                                } catch (InterruptedException ignored) {

                                }
                            } : queue -> {}
                    )
            );
        }
        TokenRing ring = new TokenRing(n, nodes);
        Thread thread = new Thread(ring);
        thread.start();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long latency = 0;
        long sendCount = 0;
        long sumTime = 0;
        for (var node : nodes) {
            latency += node.getLatency();
            sendCount += node.getSendCount();
            sumTime += node.getSumTime();
        }
        System.out.println(latency / sendCount);
        System.out.println(sumTime / sendCount);
        System.out.println((sendCount + 0.0) / sumTime * 1_000_000_000);
    }
}
