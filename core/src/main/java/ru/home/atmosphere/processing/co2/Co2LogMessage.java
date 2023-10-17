package ru.home.atmosphere.processing.co2;

import ru.home.atmosphere.atmosphere_metrics.Co2PPM;
import ru.home.atmosphere.log.LogMessage;

import java.sql.Timestamp;

public class Co2LogMessage implements LogMessage<Co2PPM> {

    private final String sensorId;
    private final Co2PPM co2Metric;
    private final Timestamp measureTimeStamp;

    public Co2LogMessage(String sensorId, Co2PPM co2Metric, Timestamp measureTimeStamp) {
        this.sensorId = sensorId;
        this.co2Metric = co2Metric;
        this.measureTimeStamp = measureTimeStamp;
    }

    @Override
    public String getSensorId() {
        return sensorId;
    }

    @Override
    public Co2PPM getMetric() {
        return co2Metric;
    }

    @Override
    public Timestamp getMeasureTimestamp() {
        return measureTimeStamp;
    }
}
