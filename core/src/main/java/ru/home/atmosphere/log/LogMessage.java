package ru.home.atmosphere.log;


import java.sql.Timestamp;

public interface LogMessage<Metric> {

    String getSensorId();

    Metric getMetric();

    Timestamp getMeasureTimestamp();
}
