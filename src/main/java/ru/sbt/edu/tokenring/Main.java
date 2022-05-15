package ru.sbt.edu.tokenring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        for (var queueGetter : Arrays.asList(
                (QueueGetter) (n, p) -> new LockQueue(p, new ReentrantLock(), new ReentrantLock()),
                (QueueGetter) (n, p) -> new ArrayBlockingQueueAdapter(new ArrayBlockingQueue<>(p)),
                (QueueGetter) (n, p) -> new LockFreeQueue()
        )) {
            System.out.println(test(queueGetter));
        }
    }

    private static class Result {
        public final double latency;
        public final double throughput;
        public final int n;
        public final int p;

        public Result(double latency, double throughput, int n, int p) {
            this.latency = latency;
            this.throughput = throughput;
            this.n = n;
            this.p = p;
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "(%.10f, %.10f, %d, %d)", latency, throughput, n, p);
        }
    }

    private interface QueueGetter {
        Queue get(int n, int p);
    }

    private static List<Result> test(QueueGetter queueGetter) {
        List<Result> results = new ArrayList<>();
        for (var n : Arrays.asList(2, 4, 8, 16, 32)) {
        for (var p : Arrays.asList(1, 16, 16 * 16, 16 * 16 * 16, 16 * 16 * 16 * 16)) {
                results.add(test(queueGetter.get(n, p), n, p));
            }
        }
        return results;
    }

    private static Result test(Queue queue, int n, int p) {
        List<Node> nodes = new ArrayList<>();
        for (int idx = 0; idx < n; idx++) {
            nodes.add(
                    new Node(
                            queue,
                            10_000,
                            idx == 0 ? q -> {
                                try {
                                    for (int i = 0; i < p; i++)
                                        q.enq(new Package("123123123"));
                                } catch (InterruptedException ignored) {

                                }
                            } : q -> {}
                    )
            );
        }
        TokenRing ring = new TokenRing(n, nodes);
        Thread thread = new Thread(ring);
        thread.start();
        try {
            TimeUnit.SECONDS.sleep(5);
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
        return new Result(
                latency / (sendCount + 0.0) / 1_000_000_000,
                (sendCount + 0.0) / sumTime * 1_000_000_00,
                n, p
        );
    }
}
