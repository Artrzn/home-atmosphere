package ru.home.atmosphere.atmosphere_metrics;

import java.util.Map;

public class HomeAtmosphereMetrics {

    private Map<String, Temperature> temperature;
    private Map<String, Humidity> humidity;
    private Map<String, Co2PPM> co2PPM;

    public Map<String, Temperature> getTemperature() {
        return temperature;
    }

    public void setTemperature(Map<String, Temperature> temperature) {
        this.temperature = temperature;
    }

    public Map<String, Humidity> getHumidity() {
        return humidity;
    }

    public void setHumidity(Map<String, Humidity> humidity) {
        this.humidity = humidity;
    }

    public Map<String, Co2PPM> getCo2PPM() {
        return co2PPM;
    }

    public void setCo2PPM(Map<String, Co2PPM> co2PPM) {
        this.co2PPM = co2PPM;
    }
}
