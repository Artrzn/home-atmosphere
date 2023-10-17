package ru.home.atmosphere.processing.temperature.priority;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.home.atmosphere.atmosphere_metrics.Temperature;
import ru.home.atmosphere.processing.ProcessingException;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PriorityByTimePeriodTests {

    private static final List<String> sensorIds = List.of("s1", "s2", "s3");
    private static Map<String, LocalTime[]> timeBySensors;
    private PriorityByTimePeriod priority;


    @Test
    public void construct_intersectionsOfConfigurations_exceptionThrown() {
        timeBySensors = Map.of(
                sensorIds.get(0), new LocalTime[]{LocalTime.of(0, 0), LocalTime.of(12, 0)},
                sensorIds.get(1), new LocalTime[]{LocalTime.of(11, 0), LocalTime.of(14, 0)},
                sensorIds.get(2), new LocalTime[]{LocalTime.of(14, 2), LocalTime.of(15, 0)}
        );

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> new PriorityByTimePeriod(timeBySensors));

        String id = sensorIds.get(0);
        String nextId = sensorIds.get(1);
        assertEquals(String.format("Period(from: %s to: %s) for sensor: %s intersects with period(from: %s to: %s) for sensor: %s.",
                timeBySensors.get(id)[0], timeBySensors.get(id)[1], id, timeBySensors.get(nextId)[0], timeBySensors.get(nextId)[1], nextId), runtimeException.getMessage());
    }

    @Test
    public void construct_oneOfPeriodsNotContainTwoElements_exceptionThrown() {
        timeBySensors = Map.of(
                sensorIds.get(0), new LocalTime[]{LocalTime.of(12, 0), LocalTime.of(15, 0)},
                sensorIds.get(1), new LocalTime[]{LocalTime.of(16, 0), LocalTime.of(17, 0)},
                sensorIds.get(2), new LocalTime[]{LocalTime.of(18, 0)}
        );

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> new PriorityByTimePeriod(timeBySensors));

        assertEquals(String.format("Period for sensor: %s contain only 1 element, but must contaion 2 elements.", sensorIds.get(2)), runtimeException.getMessage());
    }

    @Test
    public void construct_oneOfPeriodsIsNull_exceptionThrown() {
        timeBySensors = new HashMap<>();
        timeBySensors.put(sensorIds.get(0), new LocalTime[]{LocalTime.of(12, 0), LocalTime.of(15, 0)});
        timeBySensors.put(sensorIds.get(1), null);
        timeBySensors.put(sensorIds.get(2), new LocalTime[]{LocalTime.of(16, 0), LocalTime.of(17, 0)});

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> new PriorityByTimePeriod(timeBySensors));

        assertEquals(String.format("Period for sensor: %s is not defined.", sensorIds.get(1)), runtimeException.getMessage());
    }

    @Test
    public void construct_periodsNotCoveredDay_exceptionThrown() {
        timeBySensors = Map.of(
                sensorIds.get(0), new LocalTime[]{LocalTime.of(0, 0), LocalTime.of(23, 0)},
                sensorIds.get(1), new LocalTime[]{LocalTime.of(23, 0), LocalTime.of(23, 57)},
                sensorIds.get(2), new LocalTime[]{LocalTime.of(23, 57), LocalTime.of(23, 58)}
        );

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> new PriorityByTimePeriod(timeBySensors));

        assertEquals(String.format("Time %s:%s not covered in configs for sensors.", 23, 59), runtimeException.getMessage());
    }

    @Test
    public void compute_wrongSensorsId_exceptionThrown() {
        timeBySensors = Map.of(
                sensorIds.get(0), new LocalTime[]{LocalTime.of(0, 0), LocalTime.of(23, 59)}
        );
        priority = new PriorityByTimePeriod(timeBySensors);
        Map<String, Temperature> temperatureMetrics = new HashMap<>();
        for (int i = 0; i < sensorIds.size(); i++) {
            Temperature temperature = new Temperature();
            temperature.setValue(10.0f + i);
            temperatureMetrics.put(sensorIds.get(i), temperature);
        }

        ProcessingException processingException = assertThrows(ProcessingException.class, () -> priority.compute(temperatureMetrics));

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
        assertEquals(String.format("Can not compute priority temperature for metrics: %s with timeBySensors configuration: %s.",
                temperatureMetrics, stringBuilder), processingException.getMessage());
    }

    @Test
    public void compute_emptyTemperatureMap_exceptionThrown() {
        timeBySensors = Map.of(
                sensorIds.get(0), new LocalTime[]{LocalTime.of(0, 0), LocalTime.of(23, 59)}
        );
        priority = new PriorityByTimePeriod(timeBySensors);

        ProcessingException processingException = assertThrows(ProcessingException.class, () -> priority.compute(new HashMap<>()));

        assertEquals("Empty temperature map.", processingException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("prepareArguments")
    public void compute_normalState_priorityTemperatureReturned(Map<String, LocalTime[]> timeBySensors, Map<String, Temperature> temperatureBySensors, LocalTime expectedTime, Temperature expected) throws ProcessingException {
        priority = new PriorityByTimePeriod(timeBySensors);
        try (MockedStatic<LocalTime> mock = Mockito.mockStatic(LocalTime.class)) {
            mock.when(LocalTime::now).thenReturn(expectedTime);

            Temperature result = priority.compute(temperatureBySensors);

            List<String> ids = new ArrayList<>(timeBySensors.keySet());
            StringBuilder periodString = new StringBuilder("[");
            for (String id : ids) {
                String temperature = String.valueOf(temperatureBySensors.get(id).getValue());
                String from = timeBySensors.get(id)[0].toString();
                String to = timeBySensors.get(id)[1].toString();
                periodString.append(temperature)
                        .append(" ")
                        .append(from)
                        .append("-")
                        .append(to)
                        .append(", ");
            }
            periodString.replace(periodString.length() - 2, periodString.length(), "");
            periodString.append("]");
            assertEquals(expected, result, String.format("Expected temperature by periods: %s, testTime: %s.", periodString, expectedTime));
        }
    }

    public static Stream<Arguments> prepareArguments() {
        Map<String, LocalTime[]> timeBySensors = Map.of(
                sensorIds.get(0), new LocalTime[]{LocalTime.of(2, 0), LocalTime.of(9, 59)},
                sensorIds.get(1), new LocalTime[]{LocalTime.of(10, 0), LocalTime.of(17, 59)},
                sensorIds.get(2), new LocalTime[]{LocalTime.of(18, 0), LocalTime.of(1, 59)}
        );
        Temperature t1 = new Temperature();
        t1.setValue(1.0f);
        Temperature t2 = new Temperature();
        t2.setValue(2.0f);
        Temperature t3 = new Temperature();
        t3.setValue(3.0f);
        Map<String, Temperature> temperatureBySensors = Map.of(
                sensorIds.get(0), t1,
                sensorIds.get(1), t2,
                sensorIds.get(2), t3
        );
        return Stream.of(
                Arguments.of(
                        timeBySensors,
                        temperatureBySensors,
                        LocalTime.of(2, 0),
                        t1
                ),
                Arguments.of(
                        timeBySensors,
                        temperatureBySensors,
                        LocalTime.of(3, 50),
                        t1
                ),
                Arguments.of(
                        timeBySensors,
                        temperatureBySensors,
                        LocalTime.of(9, 59),
                        t1
                ),
                Arguments.of(
                        timeBySensors,
                        temperatureBySensors,
                        LocalTime.of(10, 0),
                        t2
                ),
                Arguments.of(
                        timeBySensors,
                        temperatureBySensors,
                        LocalTime.of(11, 50),
                        t2
                ),
                Arguments.of(
                        timeBySensors,
                        temperatureBySensors,
                        LocalTime.of(17, 59),
                        t2
                ),
                Arguments.of(
                        timeBySensors,
                        temperatureBySensors,
                        LocalTime.of(18, 0),
                        t3
                ),
                Arguments.of(
                        timeBySensors,
                        temperatureBySensors,
                        LocalTime.of(0, 50),
                        t3
                ),
                Arguments.of(
                        timeBySensors,
                        temperatureBySensors,
                        LocalTime.of(1, 59),
                        t3
                )
        );
    }

}
