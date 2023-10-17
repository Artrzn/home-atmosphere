package ru.home.atmosphere.processing.humidity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.home.atmosphere.atmosphere_metrics.Humidity;
import ru.home.atmosphere.log.MetricsLogWriter;
import ru.home.atmosphere.processing.ProcessingException;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class HumidityProcessingTests {

    private final Humidity humidity1 = new Humidity();
    private final Humidity humidity2 = new Humidity();
    private final Humidity humidity3 = new Humidity();
    private MetricsLogWriter<HumidityLogMessage> humidityLog;
    private HumidityProcessing processing;

    @BeforeEach
    public void init() {
        humidityLog = mock(MetricsLogWriter.class);
        processing = new HumidityProcessing(humidityLog);
    }

    private Map<String, Humidity> getMetrics() {
        humidity1.setValue(10.0f);
        humidity2.setValue(20.0f);
        humidity3.setValue(30.0f);
        String sensor1Id = "s1";
        String sensor2Id = "s2";
        String sensor3Id = "s3";
        return Map.of(sensor1Id, humidity1, sensor2Id, humidity2, sensor3Id, humidity3);
    }

    @Test
    public void process_normalState_humidityLogged() throws ProcessingException {
        Map<String, Humidity> metrics = getMetrics();
        ArgumentCaptor<List<HumidityLogMessage>> logMessages = ArgumentCaptor.forClass(List.class);

        processing.process(metrics);

        verify(humidityLog, times(1)).log(logMessages.capture());
        List<HumidityLogMessage> loggedMessages = logMessages.getValue();
        assertEquals(metrics.size(), loggedMessages.size());
        Set<Timestamp> timestamps = new HashSet<>();
        for (HumidityLogMessage logMessage : loggedMessages) {
            Humidity sourceHumidity = metrics.get(logMessage.getSensorId());
            assertEquals(sourceHumidity, logMessage.getMetric());
            timestamps.add(logMessage.getMeasureTimestamp());
        }
        assertEquals(1, timestamps.size());//убеждаемся что везде использован один timestamp
        verifyNoMoreInteractions(humidityLog);
    }


    @Test
    public void process_exceptionsWhileLog_exceptionThrown() {
        Map<String, Humidity> metrics = getMetrics();
        doThrow(RuntimeException.class).when(humidityLog).log(any(List.class));

        assertThrows(ProcessingException.class, () -> processing.process(metrics));
        verify(humidityLog, times(1)).log(any(List.class));
        verifyNoMoreInteractions(humidityLog);
    }
}
