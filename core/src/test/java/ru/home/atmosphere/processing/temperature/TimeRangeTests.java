package ru.home.atmosphere.processing.temperature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TimeRangeTests {

    private LocalTime from;
    private LocalTime to;
    private TimeRange timeRange;

    @BeforeEach
    public void init() {
        from = LocalTime.NOON;
        to = LocalTime.MIDNIGHT;
        timeRange = new TimeRange(from, to);
    }

    @Test
    public void isInRange_morning_falseReturned() {
        boolean result = timeRange.isInRange(from.minusHours(4));

        assertFalse(result, "Checked time not in range, result must be false.");
    }

    @Test
    public void isInRange_evening_trueReturned() {
        boolean result = timeRange.isInRange(from.plusHours(6));

        assertFalse(result, "Checked time in range, result must be true.");
    }
}
