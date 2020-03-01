package com.writer.audio;

public interface TimeListener {
    void timeUpdated(long seconds);
    void stopped(long seconds);
}
