package ru.home.atmosphere.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.home.atmosphere.atmosphere_metrics.HomeAtmosphereMetrics;
import ru.home.atmosphere.processing.ProcessingException;
import ru.home.atmosphere.processing.co2.Co2Processing;
import ru.home.atmosphere.processing.humidity.HumidityProcessing;
import ru.home.atmosphere.processing.temperature.TemperatureProcessing;
import ru.home.atmosphere.sensor.HomeAtmosphere;

import java.util.Optional;

@Component
public class WorkCycle {

    private final static Logger LOGGER = LogManager.getLogger(WorkCycle.class);
    private final HomeAtmosphere homeAtmosphere;
    private final TemperatureProcessing temperatureProcessing;
    private final HumidityProcessing humidityProcessing;
    private final Co2Processing co2Processing;

    public WorkCycle(HomeAtmosphere homeAtmosphere, TemperatureProcessing temperatureProcessing,
                     HumidityProcessing humidityProcessing, Co2Processing co2Processing) {
        this.homeAtmosphere = homeAtmosphere;
        this.temperatureProcessing = temperatureProcessing;
        this.humidityProcessing = humidityProcessing;
        this.co2Processing = co2Processing;
    }

        @Scheduled(fixedDelayString = "120000")
    public void scheduleFixedDelayTask() {
            Optional<HomeAtmosphereMetrics> optional = homeAtmosphere.getMetrics();
            if (optional.isPresent()) {
                HomeAtmosphereMetrics metrics  = optional.get();
                try {
                    temperatureProcessing.process(metrics.getTemperature());
                } catch (ProcessingException e) {
                    LOGGER.error("Error processing temperature.", e);
                }
                try {
                    humidityProcessing.process(metrics.getHumidity());
                } catch (ProcessingException e) {
                    LOGGER.error("Error processing humidity.", e);
                }
                try {
                    co2Processing.process(metrics.getCo2PPM());
                } catch (ProcessingException e) {
                    LOGGER.error("Error processing co2Ppm.", e);
                }
            }
        }
}
