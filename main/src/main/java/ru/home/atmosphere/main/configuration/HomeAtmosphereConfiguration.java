package ru.home.atmosphere.main.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.main.SensorWithTemperatureCoefficientWrapper;
import ru.home.atmosphere.processing.co2.Co2LogMessage;
import ru.home.atmosphere.processing.co2.Co2Processing;
import ru.home.atmosphere.processing.humidity.HumidityLogMessage;
import ru.home.atmosphere.processing.humidity.HumidityProcessing;
import ru.home.atmosphere.processing.temperature.HeaterMode;
import ru.home.atmosphere.processing.temperature.TemperatureLogMessage;
import ru.home.atmosphere.processing.temperature.TemperatureProcessing;
import ru.home.atmosphere.processing.temperature.heater_relay.HeaterRelay;
import ru.home.atmosphere.processing.temperature.heater_relay.HttpHeaterRelay;
import ru.home.atmosphere.processing.temperature.heater_relay.RelayHttpClient;
import ru.home.atmosphere.processing.temperature.priority.PriorityByTimePeriod;
import ru.home.atmosphere.processing.temperature.priority.PriorityByWeight;
import ru.home.atmosphere.processing.temperature.priority.PriorityTemperature;
import ru.home.atmosphere.sensor.AtmosphereSensor;
import ru.home.atmosphere.sensor.HomeAtmosphere;
import ru.home.atmosphere.sensor.http.HttpAtmosphereSensor;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "server")
public class HomeAtmosphereConfiguration {

    public static class Sensor {
        private String url;
        private float temperatureCoefficient;
        private int priorityWeight;
        private String[] priorityTimePeriod;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public float getTemperatureCoefficient() {
            return temperatureCoefficient;
        }

        public void setTemperatureCoefficient(float temperatureCoefficient) {
            this.temperatureCoefficient = temperatureCoefficient;
        }

        public int getPriorityWeight() {
            return priorityWeight;
        }

        public void setPriorityWeight(int priorityWeight) {
            this.priorityWeight = priorityWeight;
        }

        public String[] getPriorityTimePeriod() {
            return priorityTimePeriod;
        }

        public void setPriorityTimePeriod(String[] priorityTimePeriod) {
            this.priorityTimePeriod = priorityTimePeriod;
        }
    }

    private Map<String, Sensor> sensors;
    private float expectedTemperature;
    private float hysteresis;
    private String relayUrl;

    @Bean
    public HomeAtmosphere getHomeAtmosphere() throws URISyntaxException {
        return new HomeAtmosphere(getAtmosphereSensors());
    }

    public Map<String, AtmosphereSensor> getAtmosphereSensors() throws URISyntaxException {
        Map<String, AtmosphereSensor> resultSensors = new HashMap<>();
        for (Map.Entry<String, Sensor> entry : sensors.entrySet()) {
            URI uri = new URI(entry.getValue().getUrl());
            HttpClient httpClient = HttpClient.newBuilder().build();
            float temperatureCoefficient = entry.getValue().getTemperatureCoefficient();
            AtmosphereSensor wrappedSensor = new HttpAtmosphereSensor(uri, httpClient);
            resultSensors.put(entry.getKey(), new SensorWithTemperatureCoefficientWrapper(temperatureCoefficient, wrappedSensor));
        }
        return resultSensors;
    }

    @Bean
    public TemperatureProcessing getTemperatureProcessing(MetricsLogWriter<TemperatureLogMessage> metricsLogWriter) {
        PriorityTemperature priorityByWeight = getPriorityTemperature();
        HeaterMode heaterMode = new HeaterMode(expectedTemperature, hysteresis);
        HeaterRelay heaterRelay = new HttpHeaterRelay(relayUrl, new RelayHttpClient());//todo привести к общему виду
        return new TemperatureProcessing(priorityByWeight, heaterMode, heaterRelay, metricsLogWriter);
    }

    public PriorityTemperature getPriorityTemperature() {
        boolean byTimePeriod = sensors.values().stream()
                .allMatch(sensor -> sensor.getPriorityTimePeriod() != null);
        if (byTimePeriod) {
            Map<String, LocalTime[]> priorityByTime = new HashMap<>();
            for (Map.Entry<String, Sensor> entry : sensors.entrySet()) {
                String[] periodString = entry.getValue().getPriorityTimePeriod();
                int fromHour = Integer.parseInt(periodString[0].split(":")[0]);
                int fromMinutes = Integer.parseInt(periodString[0].split(":")[1]);
                int toHour = Integer.parseInt(periodString[1].split(":")[0]);
                int toMinutes = Integer.parseInt(periodString[1].split(":")[1]);
                priorityByTime.put(entry.getKey(), new LocalTime[]{LocalTime.of(fromHour, fromMinutes), LocalTime.of(toHour, toMinutes)});
            }
            return new PriorityByTimePeriod(priorityByTime);
        } else {
            Map<String, Integer> prioritySensors = new HashMap<>();
            for (Map.Entry<String, Sensor> entry : sensors.entrySet()) {
                prioritySensors.put(entry.getKey(), entry.getValue().getPriorityWeight());
            }
            return new PriorityByWeight(prioritySensors);
        }
    }

    @Bean
    public HumidityProcessing getHumidityProcessing(MetricsLogWriter<HumidityLogMessage> metricsLogWriter) {
        return new HumidityProcessing(metricsLogWriter);
    }

    @Bean
    public Co2Processing getCo2Processing(MetricsLogWriter<Co2LogMessage> metricsLogWriter) {
        return new Co2Processing(metricsLogWriter);
    }

    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(Map<String, Sensor> sensors) {
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
