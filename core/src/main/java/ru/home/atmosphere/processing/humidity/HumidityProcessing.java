package ru.home.atmosphere.processing.humidity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.home.atmosphere.atmosphere_metrics.Humidity;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.MetricsProcessing;
import ru.home.atmosphere.processing.ProcessingException;
import ru.home.atmosphere.processing.co2.Co2LogMessage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HumidityProcessing implements MetricsProcessing<Map<String, Humidity>> {

    private final static Logger LOGGER = LogManager.getLogger(HumidityProcessing.class);
    private MetricsLogWriter<HumidityLogMessage> humidityLog;

    public HumidityProcessing(MetricsLogWriter<HumidityLogMessage> humidityLog) {
        this.humidityLog = humidityLog;
    }

    @Override
    public void process(Map<String, Humidity> humidityMetrics) throws ProcessingException {
        LOGGER.info("Start processing humidity: {}.", humidityMetrics);
        logHumidity(humidityMetrics);
    }

    private void logHumidity(Map<String, Humidity> humidityMetrics) throws ProcessingException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<HumidityLogMessage> logMessages = humidityMetrics.entrySet().stream()
                .map(metricsEntry -> new HumidityLogMessage(metricsEntry.getKey(), metricsEntry.getValue(), timestamp))
                .collect(Collectors.toList());
        try {
            humidityLog.log(logMessages);
        } catch (Exception e) {
            throw new ProcessingException("Error while log humidity metrics.", e);
        }
    }
}
