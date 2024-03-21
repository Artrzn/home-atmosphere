package ru.home.atmosphere.processing.temperature.heater_relay;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RelayHttpClient {

    private final static Logger LOGGER = LogManager.getLogger(RelayHttpClient.class);

    public int execute(HttpPost request) throws RelayException {
        LOGGER.info("Try execute POST request: {}", request);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity, "UTF-8");
            LOGGER.info("Success execution. Response: {}. Response body: {}.", response, responseBody);
            return response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            throw new RelayException(String.format("Error while execute request: %s.", request), e);
        }
    }
}
