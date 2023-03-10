package ru.home.atmosphere.log.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.home.atmosphere.atmosphere_metrics.Humidity;
import ru.home.atmosphere.log.MetricsLogReader;
import ru.home.atmosphere.processing.humidity.HumidityLogMessage;
import java.sql.Timestamp;
import java.util.List;

public class JdbcHumidityMetricsLogReader implements MetricsLogReader<HumidityLogMessage> {

    private final static Logger LOGGER = LogManager.getLogger(JdbcHumidityMetricsLogReader.class);
    private String selectTemplate = "SELECT * FROM HUMIDITY_LOG WHERE MEASURETIMESTAMP BETWEEN ? and ? ORDER BY MEASURETIMESTAMP";
    private String selectBySensorIdTemplate = "SELECT * FROM HUMIDITY_LOG WHERE MEASURETIMESTAMP BETWEEN ? and ? and SENSORID = ? ORDER BY MEASURETIMESTAMP";
    private JdbcTemplate jdbcTemplate;

    public JdbcHumidityMetricsLogReader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<HumidityLogMessage> readLog(Timestamp from, Timestamp to) {
        LOGGER.info("Read humidity logs between {} and {}.", from, to);
        return jdbcTemplate.query(selectTemplate, (resultSet, i) ->
                        new HumidityLogMessage(
                                resultSet.getString(3),
                                new Humidity() {{
                                    setValue(resultSet.getFloat(4));
                                }},
                                resultSet.getTimestamp(2)),
                from, to
        );
    }

    @Override
    public List<HumidityLogMessage> readLogBySensorId(String sensorId, Timestamp from, Timestamp to) {
        return jdbcTemplate.query(selectBySensorIdTemplate, (resultSet, i) ->
                        new HumidityLogMessage(
                                resultSet.getString(3),
                                new Humidity() {{
                                    setValue(resultSet.getFloat(4));
                                }},
                                resultSet.getTimestamp(2)),
                from, to, sensorId
        );
    }
}
