package ru.home.atmosphere.processing.temperature.priority;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.home.atmosphere.atmosphere_metrics.Temperature;
import ru.home.atmosphere.processing.ProcessingException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PriorityByWeightTests {

    private static final List<String> sensorIds = List.of("s1", "s2", "s3");
    private static PriorityByWeight priorityByWeight;

    @BeforeAll
    public static void init() {
        Map<String, Integer> priorityById = new HashMap<>();
        for (int i = 0; i < sensorIds.size(); i++) {
            priorityById.put(sensorIds.get(i), i + 1);
        }
        priorityByWeight = new PriorityByWeight(priorityById);
    }

    @Test
    public void compute_wrongSensorsId_exceptionThrown() {
        Map<String, Temperature> temperatureMetrics = new HashMap<>();
        for (int i = 0; i < sensorIds.size(); i++) {
            Temperature temperature = new Temperature();
            temperature.setValue(10.0f + i);
            temperatureMetrics.put(sensorIds.get(i) + "wrong", temperature);
        }

        assertThrows(ProcessingException.class, () -> priorityByWeight.compute(temperatureMetrics));
    }

    @Test
    public void compute_notAllExpectedMetrics_returnedFromNext() throws ProcessingException {
        Map<String, Temperature> temperatureMetrics = new HashMap<>();
        for (int i = 0; i < sensorIds.size(); i++) {
            if (i != 0) {
                Temperature temperature = new Temperature();
                temperature.setValue(10.0f + i);
                temperatureMetrics.put(sensorIds.get(i), temperature);
            }
        }

        Temperature result = priorityByWeight.compute(temperatureMetrics);

        assertEquals(temperatureMetrics.get(sensorIds.get(1)).getValue(), result.getValue(),
                "First priority metrics is not exist, returned value must be from second metrics.");
    }

    @Test
    public void compute_allMetricsIsPresent_returnedFirstTemperature() throws ProcessingException {
        Map<String, Temperature> temperatureMetrics = new HashMap<>();
        for (int i = 0; i < sensorIds.size(); i++) {
            Temperature temperature = new Temperature();
            temperature.setValue(10.0f + i);
            temperatureMetrics.put(sensorIds.get(i), temperature);
        }

        Temperature result = priorityByWeight.compute(temperatureMetrics);

        assertEquals(temperatureMetrics.get(sensorIds.get(0)).getValue(), result.getValue(),
                "Returned value must be from first metrics.");
    }
}
