package ru.home.atmosphere.sensor.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.home.atmosphere.atmosphere_metrics.RoomAtmosphereMetrics;
import ru.home.atmosphere.sensor.SensorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HttpAtmosphereSensorTests {

    private URI uri;
    private HttpClient httpClient;
    private HttpAtmosphereSensor sensor;

    @BeforeEach
    public void init() throws URISyntaxException {
        uri  = new URI("http://localhost");
        httpClient = mock(HttpClient.class);
        sensor = new HttpAtmosphereSensor(uri, httpClient);
    }

    @Test
    public void getAtmosphereMetrics_httpError_exceptionThrown() throws IOException, InterruptedException {
        when(httpClient.send(any(), any())).thenThrow(IOException.class);

        assertThrows(SensorException.class, sensor::getAtmosphereMetrics);
        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    public void getAtmosphereMetrics_wrongResponse_exceptionThrown() throws IOException, InterruptedException {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn("wrong result");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()))).thenReturn(response);

        assertThrows(SensorException.class, sensor::getAtmosphereMetrics);
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()));
    }

    @Test
    public void getAtmosphereMetrics_normalState_metricsReturned() throws IOException, InterruptedException, SensorException {
         float expectedDsTemperature = 20.1f;
         float expectedDhtTemperature = Float.NaN;
         float expectedHumidity = 30.5f;
         int expectedCo2Ppm = 555;
        String jsonResponse = "{\"temperatureFromDS\":\"" + expectedDsTemperature + "\", \"temperatureFrimDHT\":\"" +
                expectedDhtTemperature + "\", \"humidityFromDHT\":\"" + expectedHumidity + "\", \"co2ppm\":\"" +
                expectedCo2Ppm + "\"}";
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()))).thenReturn(response);

        RoomAtmosphereMetrics result  = sensor.getAtmosphereMetrics();

        assertEquals(expectedDsTemperature, result.getTemperatureFromDS());
        assertEquals(expectedDhtTemperature, result.getTemperatureFrimDHT());
        assertEquals(expectedHumidity, result.getHumidityFromDHT());
        assertEquals(expectedCo2Ppm, result.getCo2ppm());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()));
    }
}
