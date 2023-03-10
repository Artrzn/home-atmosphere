package ru.home.atmosphere.atmosphere_metrics;

import java.util.Objects;

public class RoomAtmosphereMetrics {//todo rename metrics

    private float temperatureFromDS;
    private float temperatureFrimDHT;//todo fix "frim"
    private float humidityFromDHT;
    private int co2ppm;

    public float getTemperatureFromDS() {
        return temperatureFromDS;
    }

    public void setTemperatureFromDS(float temperatureFromDS) {
        this.temperatureFromDS = temperatureFromDS;
    }

    public float getTemperatureFrimDHT() {
        return temperatureFrimDHT;
    }

    public void setTemperatureFrimDHT(float temperatureFrimDHT) {
        this.temperatureFrimDHT = temperatureFrimDHT;
    }

    public float getHumidityFromDHT() {
        return humidityFromDHT;
    }

    public void setHumidityFromDHT(float humidityFromDHT) {
        this.humidityFromDHT = humidityFromDHT;
    }

    public int getCo2ppm() {
        return co2ppm;
    }

    public void setCo2ppm(int co2ppm) {
        this.co2ppm = co2ppm;
    }

    @Override
    public String toString() {
        return "RoomAtmosphereMetrics{" +
                "temperatureFromDS=" + temperatureFromDS +
                ", temperatureFrimDHT=" + temperatureFrimDHT +
                ", humidityFromDHT=" + humidityFromDHT +
                ", co2ppm=" + co2ppm +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomAtmosphereMetrics that = (RoomAtmosphereMetrics) o;
        return Float.compare(that.temperatureFromDS, temperatureFromDS) == 0 && Float.compare(that.temperatureFrimDHT, temperatureFrimDHT) == 0 && Float.compare(that.humidityFromDHT, humidityFromDHT) == 0 && co2ppm == that.co2ppm;
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperatureFromDS, temperatureFrimDHT, humidityFromDHT, co2ppm);
    }
}
