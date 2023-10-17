package ru.home.atmosphere.log.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.home.atmosphere.log.LogMessage;
import ru.home.atmosphere.log.MetricsLogReader;
import ru.home.atmosphere.processing.temperature.TemperatureLogMessage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class JdbcTemperatureMetricsLogReaderTests {

    private final String selectTemplate = "SELECT * FROM TEMPERATURE_LOG WHERE MEASURETIMESTAMP BETWEEN ? and ? ORDER BY MEASURETIMESTAMP";
    private final String selectBySensorIdTemplate = "SELECT * FROM TEMPERATURE_LOG WHERE MEASURETIMESTAMP BETWEEN ? and ? and SENSORID = ? ORDER BY MEASURETIMESTAMP";
    private JdbcTemplate jdbcTemplate;
    private MetricsLogReader<TemperatureLogMessage> logReader;

    @BeforeEach
    public void init() {
        jdbcTemplate = mock(JdbcTemplate.class);
        logReader = new JdbcTemperatureMetricsLogReader(jdbcTemplate);
    }

    @Test
    public void readLog_notFoundLogsWithSelectedDates_emptyResultReturned() {
        Timestamp from = Timestamp.valueOf(LocalDateTime.now());
        Timestamp to = Timestamp.valueOf(LocalDateTime.now().plusDays(1));

        when(jdbcTemplate.query(eq(selectTemplate), any(RowMapper.class), eq(from), eq(to))).thenReturn(List.of());

        List<TemperatureLogMessage> result = logReader.readLog(from, to);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, times(1)).query(eq(selectTemplate), any(RowMapper.class), eq(from), eq(to));
    }

    @Test
    public void readLog_normalState_listOfRowsReturned() {
        List<LogMessage> expected = List.of(mock(LogMessage.class));
        Timestamp from = Timestamp.valueOf(LocalDateTime.now());
        Timestamp to = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
        when(jdbcTemplate.query(eq(selectTemplate), any(RowMapper.class), eq(from), eq(to))).thenReturn(expected);

        List<TemperatureLogMessage> result = logReader.readLog(from, to);

        assertEquals(expected, result);
        verify(jdbcTemplate, times(1)).query(eq(selectTemplate), any(RowMapper.class), eq(from), eq(to));
    }

    @Test
    public void readLogBySensorId_notFoundLogsWithSelectedDates_emptyResultReturned() {
        String sensorId = "testSensorId";
        Timestamp from = Timestamp.valueOf(LocalDateTime.now());
        Timestamp to = Timestamp.valueOf(LocalDateTime.now().plusDays(1));

        when(jdbcTemplate.query(eq(selectBySensorIdTemplate), any(RowMapper.class), eq(from), eq(to), eq(sensorId))).thenReturn(List.of());

        List<TemperatureLogMessage> result = logReader.readLogBySensorId(sensorId, from, to);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, times(1)).query(eq(selectBySensorIdTemplate), any(RowMapper.class), eq(from), eq(to), eq(sensorId));
    }

    @Test
    public void readLogBySensorId_normalState_listOfRowsReturned() {
        List<LogMessage> expected = List.of(mock(LogMessage.class));
        String sensorId = "testSensorId";
        Timestamp from = Timestamp.valueOf(LocalDateTime.now());
        Timestamp to = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
        when(jdbcTemplate.query(eq(selectBySensorIdTemplate), any(RowMapper.class), eq(from), eq(to), eq(sensorId))).thenReturn(expected);

        List<TemperatureLogMessage> result = logReader.readLogBySensorId(sensorId, from, to);

        assertEquals(expected, result);
        verify(jdbcTemplate, times(1)).query(eq(selectBySensorIdTemplate), any(RowMapper.class), eq(from), eq(to), eq(sensorId));
    }
}
