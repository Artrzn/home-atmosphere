package ru.home.atmosphere.log.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.home.atmosphere.atmosphere_metrics.Co2PPM;
import ru.home.atmosphere.log.MetricsLogReader;
import ru.home.atmosphere.processing.co2.Co2LogMessage;

import java.sql.Timestamp;
import java.util.List;

public class JdbcCo2MetricsLogReader implements MetricsLogReader<Co2LogMessage> {

    private final static Logger LOGGER = LogManager.getLogger(JdbcCo2MetricsLogReader.class);
    private final JdbcTemplate jdbcTemplate;

    public JdbcCo2MetricsLogReader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Co2LogMessage> readLog(Timestamp from, Timestamp to) {
        LOGGER.info("Read co2 logs between {} and {}.", from, to);
        String selectTemplate = "SELECT * FROM CO2_LOG WHERE MEASURETIMESTAMP BETWEEN ? and ? ORDER BY MEASURETIMESTAMP";
        return jdbcTemplate.query(selectTemplate, (resultSet, i) ->
                        new Co2LogMessage(
                                resultSet.getString(3),
                                new Co2PPM() {{
                                    setValue(resultSet.getInt(4));
                                }},
                                resultSet.getTimestamp(2)),
                from, to
        );
    }

    @Override
    public List<Co2LogMessage> readLogBySensorId(String sensorId, Timestamp from, Timestamp to) {
        String selectBySensorIdTemplate = "SELECT * FROM CO2_LOG WHERE MEASURETIMESTAMP BETWEEN ? and ? and SENSORID = ? ORDER BY MEASURETIMESTAMP";
        return jdbcTemplate.query(selectBySensorIdTemplate, (resultSet, i) ->
                        new Co2LogMessage(
                                resultSet.getString(3),
                                new Co2PPM() {{
                                    setValue(resultSet.getInt(4));
                                }},
                                resultSet.getTimestamp(2)),
                from, to, sensorId
        );
    }
}
