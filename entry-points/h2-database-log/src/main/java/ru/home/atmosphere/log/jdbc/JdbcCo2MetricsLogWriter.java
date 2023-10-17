package ru.home.atmosphere.log.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.co2.Co2LogMessage;

import java.util.ArrayList;
import java.util.List;

public class JdbcCo2MetricsLogWriter implements MetricsLogWriter<Co2LogMessage> {

    private static final String QUERY = "INSERT INTO CO2_LOG (measureTimeStamp, sensorId, co2ppm) VALUES (?, ?, ?)";
    private final JdbcTemplate jdbcTemplate;

    public JdbcCo2MetricsLogWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void log(List<Co2LogMessage> messages) {
        List<Object[]> batchArguments = prepareArguments(messages);
        jdbcTemplate.batchUpdate(QUERY, batchArguments);
    }

    private List<Object[]> prepareArguments(List<Co2LogMessage> messages) {
        List<Object[]> result = new ArrayList<>();
        for (Co2LogMessage message : messages) {
            Object[] arguments = new Object[3];
            arguments[0] = message.getMeasureTimestamp();
            arguments[1] = message.getSensorId();
            arguments[2] = message.getMetric().getValue();
            result.add(arguments);
        }
        return result;
    }
}
