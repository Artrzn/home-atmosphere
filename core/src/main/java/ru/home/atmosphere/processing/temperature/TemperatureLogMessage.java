package ru.home.atmosphere.processing.temperature;

import ru.home.atmosphere.atmosphere_metrics.Temperature;
import ru.home.atmosphere.log.LogMessage;

import java.sql.Timestamp;

public class TemperatureLogMessage implements LogMessage<Temperature> {

    private final String sensorId;
    private final Temperature temperatureMetric;
    private final Timestamp measureTimeStamp;
    private final boolean heaterRelayState;

    public TemperatureLogMessage(String sensorId, Temperature temperatureMetric, Timestamp measureTimeStamp,
                                 boolean heaterRelayState) {
        this.sensorId = sensorId;
        this.temperatureMetric = temperatureMetric;
        this.measureTimeStamp = measureTimeStamp;
        this.heaterRelayState = heaterRelayState;
    }
    @Override
    public String getSensorId() {
        return sensorId;
    }

    @Override
    public Temperature getMetric() {
        return temperatureMetric;
    }

    @Override
    public Timestamp getMeasureTimestamp() {
        return measureTimeStamp;
    }

    public boolean getHeaterRelayState() {
        return heaterRelayState;
    }
}
