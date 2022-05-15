package ru.sbt.edu.tokenring;

import java.util.ArrayList;
import java.util.List;

public class TokenRing implements Runnable {
    private final List<Node> nodes;

    public TokenRing(int n, List<Node> nodes) {
        this.nodes = nodes;
        for (int i = 0; i < n; i++)
            nodes.get(i).setNext(nodes.get((i + 1) % n));
    }

    @Override
    public void run() {
        List<Thread> threads = new ArrayList<>();
        try {
            for (var node : nodes)
                threads.add(new Thread(node));
            for (var thread : threads)
                thread.start();
            while (!Thread.currentThread().isInterrupted()) Thread.onSpinWait();
        } finally {
            for (var thread : threads)
                thread.interrupt();
            for (var thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
