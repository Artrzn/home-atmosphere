package ru.home.atmosphere.log.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.humidity.HumidityLogMessage;

import java.util.ArrayList;
import java.util.List;

public class JdbcHumidityMetricsLogWriter implements MetricsLogWriter<HumidityLogMessage> {

    private static final String QUERY = "INSERT INTO HUMIDITY_LOG (measureTimeStamp, sensorId, humidity) VALUES (?, ?, ?)";
    private JdbcTemplate jdbcTemplate;

    public JdbcHumidityMetricsLogWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void log(List<HumidityLogMessage> messages) {
        List<Object[]> batchArguments = prepareArguments(messages);
        jdbcTemplate.batchUpdate(QUERY, batchArguments);
    }

    private List<Object[]> prepareArguments(List<HumidityLogMessage> messages) {
        List<Object[]> result = new ArrayList<>();
        for (HumidityLogMessage message : messages) {
            Object[] arguments = new Object[3];
            arguments[0] = message.getMeasureTimestamp();
            arguments[1] = message.getSensorId();
            arguments[2] = message.getMetric().getValue();
            result.add(arguments);
        }
        return result;
    }
}
