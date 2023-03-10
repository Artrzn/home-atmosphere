package ru.home.atmosphere.processing.temperature.heater_relay;

import ru.home.atmosphere.AtmosphereControlException;

public class RelayException extends AtmosphereControlException {
    public RelayException(String message) {
        super(message);
    }

    public RelayException(String message, Throwable cause) {
        super(message, cause);
    }
}
