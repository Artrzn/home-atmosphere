package ru.home.atmosphere.main.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.home.atmosphere.ChartsController;
import ru.home.atmosphere.log.MetricsLogReader;
import ru.home.atmosphere.processing.co2.Co2LogMessage;
import ru.home.atmosphere.processing.humidity.HumidityLogMessage;
import ru.home.atmosphere.processing.temperature.TemperatureLogMessage;

@Configuration
public class ChartConfiguration {

    @Bean
    public ChartsController getChartsController(MetricsLogReader<Co2LogMessage> co2LogReader, MetricsLogReader<HumidityLogMessage> humidityLogReader,
                                                MetricsLogReader<TemperatureLogMessage> temperatureLogReader) {
        return new ChartsController(co2LogReader, humidityLogReader, temperatureLogReader);
    }
}
