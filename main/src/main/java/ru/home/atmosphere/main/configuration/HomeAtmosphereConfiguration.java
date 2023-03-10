package ru.home.atmosphere.main.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.co2.Co2LogMessage;
import ru.home.atmosphere.processing.co2.Co2Processing;
import ru.home.atmosphere.processing.humidity.HumidityLogMessage;
import ru.home.atmosphere.processing.humidity.HumidityProcessing;
import ru.home.atmosphere.processing.temperature.HeaterMode;
import ru.home.atmosphere.processing.temperature.PriorityTemperature;
import ru.home.atmosphere.processing.temperature.TemperatureLogMessage;
import ru.home.atmosphere.processing.temperature.TemperatureProcessing;
import ru.home.atmosphere.processing.temperature.heater_relay.HeaterRelay;
import ru.home.atmosphere.processing.temperature.heater_relay.HttpHeaterRelay;
import ru.home.atmosphere.processing.temperature.heater_relay.RelayHttpClient;
import ru.home.atmosphere.sensor.AtmosphereSensor;
import ru.home.atmosphere.sensor.HomeAtmosphere;
import ru.home.atmosphere.sensor.http.HttpAtmosphereSensor;
import ru.home.atmosphere.main.SensorWithTemperatureCoefficientWrapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "server")
public class HomeAtmosphereConfiguration {

    private Map<String, Map<String, String>> sensors;
    private float expectedTemperature;
    private float hysteresis;
    private String relayUrl;

    @Bean
    public HomeAtmosphere getHomeAtmosphere() throws URISyntaxException {
        return new HomeAtmosphere(getAtmosphereSensors());
    }

    public Map<String, AtmosphereSensor> getAtmosphereSensors() throws URISyntaxException {
        Map<String, AtmosphereSensor> resultSensors = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : sensors.entrySet()) {
            URI uri = new URI(entry.getValue().get("url"));
            HttpClient httpClient = HttpClient.newBuilder().build();
            float temperatureCoefficient = Float.parseFloat(entry.getValue().get("temperatureCoefficient"));
            AtmosphereSensor wrappedSensor = new HttpAtmosphereSensor(uri, httpClient);
            resultSensors.put(entry.getKey(), new SensorWithTemperatureCoefficientWrapper(temperatureCoefficient, wrappedSensor));
        }
        return resultSensors;
    }

    @Bean
    public TemperatureProcessing getTemperatureProcessing(MetricsLogWriter<TemperatureLogMessage> metricsLogWriter) {
        PriorityTemperature priorityTemperature = getPriorityTemperature();
        HeaterMode heaterMode = new HeaterMode(expectedTemperature, hysteresis);
        HeaterRelay heaterRelay = new HttpHeaterRelay(relayUrl, new RelayHttpClient());//todo привести к общему виду
        return new TemperatureProcessing(priorityTemperature, heaterMode, heaterRelay, metricsLogWriter);
    }

    public PriorityTemperature getPriorityTemperature() {
        Map<String, Integer> prioritySensors = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : sensors.entrySet()) {
            prioritySensors.put(entry.getKey(), Integer.valueOf(entry.getValue().get("priority")));
        }
        return new PriorityTemperature(prioritySensors);
    }

    @Bean
    public HumidityProcessing getHumidityProcessing(MetricsLogWriter<HumidityLogMessage> metricsLogWriter) {
        return new HumidityProcessing(metricsLogWriter);
    }

    @Bean
    public Co2Processing getCo2Processing(MetricsLogWriter<Co2LogMessage> metricsLogWriter) {
        return new Co2Processing(metricsLogWriter);
    }

    public Map<String, Map<String, String>> getSensors() {
        return sensors;
    }

    public void setSensors(Map<String, Map<String, String>> sensors) {
        this.sensors = sensors;
    }

    public float getExpectedTemperature() {
        return expectedTemperature;
    }

    public void setExpectedTemperature(float expectedTemperature) {
        this.expectedTemperature = expectedTemperature;
    }

    public float getHysteresis() {
        return hysteresis;
    }

    public void setHysteresis(float hysteresis) {
        this.hysteresis = hysteresis;
    }

    public String getRelayUrl() {
        return relayUrl;
    }

    public void setRelayUrl(String relayUrl) {
        this.relayUrl = relayUrl;
    }
}
