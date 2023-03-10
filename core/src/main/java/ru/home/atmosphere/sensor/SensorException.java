package ru.home.atmosphere.sensor;

public class SensorException extends Exception{

    public SensorException(String message) {
        super(message);
    }

    public SensorException(String message, Throwable cause) {
        super(message, cause);
    }
}
