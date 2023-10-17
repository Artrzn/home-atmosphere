package ru.home.atmosphere.log.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.temperature.TemperatureLogMessage;

import java.util.ArrayList;
import java.util.List;

public class JdbcTemperatureMetricsLogWriter implements MetricsLogWriter<TemperatureLogMessage> {

    private static final String QUERY = "INSERT INTO TEMPERATURE_LOG (measureTimeStamp, sensorId, temperature, heaterState) VALUES (?, ?, ?, ?)";
    private static final String[] HEATER_STATES = new String[]{"WARM_UP", "COOL_DOWN"};
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemperatureMetricsLogWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void log(List<TemperatureLogMessage> messages) {
        List<Object[]> batchArguments = prepareArguments(messages);
        jdbcTemplate.batchUpdate(QUERY, batchArguments);
    }

    private List<Object[]> prepareArguments(List<TemperatureLogMessage> messages) {
        List<Object[]> result = new ArrayList<>();
        for (TemperatureLogMessage message : messages) {
            Object[] arguments = new Object[4];
            arguments[0] = message.getMeasureTimestamp();
            arguments[1] = message.getSensorId();
            arguments[2] = message.getMetric().getValue();
            arguments[3] = message.getHeaterRelayState() ? HEATER_STATES[0] : HEATER_STATES[1];
            result.add(arguments);
        }
        return result;
    }
}
