package ru.home.atmosphere.log.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.home.atmosphere.atmosphere_metrics.Temperature;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.temperature.TemperatureLogMessage;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JdbcTemperatureMetricsLogWriterTests {

    private static final String[] HEATER_STATES = new String[]{"WARM_UP", "COOL_DOWN"};
    private static final String QUERY = "INSERT INTO TEMPERATURE_LOG (measureTimeStamp, sensorId, temperature, heaterState) VALUES (?, ?, ?, ?)";
    private JdbcTemplate jdbcTemplate;
    private MetricsLogWriter<TemperatureLogMessage> temperatureMetricsLogWriter;

    @BeforeEach
    public void init() {
        jdbcTemplate = mock(JdbcTemplate.class);
        temperatureMetricsLogWriter = new JdbcTemperatureMetricsLogWriter(jdbcTemplate);
    }
    @Test
    public void log_exceptionWhileLog_exceptionThrown() {
        Map<String, Temperature> temperatureMetrics = getMetrics();
        List<TemperatureLogMessage> messages = getLogMessages(temperatureMetrics, new Timestamp(System.currentTimeMillis()), true);
        when(jdbcTemplate.batchUpdate(eq(QUERY), any(List.class))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> temperatureMetricsLogWriter.log(messages));
        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), any(List.class));
    }

    @Test
    public void log_heaterStateIsTrue_logWrote() {
        Map<String, Temperature> temperatureMetrics = getMetrics();
        List<TemperatureLogMessage> messages = getLogMessages(temperatureMetrics, new Timestamp(System.currentTimeMillis()), true);

        temperatureMetricsLogWriter.log(messages);

        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), any(List.class));
    }

    @Test
    public void log_heaterStateIsTrue_queryIsCorrect() {
        Map<String, Temperature> temperatureMetrics = getMetrics();
        List<TemperatureLogMessage> messages = getLogMessages(temperatureMetrics, new Timestamp(System.currentTimeMillis()), true);
        List<Object[]> batchArguments = getArguments(messages);

        temperatureMetricsLogWriter.log(messages);

        ArgumentCaptor<List<Object[]>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), argumentCaptor.capture());
        List<Object[]> resultValue = argumentCaptor.getValue();
        verifyBatchArguments(batchArguments, resultValue);
    }

    @Test
    public void log_heaterStateIsFalse_logWrote() {
        Map<String, Temperature> temperatureMetrics = getMetrics();
        List<TemperatureLogMessage> messages = getLogMessages(temperatureMetrics, new Timestamp(System.currentTimeMillis()), false);

        temperatureMetricsLogWriter.log(messages);

        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), any(List.class));
    }

    @Test
    public void log_heaterStateIsFalse_queryIsCorrect() {
        Map<String, Temperature> temperatureMetrics = getMetrics();
        List<TemperatureLogMessage> messages = getLogMessages(temperatureMetrics, new Timestamp(System.currentTimeMillis()), false);
        List<Object[]> batchArguments = getArguments(messages);

        temperatureMetricsLogWriter.log(messages);

        ArgumentCaptor<List<Object[]>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), argumentCaptor.capture());
        List<Object[]> resultValue = argumentCaptor.getValue();
        verifyBatchArguments(batchArguments, resultValue);
    }

    private Map<String, Temperature> getMetrics() {
        Temperature temperature1 = new Temperature();
        Temperature temperature2 = new Temperature();
        Temperature temperature3 = new Temperature();
        temperature1.setValue(10.0f);
        temperature2.setValue(20.0f);
        temperature3.setValue(30.0f);
        return Map.of("sensor1Id", temperature1, "sensor2Id", temperature2, "sensor3Id", temperature3);
    }

    private List<TemperatureLogMessage> getLogMessages(Map<String, Temperature> metrics, Timestamp timestamp,  boolean heaterState) {
        return metrics.entrySet().stream()
                .map(metricsEntry -> new TemperatureLogMessage(metricsEntry.getKey(), metricsEntry.getValue(), timestamp, heaterState))
                .collect(Collectors.toList());
    }


    private List<Object[]> getArguments(List<TemperatureLogMessage> logMessages) {
        List<Object[]> result = new ArrayList<>();
        for (TemperatureLogMessage message : logMessages) {
            Object[] arguments = new Object[4];
            arguments[0] = message.getMeasureTimestamp();
            arguments[1] = message.getSensorId();
            arguments[2] = message.getMetric().getValue();
            arguments[3] = message.getHeaterRelayState() ? HEATER_STATES[0] : HEATER_STATES[1];
            result.add(arguments);
        }
        return result;
    }

    private void verifyBatchArguments(List<Object[]> sourceArguments, List<Object[]> resultArguments) {
        assertEquals(sourceArguments.size(), resultArguments.size(), "Count of result arguments not equals to count of source arguments.");
        Map<String, Object[]> sourceArgumentsById = mapArguments(sourceArguments);
        Map<String, Object[]> resultArgumentsById = mapArguments(resultArguments);
        assertEquals(sourceArgumentsById.keySet(), resultArgumentsById.keySet(), "Result arguments not equals to source arguments.");
        for (String id : sourceArgumentsById.keySet()) {
            assertArrayEquals(sourceArgumentsById.get(id), resultArgumentsById.get(id), "Result arguments not equals to source arguments.");
        }
    }

    private Map<String, Object[]> mapArguments(List<Object[]> listOfArguments) {
        Map<String, Object[]> result = new HashMap<>();
        for (Object[] argumentsArray: listOfArguments) {
            result.put((String) argumentsArray[1], Arrays.copyOfRange(argumentsArray, 0, 4));
        }
        return result;
    }
}
