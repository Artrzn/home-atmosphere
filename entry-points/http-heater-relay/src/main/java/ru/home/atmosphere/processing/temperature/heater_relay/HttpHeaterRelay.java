package ru.home.atmosphere.processing.temperature.heater_relay;

import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

public class HttpHeaterRelay implements HeaterRelay {

    private final String url;
    private final RelayHttpClient client;

    public HttpHeaterRelay(String url, RelayHttpClient client) {
        this.url = url;
        this.client = client;
    }

    @Override
    public void switchOn() throws RelayException {
        HttpPost switchOnRequest = new HttpPost(url);
        List<NameValuePair> requestParameters = List.of(new BasicNameValuePair("relay_state", "on"));
        switchOnRequest.setEntity(new UrlEncodedFormEntity(requestParameters, Consts.UTF_8));
        int code = client.execute(switchOnRequest);
        if (HttpStatus.SC_OK != code) {
            throw new RelayException(String.format("Error while switch on relay. Http code: %s", code));
        }
    }

    @Override
    public void switchOff() throws RelayException {
        HttpPost switchOffRequest = new HttpPost(url);
        List<NameValuePair> requestParameters = List.of(new BasicNameValuePair("relay_state", "off"));
        switchOffRequest.setEntity(new UrlEncodedFormEntity(requestParameters, Consts.UTF_8));
        int code = client.execute(switchOffRequest);
        if (HttpStatus.SC_OK != code) {
            throw new RelayException(String.format("Error while switch off relay. Http code: %s", code));
        }
    }
}
