package ru.home.atmosphere.processing.humidity;

import ru.home.atmosphere.atmosphere_metrics.Humidity;
import ru.home.atmosphere.log.LogMessage;

import java.sql.Timestamp;

public class HumidityLogMessage implements LogMessage<Humidity> {

    private final String sensorId;
    private final Humidity humidityMetric;
    private final Timestamp measureTimeStamp;

    public HumidityLogMessage(String sensorId, Humidity humidityMetric, Timestamp measureTimeStamp) {
        this.sensorId = sensorId;
        this.humidityMetric = humidityMetric;
        this.measureTimeStamp = measureTimeStamp;
    }

    @Override
    public String getSensorId() {
        return sensorId;
    }

    @Override
    public Humidity getMetric() {
        return humidityMetric;
    }

    @Override
    public Timestamp getMeasureTimestamp() {
        return measureTimeStamp;
    }
}
