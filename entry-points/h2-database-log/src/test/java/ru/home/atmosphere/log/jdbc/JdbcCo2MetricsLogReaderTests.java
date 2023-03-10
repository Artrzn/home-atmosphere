package ru.home.atmosphere.log.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.home.atmosphere.log.LogMessage;
import ru.home.atmosphere.log.MetricsLogReader;
import ru.home.atmosphere.processing.co2.Co2LogMessage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JdbcCo2MetricsLogReaderTests {

    private String selectTemplate = "SELECT * FROM CO2_LOG WHERE MEASURETIMESTAMP BETWEEN ? and ? ORDER BY MEASURETIMESTAMP";
    private String selectBySensorIdTemplate = "SELECT * FROM CO2_LOG WHERE MEASURETIMESTAMP BETWEEN ? and ? and SENSORID = ? ORDER BY MEASURETIMESTAMP";
    private JdbcTemplate jdbcTemplate;
    private MetricsLogReader<Co2LogMessage> logReader;

    @BeforeEach
    public void init() {
        jdbcTemplate = mock(JdbcTemplate.class);
        logReader = new JdbcCo2MetricsLogReader(jdbcTemplate);
    }

    @Test
    public void readLog_notFoundLogsWithSelectedDates_emptyResultReturned() {
        Timestamp from = Timestamp.valueOf(LocalDateTime.now());
        Timestamp to = Timestamp.valueOf(LocalDateTime.now().plusDays(1));

        when(jdbcTemplate.query(eq(selectTemplate), any(RowMapper.class), eq(from), eq(to))).thenReturn(List.of());

        List<Co2LogMessage> result = logReader.readLog(from, to);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, times(1)).query(eq(selectTemplate), any(RowMapper.class), eq(from), eq(to));
    }

    @Test
    public void readLog_normalState_listOfRowsReturned() {
        List<LogMessage> expected = List.of(mock(LogMessage.class));
        Timestamp from = Timestamp.valueOf(LocalDateTime.now());
        Timestamp to = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
        when(jdbcTemplate.query(eq(selectTemplate), any(RowMapper.class), eq(from), eq(to))).thenReturn(expected);

        List<Co2LogMessage> result = logReader.readLog(from, to);

        assertEquals(expected, result);
        verify(jdbcTemplate, times(1)).query(eq(selectTemplate), any(RowMapper.class), eq(from), eq(to));
    }

    @Test
    public void readLogBySensorId_notFoundLogsWithSelectedDates_emptyResultReturned() {
        String sensorId = "testSensorId";
        Timestamp from = Timestamp.valueOf(LocalDateTime.now());
        Timestamp to = Timestamp.valueOf(LocalDateTime.now().plusDays(1));

        when(jdbcTemplate.query(eq(selectBySensorIdTemplate), any(RowMapper.class), eq(from), eq(to), eq(sensorId))).thenReturn(List.of());

        List<Co2LogMessage> result = logReader.readLogBySensorId(sensorId, from, to);

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

        List<Co2LogMessage> result = logReader.readLogBySensorId(sensorId, from, to);

        assertEquals(expected, result);
        verify(jdbcTemplate, times(1)).query(eq(selectBySensorIdTemplate), any(RowMapper.class), eq(from), eq(to), eq(sensorId));
    }
}
