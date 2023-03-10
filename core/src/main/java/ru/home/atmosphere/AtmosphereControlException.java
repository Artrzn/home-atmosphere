package ru.home.atmosphere;

public class AtmosphereControlException extends Exception {

    public AtmosphereControlException(String message) {
        super(message);
    }

    public AtmosphereControlException(String message, Throwable cause) {
        super(message, cause);
    }
}
