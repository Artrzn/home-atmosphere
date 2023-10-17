package ru.home.atmosphere.log.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.home.atmosphere.atmosphere_metrics.Co2PPM;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.co2.Co2LogMessage;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JdbcCo2MetricsLogWriterTests {

    private static final String QUERY = "INSERT INTO CO2_LOG (measureTimeStamp, sensorId, co2ppm) VALUES (?, ?, ?)";
    private JdbcTemplate jdbcTemplate;
    private MetricsLogWriter<Co2LogMessage> co2MetricsLogWriter;

    @BeforeEach
    public void init() {
        jdbcTemplate = mock(JdbcTemplate.class);
        co2MetricsLogWriter = new JdbcCo2MetricsLogWriter(jdbcTemplate);
    }

    @Test
    public void log_exceptionWhileLog_exceptionThrown() {
        Map<String, Co2PPM> co2Metrics = getMetrics();
        List<Co2LogMessage> logMessages = getLogMessages(co2Metrics, new Timestamp(System.currentTimeMillis()));
        when(jdbcTemplate.batchUpdate(eq(QUERY), any(List.class))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> co2MetricsLogWriter.log(logMessages));
        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), any(List.class));
    }

    @Test
    public void log_normalState_logWrote() {
        Map<String, Co2PPM> co2Metrics = getMetrics();
        List<Co2LogMessage> logMessages = getLogMessages(co2Metrics, new Timestamp(System.currentTimeMillis()));

        co2MetricsLogWriter.log(logMessages);

        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), any(List.class));
    }

    @Test
    public void log_normalState_queryIsCorrect() {
        Map<String, Co2PPM> co2Metrics = getMetrics();
        List<Co2LogMessage> logMessages = getLogMessages(co2Metrics, new Timestamp(System.currentTimeMillis()));
        List<Object[]> batchArguments = getArguments(logMessages);

        co2MetricsLogWriter.log(logMessages);

        ArgumentCaptor<List<Object[]>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(jdbcTemplate, times(1)).batchUpdate(eq(QUERY), argumentCaptor.capture());
        List<Object[]> resultValue = argumentCaptor.getValue();
        verifyBatchArguments(batchArguments, resultValue);
    }

    private Map<String, Co2PPM> getMetrics() {
        Co2PPM co2PPM1 = new Co2PPM();
        Co2PPM co2PPM2 = new Co2PPM();
        Co2PPM co2PPM3 = new Co2PPM();
        co2PPM1.setValue(10);
        co2PPM2.setValue(20);
        co2PPM3.setValue(30);
        return Map.of("sensor1Id", co2PPM1, "sensor2Id", co2PPM2, "sensor3Id", co2PPM3);
    }

    private List<Co2LogMessage> getLogMessages(Map<String, Co2PPM> metrics, Timestamp timestamp) {
        return metrics.entrySet().stream()
                .map(metricsEntry -> new Co2LogMessage(metricsEntry.getKey(), metricsEntry.getValue(), timestamp))
                .collect(Collectors.toList());
    }

    private List<Object[]> getArguments(List<Co2LogMessage> logMessages) {
        List<Object[]> result = new ArrayList<>();
        for (Co2LogMessage message : logMessages) {
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
