package ru.sbt.edu.tokenring;

public class BenchmarkRunner {
    public static void main(String[] args) {
        try {
            org.openjdk.jmh.Main.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}