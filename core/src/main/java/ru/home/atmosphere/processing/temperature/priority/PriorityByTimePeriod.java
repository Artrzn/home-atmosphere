package ru.home.atmosphere.processing.temperature.priority;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.home.atmosphere.atmosphere_metrics.Temperature;
import ru.home.atmosphere.processing.ProcessingException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PriorityByTimePeriod implements PriorityTemperature {

    private final static Logger LOGGER = LogManager.getLogger(PriorityByTimePeriod.class);
    private final Map<String, LocalTime[]> timeBySensors;

    public PriorityByTimePeriod(Map<String, LocalTime[]> timeBySensors) {
        checkConfiguration(timeBySensors);
        this.timeBySensors = timeBySensors;
    }

    private void checkConfiguration(Map<String, LocalTime[]> timeBySensors) {
        List<String> ids = new ArrayList<>(timeBySensors.keySet());
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            LocalTime[] period = timeBySensors.get(id);
            checkPeriod(period, id);
            LocalTime from = period[0];
            LocalTime to = period[1];
            for (int ii = 0; ii < ids.size(); ii++) {
                if (ii == i) continue;
                String nextId = ids.get(ii);
                LocalTime[] nextPeriod = timeBySensors.get(nextId);
                checkPeriod(nextPeriod, nextId);
                LocalTime nextFrom = nextPeriod[0];
                LocalTime nextTo = nextPeriod[1];
                if (from.isBefore(nextFrom) && to.isAfter(nextFrom)) {
                    throw new RuntimeException(String.format("Period(from: %s to: %s) for sensor: %s intersects with period(from: %s to: %s) for sensor: %s.",
                            from, to, id, nextFrom, nextTo, nextId));
                }
            }
        }
        checkCoverage(ids, timeBySensors);
    }

    private void checkPeriod(LocalTime[] period, String sensorId) {
        if (period == null) {
            throw new RuntimeException(String.format("Period for sensor: %s is not defined.", sensorId));
        }
        if (period.length < 2) {
            throw new RuntimeException(String.format("Period for sensor: %s contain only %s element, but must contaion 2 elements.", sensorId, period.length));
        }
    }

    private void checkCoverage(List<String> ids, Map<String, LocalTime[]> timeBySensors) {
        for (int hour = 0; hour <= 23; hour++) {
            minutes:
            for (int minute = 0; minute <= 59; minute++) {
                LocalTime checkedMinute = LocalTime.of(hour, minute);
                for (String id : ids) {
                    LocalTime[] period = timeBySensors.get(id);
                    LocalTime from = period[0];
                    LocalTime to = period[1];
                    if (from.isAfter(to)) {
                        if (checkedMinute.isAfter(from) || checkedMinute.isBefore(to) || checkedMinute.equals(from) || checkedMinute.equals(to)) {
                            continue minutes;
                        }
                    } else {
                        if ((checkedMinute.isAfter(from) || checkedMinute.equals(from)) && ((checkedMinute.isBefore(to)) || checkedMinute.equals(to))) {
                            continue minutes;
                        }
                    }
                }
                throw new RuntimeException(String.format("Time %s:%s not covered in configs for sensors.", hour, minute));
            }
        }
    }

    @Override
    public Temperature compute(Map<String, Temperature> allTemperatureMetrics) throws ProcessingException {
        LOGGER.info("Compute more priority temperature by time period.");
        checkIsComputationUnavailable(allTemperatureMetrics);
        Temperature result = null;
        for (String sensorId : allTemperatureMetrics.keySet()) {
            result = allTemperatureMetrics.get(sensorId);
            LocalTime currentTime = LocalTime.now();
            LocalTime[] localTimes = timeBySensors.get(sensorId);
            LocalTime from = localTimes[0];
            LocalTime to = localTimes[1];
            if (from.isAfter(to) && (currentTime.isAfter(from) || currentTime.isBefore(to) || currentTime.equals(from) || currentTime.equals(to))) {
                LOGGER.info("More priority temperature for current time: {} is from sensor: {}.", currentTime, sensorId);
                break;
            } else if ((currentTime.isAfter(from) || currentTime.equals(from)) && (currentTime.isBefore(to)) || currentTime.equals(to)) {
                LOGGER.info("More priority temperature for current time: {} is from sensor: {}.", currentTime, sensorId);
                break;
            } else {
                LOGGER.info("Temperature from sensor: {} have less priority, because current time not in period: {}-{}.", sensorId, from, to);
            }
        }
        return result;
    }

    private void checkIsComputationUnavailable(Map<String, Temperature> allTemperatureMetrics) throws ProcessingException {
        if (allTemperatureMetrics.isEmpty()) {
            throw new ProcessingException("Empty temperature map.");
        }
        if (!timeBySensors.keySet().containsAll(allTemperatureMetrics.keySet())) {
            throw new ProcessingException(String.format("Can not compute priority temperature for metrics: %s with timeBySensors configuration: %s.",
                    allTemperatureMetrics, getStringOfTimeBySensors()));
        }
    }

    private String getStringOfTimeBySensors() {
        StringBuilder stringBuilder = new StringBuilder("{");
        for (String sensorId : timeBySensors.keySet()) {
            LocalTime[] localTimes = timeBySensors.get(sensorId);
            stringBuilder.append(sensorId)
                    .append("=")
                    .append(Arrays.toString(localTimes))
                    .append(",");
        }
        stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
