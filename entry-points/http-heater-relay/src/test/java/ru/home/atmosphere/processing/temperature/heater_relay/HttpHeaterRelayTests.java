package ru.home.atmosphere.processing.temperature.heater_relay;

import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class HttpHeaterRelayTests {

    private HeaterRelay relay;
    private RelayHttpClient client;

    @BeforeEach
    public void init() {
        client = mock(RelayHttpClient.class);
        String url = "url";
        relay = new HttpHeaterRelay(url, client);
    }

    @Test
    public void switchOn_httpError_exceptionThrown() throws RelayException {
        when(client.execute(any(HttpPost.class))).thenThrow(RelayException.class);

        assertThrows(RelayException.class, relay::switchOn);
        verify(client, times(1)).execute(any(HttpPost.class));
    }

    @Test
    public void switchOn_wrongHttpCode_exceptionThrown() throws RelayException {
        when(client.execute(any(HttpPost.class))).thenReturn(500);

        assertThrows(RelayException.class, relay::switchOn);
        verify(client, times(1)).execute(any(HttpPost.class));
    }

    @Test
    public void switchOn_normalState_success() throws RelayException {
        when(client.execute(any(HttpPost.class))).thenReturn(200);

        relay.switchOn();

        verify(client, times(1)).execute(any(HttpPost.class));
    }

    @Test
    public void switchOff_httpError_exceptionThrown() throws RelayException {
        when(client.execute(any(HttpPost.class))).thenThrow(RelayException.class);

        assertThrows(RelayException.class, relay::switchOff);
        verify(client, times(1)).execute(any(HttpPost.class));
    }

    @Test
    public void switchOff_wrongHttpCode_exceptionThrown() throws RelayException {
        when(client.execute(any(HttpPost.class))).thenReturn(500);

        assertThrows(RelayException.class, relay::switchOff);
        verify(client, times(1)).execute(any(HttpPost.class));
    }

    @Test
    public void switchOff_normalState_success() throws RelayException {
        when(client.execute(any(HttpPost.class))).thenReturn(200);

        relay.switchOff();

        verify(client, times(1)).execute(any(HttpPost.class));
    }
}
