package ru.sbt.edu.tokenring;

public class Package {
    private final String value;
    private Long startTime;

    public Package(String value) {
        this.value = value;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}
