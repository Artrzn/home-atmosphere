package ru.home.atmosphere.sensor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.home.atmosphere.atmosphere_metrics.*;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HomeAtmosphereTests {

    private final String sensorName1 = "room1";
    private final String sensorName2 = "room2";
    private final String sensorName3 = "room3";
    private AtmosphereSensor sensor1;
    private AtmosphereSensor sensor2;
    private AtmosphereSensor sensor3;
    private HomeAtmosphere homeAtmosphere;

    @BeforeEach
    public void init() {
        sensor1 = mock(AtmosphereSensor.class);
        sensor2 = mock(AtmosphereSensor.class);
        sensor3 = mock(AtmosphereSensor.class);
        Map<String, AtmosphereSensor> sensors = Map.of(sensorName1, sensor1, sensorName2, sensor2, sensorName3, sensor3);
        homeAtmosphere = new HomeAtmosphere(sensors);
    }

    @Test
    public void getMetrics_normalState_metricsReturned() throws SensorException {
        RoomAtmosphereMetrics room1Metrics = getRandomMetrics();
        RoomAtmosphereMetrics room2Metrics = getRandomMetrics();
        RoomAtmosphereMetrics room3Metrics = getRandomMetrics();
        when(sensor1.getAtmosphereMetrics()).thenReturn(room1Metrics);
        when(sensor2.getAtmosphereMetrics()).thenReturn(room2Metrics);
        when(sensor3.getAtmosphereMetrics()).thenReturn(room3Metrics);

        Optional<HomeAtmosphereMetrics> result = homeAtmosphere.getMetrics();

        Map<String, Temperature> temperature = result.get().getTemperature();
        Map<String, Humidity> humidity = result.get().getHumidity();
        Map<String, Co2PPM> co2PPMMap = result.get().getCo2PPM();
        assertEquals(room1Metrics.getTemperatureFromDS(), temperature.get(sensorName1).getValue());
        assertEquals(room2Metrics.getTemperatureFromDS(), temperature.get(sensorName2).getValue());
        assertEquals(room3Metrics.getTemperatureFromDS(), temperature.get(sensorName3).getValue());
        assertEquals(room1Metrics.getHumidityFromDHT(), humidity.get(sensorName1).getValue());
        assertEquals(room2Metrics.getHumidityFromDHT(), humidity.get(sensorName2).getValue());
        assertEquals(room3Metrics.getHumidityFromDHT(), humidity.get(sensorName3).getValue());
        assertEquals(room1Metrics.getCo2ppm(), co2PPMMap.get(sensorName1).getValue());
        assertEquals(room2Metrics.getCo2ppm(), co2PPMMap.get(sensorName2).getValue());
        assertEquals(room3Metrics.getCo2ppm(), co2PPMMap.get(sensorName3).getValue());
        verify(sensor1, times(1)).getAtmosphereMetrics();
        verify(sensor2, times(1)).getAtmosphereMetrics();
        verify(sensor3, times(1)).getAtmosphereMetrics();
    }

    @Test
    public void getMetrics_exceptionFromOneOfSensors_metricsReturnedFromWorkingSensors() throws SensorException {
        RoomAtmosphereMetrics room1Metrics = getRandomMetrics();
        RoomAtmosphereMetrics room3Metrics = getRandomMetrics();
        when(sensor1.getAtmosphereMetrics()).thenReturn(room1Metrics);
        when(sensor2.getAtmosphereMetrics()).thenThrow(SensorException.class);
        when(sensor3.getAtmosphereMetrics()).thenReturn(room3Metrics);

        Optional<HomeAtmosphereMetrics> result = homeAtmosphere.getMetrics();

        Map<String, Temperature> temperature = result.get().getTemperature();
        Map<String, Humidity> humidity = result.get().getHumidity();
        Map<String, Co2PPM> co2PPMMap = result.get().getCo2PPM();
        assertFalse(temperature.containsKey(sensorName2));
        assertFalse(humidity.containsKey(sensorName2));
        assertFalse(co2PPMMap.containsKey(sensorName2));
        assertEquals(room1Metrics.getTemperatureFromDS(), temperature.get(sensorName1).getValue());
        assertEquals(room3Metrics.getTemperatureFromDS(), temperature.get(sensorName3).getValue());
        assertEquals(room1Metrics.getHumidityFromDHT(), humidity.get(sensorName1).getValue());
        assertEquals(room3Metrics.getHumidityFromDHT(), humidity.get(sensorName3).getValue());
        assertEquals(room1Metrics.getCo2ppm(), co2PPMMap.get(sensorName1).getValue());
        assertEquals(room3Metrics.getCo2ppm(), co2PPMMap.get(sensorName3).getValue());
        verify(sensor1, times(1)).getAtmosphereMetrics();
        verify(sensor2, times(1)).getAtmosphereMetrics();
        verify(sensor3, times(1)).getAtmosphereMetrics();
    }

    @Test
    public void getMetrics_allSensorsAreUnavailable_emptyOptionalReturned() throws SensorException {
        when(sensor1.getAtmosphereMetrics()).thenThrow(SensorException.class);
        when(sensor2.getAtmosphereMetrics()).thenThrow(SensorException.class);
        when(sensor3.getAtmosphereMetrics()).thenThrow(SensorException.class);

        Optional<HomeAtmosphereMetrics> result = homeAtmosphere.getMetrics();

        assertTrue(result.isEmpty());
        verify(sensor1, times(1)).getAtmosphereMetrics();
        verify(sensor2, times(1)).getAtmosphereMetrics();
        verify(sensor3, times(1)).getAtmosphereMetrics();
    }


    private RoomAtmosphereMetrics getRandomMetrics() {
        Random random = new Random();
        return new RoomAtmosphereMetrics() {{
            setTemperatureFromDS(random.nextFloat());
            setTemperatureFrimDHT(random.nextFloat());
            setCo2ppm(random.nextInt());
            setHumidityFromDHT(random.nextFloat());
        }};
    }

}
