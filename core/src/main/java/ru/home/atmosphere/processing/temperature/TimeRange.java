package ru.home.atmosphere.processing.temperature;

import java.time.LocalTime;

public class TimeRange {
    private LocalTime from;
    private LocalTime to;

    public TimeRange(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }

    public boolean isInRange(LocalTime checkedTime) {
        return checkedTime.isAfter(from) && checkedTime.isBefore(to);
    }
}
