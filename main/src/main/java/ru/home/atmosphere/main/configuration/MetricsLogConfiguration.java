package ru.home.atmosphere.main.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.home.atmosphere.log.MetricsLogReader;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.log.jdbc.*;
import ru.home.atmosphere.processing.co2.Co2LogMessage;
import ru.home.atmosphere.processing.humidity.HumidityLogMessage;
import ru.home.atmosphere.processing.temperature.TemperatureLogMessage;

@Configuration
public class MetricsLogConfiguration {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public MetricsLogWriter<TemperatureLogMessage> getTemperatureLogWriter() {
        return new JdbcTemperatureMetricsLogWriter(jdbcTemplate);
    }

    @Bean
    public MetricsLogWriter<HumidityLogMessage> getHumidityLogWriter() {
        return new JdbcHumidityMetricsLogWriter(jdbcTemplate);
    }

    @Bean
    public MetricsLogWriter<Co2LogMessage> getCo2LogWriter() {
        return new JdbcCo2MetricsLogWriter(jdbcTemplate);
    }

    @Bean
    public MetricsLogReader<Co2LogMessage> getCo2LogReader() {
        return new JdbcCo2MetricsLogReader(jdbcTemplate);
    }

    @Bean
    public MetricsLogReader<HumidityLogMessage> getHumidityLogReader() {
        return new JdbcHumidityMetricsLogReader(jdbcTemplate);
    }
    @Bean
    public MetricsLogReader<TemperatureLogMessage> getTemperatureLogReader() {
        return new JdbcTemperatureMetricsLogReader(jdbcTemplate);
    }
}
