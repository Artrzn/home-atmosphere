package ru.home.atmosphere.processing.co2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.home.atmosphere.atmosphere_metrics.Co2PPM;
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

public class Co2ProcessingTests {

    private final Co2PPM co2PPM1 = new Co2PPM();
    private final Co2PPM co2PPM2 = new Co2PPM();
    private final Co2PPM co2PPM3 = new Co2PPM();
    private MetricsLogWriter<Co2LogMessage> co2Log;
    private Co2Processing processing;

    @BeforeEach
    public void init() {
        co2Log = mock(MetricsLogWriter.class);
        processing = new Co2Processing(co2Log);
    }

    private Map<String, Co2PPM> getMetrics() {
        co2PPM1.setValue(10);
        co2PPM2.setValue(20);
        co2PPM3.setValue(30);
        String sensor1Id = "s1";
        String sensor2Id = "s2";
        String sensor3Id = "s3";
        return Map.of(sensor1Id, co2PPM1, sensor2Id, co2PPM2, sensor3Id, co2PPM3);
    }

    @Test
    public void process_normalState_humidityLogged() throws ProcessingException {
        Map<String, Co2PPM> metrics = getMetrics();
        ArgumentCaptor<List<Co2LogMessage>> logMessages = ArgumentCaptor.forClass(List.class);

        processing.process(metrics);

        verify(co2Log, times(1)).log(logMessages.capture());
        List<Co2LogMessage> loggedMessages = logMessages.getValue();
        assertEquals(metrics.size(), loggedMessages.size());
        Set<Timestamp> timestamps = new HashSet<>();
        for (Co2LogMessage logMessage : loggedMessages) {
            Co2PPM sourceCo2Ppm = metrics.get(logMessage.getSensorId());
            assertEquals(sourceCo2Ppm, logMessage.getMetric());
            timestamps.add(logMessage.getMeasureTimestamp());
        }
        assertEquals(1, timestamps.size());//убеждаемся что везде использован один timestamp
        verifyNoMoreInteractions(co2Log);
    }


    @Test
    public void process_exceptionsWhileLog_exceptionThrown() {
        Map<String, Co2PPM> metrics = getMetrics();
        doThrow(RuntimeException.class).when(co2Log).log(any());

        assertThrows(ProcessingException.class, () -> processing.process(metrics));
        verify(co2Log, times(1)).log(any());
        verifyNoMoreInteractions(co2Log);
    }
}
