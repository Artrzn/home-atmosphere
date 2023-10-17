package ru.home.atmosphere.processing.temperature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.home.atmosphere.atmosphere_metrics.Temperature;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.MetricsProcessing;
import ru.home.atmosphere.processing.ProcessingException;
import ru.home.atmosphere.processing.temperature.heater_relay.HeaterRelay;
import ru.home.atmosphere.processing.temperature.heater_relay.RelayException;
import ru.home.atmosphere.processing.temperature.priority.PriorityTemperature;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemperatureProcessing implements MetricsProcessing<Map<String, Temperature>> {

    private final static Logger LOGGER = LogManager.getLogger(TemperatureProcessing.class);
    private final PriorityTemperature priorityTemperature;
    private final HeaterMode heaterMode;
    private final HeaterRelay heaterRelay;
    private final MetricsLogWriter<TemperatureLogMessage> temperatureLog;

    public TemperatureProcessing(PriorityTemperature priorityTemperature, HeaterMode heaterMode, HeaterRelay heaterRelay,
                                 MetricsLogWriter<TemperatureLogMessage> temperatureLog) {
        this.priorityTemperature = priorityTemperature;
        this.heaterMode = heaterMode;
        this.heaterRelay = heaterRelay;
        this.temperatureLog = temperatureLog;
    }

    public void process(Map<String, Temperature> temperatureMetrics) throws ProcessingException {
        LOGGER.info("Start processing temperature: {}.", temperatureMetrics);
        Temperature temperatureForControl = priorityTemperature.compute(temperatureMetrics);
        boolean isNeedToWarmUp = heaterMode.isNeedWarmUp(temperatureForControl.getValue());
        changeHeaterState(isNeedToWarmUp);
        logTemperature(temperatureMetrics, isNeedToWarmUp);
    }

    private void changeHeaterState(boolean isNeedToWarmUp) throws ProcessingException {
        try {
            if (isNeedToWarmUp) {
                heaterRelay.switchOn();
                LOGGER.info("Heater enabled.");
            } else {
                heaterRelay.switchOff();
                LOGGER.info("Heater disabled.");
            }
        } catch (RelayException e) {
            throw new ProcessingException("Error while change heater state.", e);
        }
    }

    private void logTemperature(Map<String, Temperature> temperatureMetrics, boolean heaterRelayState) throws ProcessingException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<TemperatureLogMessage> logMessages = temperatureMetrics.entrySet().stream()
                .map(metricsEntry -> new TemperatureLogMessage(metricsEntry.getKey(), metricsEntry.getValue(), timestamp, heaterRelayState))
                .collect(Collectors.toList());
        try {
            temperatureLog.log(logMessages);
        } catch (Exception e) {
            throw new ProcessingException("Error while log temperature metrics.", e);
        }
    }
}
