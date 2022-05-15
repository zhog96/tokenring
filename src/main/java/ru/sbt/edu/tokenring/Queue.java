package ru.sbt.edu.tokenring;

public interface Queue {
    void enq(Package value) throws InterruptedException;
    Package deq() throws InterruptedException;
}
