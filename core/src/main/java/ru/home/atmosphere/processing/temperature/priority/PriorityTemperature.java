package ru.home.atmosphere.processing.temperature.priority;

import ru.home.atmosphere.atmosphere_metrics.Temperature;
import ru.home.atmosphere.processing.ProcessingException;

import java.util.Map;

public interface PriorityTemperature {
    Temperature compute(Map<String, Temperature> allTemperatureMetrics) throws ProcessingException;
}
