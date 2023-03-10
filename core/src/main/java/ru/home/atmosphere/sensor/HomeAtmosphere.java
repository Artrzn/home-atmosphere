package ru.home.atmosphere.sensor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.home.atmosphere.atmosphere_metrics.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HomeAtmosphere {

    private final static Logger LOGGER = LogManager.getLogger(HomeAtmosphere.class);
    private final Map<String, AtmosphereSensor> sensors;

    public HomeAtmosphere(Map<String, AtmosphereSensor> sensors) {
        this.sensors = sensors;
    }

    public Optional<HomeAtmosphereMetrics> getMetrics() {
        Map<String, Temperature> temperature = new HashMap<>();
        Map<String, Humidity> humidity = new HashMap<>();
        Map<String, Co2PPM> co2PPM = new HashMap<>();
        sensors.entrySet().stream()
                .forEach(sensorsEntry -> {
                    String sensorName = sensorsEntry.getKey();
                    AtmosphereSensor sensor = sensorsEntry.getValue();
                    try {
                        RoomAtmosphereMetrics roomMetrics = sensor.getAtmosphereMetrics();
                        Temperature temperatureMetric = new Temperature();
                        temperatureMetric.setValue(roomMetrics.getTemperatureFromDS());
                        temperature.put(sensorName, temperatureMetric);
                        Humidity humidityMetric = new Humidity();
                        humidityMetric.setValue(roomMetrics.getHumidityFromDHT());
                        humidity.put(sensorName, humidityMetric);
                        Co2PPM co2PPMMetric = new Co2PPM();
                        co2PPMMetric.setValue(roomMetrics.getCo2ppm());
                        co2PPM.put(sensorName, co2PPMMetric);
                    } catch (SensorException e) {
                        LOGGER.error("Error while get atmosphere metrics from sensor: {}.", sensorName, e);
                    }
                });

        if (temperature.isEmpty() || humidity.isEmpty() || co2PPM.isEmpty()) {
            return Optional.empty();
        } else {
            HomeAtmosphereMetrics result = new HomeAtmosphereMetrics();
            result.setTemperature(temperature);
            result.setHumidity(humidity);
            result.setCo2PPM(co2PPM);
            return Optional.of(result);
        }
    }
}
