package ru.sbt.edu.tokenring;

public interface Queue<T> {
    void enq(T x) throws InterruptedException;
    T deq() throws InterruptedException;
}
