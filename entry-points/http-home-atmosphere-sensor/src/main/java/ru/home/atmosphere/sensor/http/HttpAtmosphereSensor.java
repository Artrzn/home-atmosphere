package ru.home.atmosphere.sensor.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.home.atmosphere.atmosphere_metrics.RoomAtmosphereMetrics;
import ru.home.atmosphere.sensor.AtmosphereSensor;
import ru.home.atmosphere.sensor.SensorException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpAtmosphereSensor implements AtmosphereSensor {

    private final URI uri;
    private final HttpClient httpClient;

    public HttpAtmosphereSensor(URI uri, HttpClient httpClient) {
        this.uri = uri;
        this.httpClient = httpClient;
    }

    @Override
    public RoomAtmosphereMetrics getAtmosphereMetrics() throws SensorException {
        HttpRequest metricsRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(metricsRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new SensorException(String.format("Error while get atmosphere metrics from uri: %s", uri), e);
        }
        Gson gson = new Gson();
        String jsonResponse  = response.body().replaceAll("nan", "NaN");//fix for response format
        RoomAtmosphereMetrics metrics;
        try {
           metrics = gson.fromJson(jsonResponse, RoomAtmosphereMetrics.class);
        } catch (JsonSyntaxException e) {
            throw new SensorException(String.format("Error while parse response: %s", jsonResponse), e);
        }
        return metrics;
    }
}
