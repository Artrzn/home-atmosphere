package ru.home.atmosphere.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.home.atmosphere.atmosphere_metrics.RoomAtmosphereMetrics;
import ru.home.atmosphere.sensor.AtmosphereSensor;
import ru.home.atmosphere.sensor.SensorException;

public class SensorWithTemperatureCoefficientWrapper implements AtmosphereSensor {

    private final static Logger LOGGER = LogManager.getLogger(SensorWithTemperatureCoefficientWrapper.class);
    private float coefficient;
    private AtmosphereSensor wrappedSensor;

    public SensorWithTemperatureCoefficientWrapper(float coefficient, AtmosphereSensor wrappedSensor) {
        this.coefficient = coefficient;
        this.wrappedSensor = wrappedSensor;
    }

    @Override
    public RoomAtmosphereMetrics getAtmosphereMetrics() throws SensorException {
        RoomAtmosphereMetrics atmosphereMetrics = wrappedSensor.getAtmosphereMetrics();
        float actualTemperature = atmosphereMetrics.getTemperatureFromDS();
        float temperatureWithCoefficient = actualTemperature / coefficient;
        LOGGER.info("Actual temperature: {}. Temperature with coefficient: {}.", actualTemperature, temperatureWithCoefficient);
        atmosphereMetrics.setTemperatureFromDS(temperatureWithCoefficient);
        return atmosphereMetrics;
    }
}
