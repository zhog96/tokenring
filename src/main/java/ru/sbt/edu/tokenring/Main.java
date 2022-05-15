package ru.sbt.edu.tokenring;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        TokenRing ring = new TokenRing(
                20,
                () -> new Node(
                        new LockQueue<>(10, new ReentrantLock(), new ReentrantLock()),
                        queue -> {
                            try {
                                for (int i = 0; i < 5; i++)
                                    queue.enq(new Package("123123123"));
                            } catch (InterruptedException ignored) {

                            }
                        }
                )
        );
        Thread thread = new Thread(ring);
        thread.start();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }
}
