package ru.home.atmosphere.processing.co2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.home.atmosphere.atmosphere_metrics.Co2PPM;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.MetricsProcessing;
import ru.home.atmosphere.processing.ProcessingException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Co2Processing implements MetricsProcessing<Map<String, Co2PPM>> {

    private final static Logger LOGGER = LogManager.getLogger(Co2Processing.class);
    private final MetricsLogWriter<Co2LogMessage> co2Log;

    public Co2Processing(MetricsLogWriter<Co2LogMessage> co2Log) {
        this.co2Log = co2Log;
    }

    @Override
    public void process(Map<String, Co2PPM> co2Metrics) throws ProcessingException {
        LOGGER.info("Start processing co2 ppm: {}.", co2Metrics);
        logCo2Ppm(co2Metrics);
    }

    private void logCo2Ppm(Map<String, Co2PPM> co2Metrics) throws ProcessingException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<Co2LogMessage> logMessages = co2Metrics.entrySet().stream()
                .map(metricsEntry -> new Co2LogMessage(metricsEntry.getKey(), metricsEntry.getValue(), timestamp))
                .collect(Collectors.toList());
        try {
            co2Log.log(logMessages);
        } catch (Exception e) {
            throw new ProcessingException("Error while log co2 metrics.", e);
        }
    }
}
