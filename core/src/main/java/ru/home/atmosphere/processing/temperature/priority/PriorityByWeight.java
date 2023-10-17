package ru.home.atmosphere.processing.temperature.priority;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.home.atmosphere.atmosphere_metrics.Temperature;
import ru.home.atmosphere.processing.ProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PriorityByWeight implements PriorityTemperature {

    private final static Logger LOGGER = LogManager.getLogger(PriorityByWeight.class);
    private final Map<String, Integer> priorityConfiguration;

    public PriorityByWeight(Map<String, Integer> priorityConfiguration) {
        this.priorityConfiguration = priorityConfiguration;
    }

    @Override
    public Temperature compute(Map<String, Temperature> allTemperatureMetrics) throws ProcessingException {
        LOGGER.info("Chose metric from sensor with max priorityWeight.");
        if (checkIsComputationUnavailable(allTemperatureMetrics)) {
            throw new ProcessingException(String.format("Can not compute priority temperature for metrics: %s with configuration: %s.",
                    allTemperatureMetrics, priorityConfiguration));
        }
        return choseMetricFromPrioritySensor(allTemperatureMetrics);
    }

    private boolean checkIsComputationUnavailable(Map<String, Temperature> allTemperatureMetrics) {
        return !priorityConfiguration.keySet().containsAll(allTemperatureMetrics.keySet());
    }

    private Temperature choseMetricFromPrioritySensor(Map<String, Temperature> allTemperatureMetrics) {
        Temperature priorityTemperature = null;
        int comparingValue = Integer.MAX_VALUE;
        String sensorId;
        List<Map.Entry<String, Integer>> configurationEntries = new ArrayList<>(priorityConfiguration.entrySet());
        for (int i = 0; i < configurationEntries.size(); i++) {
            sensorId = configurationEntries.get(i).getKey();
            int priority = configurationEntries.get(i).getValue();
            LOGGER.info("PriorityWeight from configuration. Sensor: {} priorityWeight: {}.", sensorId, priority);
            if (allTemperatureMetrics.containsKey(sensorId)) {
                LOGGER.info("Compare priorityWeight. PriorityWeight from sensor: {} is {}. Compare with value {}.", sensorId, priority, comparingValue);
                if (priority < comparingValue) {
                    comparingValue = priority;
                    priorityTemperature = allTemperatureMetrics.get(sensorId);
                    LOGGER.info("Temperature from sensor: {} is more priority.", sensorId);
                }
            } else {
                LOGGER.info("Metrics not contain temperature from sensor: {}.", sensorId);
            }
        }
        return priorityTemperature;
    }
}
