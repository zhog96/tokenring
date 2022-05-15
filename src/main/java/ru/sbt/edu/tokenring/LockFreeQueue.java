package ru.sbt.edu.tokenring;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue implements Queue {
    private final AtomicReference<Node> head, tail;

    public LockFreeQueue() {
        Node node = new Node(null);
        head = new AtomicReference<>(node);
        tail = new AtomicReference<>(node);
    }

    private static class Node {
        public final Package value;
        public final AtomicReference<Node> next;
        public Node(Package value) {
            this.value = value;
            next = new AtomicReference<>(null);
        }
    }

    @Override
    public void enq(Package value) {
        Node node = new Node(value);
        while (true) {
            Node last = tail.get();
            Node next = last.next.get();
            if (last == tail.get()) {
                if (next == null) {
                    if (last.next.compareAndSet(null, node)) {
                        tail.compareAndSet(last, node);
                        return;
                    }
                } else {
                    tail.compareAndSet(last, next);
                }
            }
        }
    }

    @Override
    public Package deq() {
        while (true) {
            Node first = head.get();
            Node last = tail.get();
            Node next = first.next.get();
            if (first == head.get()) {
                if (first == last) {
                    if (next == null) {
                        continue;
                    }
                    tail.compareAndSet(last, next);
                } else {
                    Package value = next.value;
                    if (head.compareAndSet(first, next)) {
                        return value;
                    }
                }
            }
        }
    }
}
