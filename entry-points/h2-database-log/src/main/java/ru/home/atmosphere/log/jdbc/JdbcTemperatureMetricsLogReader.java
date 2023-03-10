package ru.home.atmosphere.log.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.home.atmosphere.atmosphere_metrics.Temperature;
import ru.home.atmosphere.log.MetricsLogReader;
import ru.home.atmosphere.processing.temperature.TemperatureLogMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class JdbcTemperatureMetricsLogReader implements MetricsLogReader<TemperatureLogMessage> {

    private final static Logger LOGGER = LogManager.getLogger(JdbcTemperatureMetricsLogReader.class);
    private String selectTemplate = "SELECT * FROM TEMPERATURE_LOG WHERE MEASURETIMESTAMP BETWEEN ? and ? ORDER BY MEASURETIMESTAMP";
    private String selectBySensorIdTemplate = "SELECT * FROM TEMPERATURE_LOG WHERE MEASURETIMESTAMP BETWEEN ? and ? and SENSORID = ? ORDER BY MEASURETIMESTAMP";
    private JdbcTemplate jdbcTemplate;

    public JdbcTemperatureMetricsLogReader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<TemperatureLogMessage> readLog(Timestamp from, Timestamp to) {
        LOGGER.info("Read temperature logs between {} and {}.", from, to);
        return jdbcTemplate.query(selectTemplate, (resultSet, i) ->
                     new TemperatureLogMessage(
                                resultSet.getString(3),
                                new Temperature() {{
                                    BigDecimal result = new BigDecimal(resultSet.getFloat(4));
                                    result = result.setScale(2, RoundingMode.DOWN);
                                    setValue(result.floatValue());
                                }},
                                resultSet.getTimestamp(2),
                                "WARM_UP".equals(resultSet.getString(5))),
                from, to
        );
    }

    @Override
    public List<TemperatureLogMessage> readLogBySensorId(String sensorId, Timestamp from, Timestamp to) {
        return jdbcTemplate.query(selectBySensorIdTemplate, (resultSet, i) ->
                        new TemperatureLogMessage(
                                resultSet.getString(3),
                                new Temperature() {{
                                    setValue(resultSet.getFloat(4));
                                }},
                                resultSet.getTimestamp(2),
                                "WARM_UP".equals(resultSet.getString(5))),
                from, to, sensorId
        );
    }
}
