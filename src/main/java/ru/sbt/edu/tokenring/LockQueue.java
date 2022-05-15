package ru.sbt.edu.tokenring;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class LockQueue<T> implements Queue<T> {
    private final Lock enqLock;
    private final Lock deqLock;
    private final Condition notEmptyCondition;
    private final Condition notFullCondition;
    private final AtomicInteger size;
    private volatile Node head, tail;
    private final int capacity;

    private class Node {
        public T value;
        public volatile Node next;
        public Node(T x) {
            value = x;
            next = null;
        }
    }

    public LockQueue(int capacity, Lock enqLock, Lock deqLock) {
        this.capacity = capacity;
        head = new Node(null);
        tail = head;
        size = new AtomicInteger(0);
        this.enqLock = enqLock;
        notFullCondition = enqLock.newCondition();
        this.deqLock = deqLock;
        notEmptyCondition = deqLock.newCondition();
    }

    @Override
    public void enq(T x) throws InterruptedException {
        boolean mustWakeDequeuers = false;
        Node node = new Node(x);
        enqLock.lock();
        try {
            while (size.get() == capacity)
                notFullCondition.await();
            tail.next = node;
            tail = node;
            if (size.getAndIncrement() == 0)
                mustWakeDequeuers = true;
        } finally {
            enqLock.unlock();
        }
        if (mustWakeDequeuers) {
            deqLock.lock();
            try {
                notEmptyCondition.signalAll();
            } finally {
                deqLock.unlock();
            }
        }
    }

    @Override
    public T deq() throws InterruptedException {
        T result;
        boolean mustWakeEnqueuers = false;
        deqLock.lock();
        try {
            while (head.next == null)
                notEmptyCondition.await();
            result = head.next.value;
            head = head.next;
            if (size.getAndDecrement() == capacity) {
                mustWakeEnqueuers = true;
            }
        } finally {
            deqLock.unlock();
        }
        if (mustWakeEnqueuers) {
            enqLock.lock();
            try {
                notFullCondition.signalAll();
            } finally {
                enqLock.unlock();
            }
        }
        return result;
    }
}
