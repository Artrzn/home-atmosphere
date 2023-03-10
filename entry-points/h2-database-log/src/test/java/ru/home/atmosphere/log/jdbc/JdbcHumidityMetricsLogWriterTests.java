package ru.home.atmosphere.log.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.home.atmosphere.atmosphere_metrics.Humidity;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.humidity.HumidityLogMessage;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class JdbcHumidityMetricsLogWriterTests {

    private static final String QUERY = "INSERT INTO HUMIDITY_LOG (measureTimeStamp, sensorId, humidity) VALUES (?, ?, ?)";
    private JdbcTemplate jdbcTemplate;
    private MetricsLogWriter<HumidityLogMessage> humidityMetricsLogWriter;

    @BeforeEach
    public void init() {
        jdbcTemplate = mock(JdbcTemplate.class);
        humidityMetricsLogWriter = new JdbcHumidityMetricsLogWriter(jdbcTemplate);
    }

    @Test
    public void log_exceptionWhileLog_exceptionThrown() {
        Map<String, Humidity> humidityMetrics = getMetrics();
        List<HumidityLogMessage> logMessages = getLogMessages(humidityMetrics, new Timestamp(System.currentTimeMillis()));
        when(jdbcTemplate.batchUpdate(eq(QUERY), any(List.class))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> humidityMetricsLogWriter.log(logMessages));
        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), any(List.class));
    }

    @Test
    public void log_normalState_logWrote() {
        Map<String, Humidity> humidityMetrics = getMetrics();
        List<HumidityLogMessage> logMessages = getLogMessages(humidityMetrics, new Timestamp(System.currentTimeMillis()));

        humidityMetricsLogWriter.log(logMessages);

        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), any(List.class));
    }

    @Test
    public void log_normalState_queryIsCorrect() {
        Map<String, Humidity> humidityMetrics = getMetrics();
        List<HumidityLogMessage> logMessages = getLogMessages(humidityMetrics, new Timestamp(System.currentTimeMillis()));
        List<Object[]> batchArguments = getArguments(logMessages);

        humidityMetricsLogWriter.log(logMessages);

        ArgumentCaptor<List<Object[]>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), argumentCaptor.capture());
        List<Object[]> resultValue = argumentCaptor.getValue();
        verifyBatchArguments(batchArguments, resultValue);
    }

    private Map<String, Humidity> getMetrics() {
        Humidity humidity1 = new Humidity();
        Humidity humidity2 = new Humidity();
        Humidity humidity3 = new Humidity();
        humidity1.setValue(10.0f);
        humidity2.setValue(20.0f);
        humidity3.setValue(30.0f);
        return Map.of("sensor1Id", humidity1, "sensor2Id", humidity2, "sensor3Id", humidity3);
    }

    private List<HumidityLogMessage> getLogMessages(Map<String, Humidity> metrics, Timestamp timestamp) {
        return metrics.entrySet().stream()
                .map(metricsEntry -> new HumidityLogMessage(metricsEntry.getKey(), metricsEntry.getValue(), timestamp))
                .collect(Collectors.toList());
    }

    private List<Object[]> getArguments(List<HumidityLogMessage> logMessages) {
        List<Object[]> result = new ArrayList<>();
        for (HumidityLogMessage message : logMessages) {
            Object[] arguments = new Object[3];
            arguments[0] = message.getMeasureTimestamp();
            arguments[1] = message.getSensorId();
            arguments[2] = message.getMetric().getValue();
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
        for (Object[] argumentsArray : listOfArguments) {
            result.put((String) argumentsArray[1], Arrays.copyOfRange(argumentsArray, 0, 3));
        }
        return result;
    }
}
