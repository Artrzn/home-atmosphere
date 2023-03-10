package ru.home.atmosphere.sensor;

import ru.home.atmosphere.atmosphere_metrics.RoomAtmosphereMetrics;

public interface AtmosphereSensor {

    RoomAtmosphereMetrics getAtmosphereMetrics() throws SensorException;
}
