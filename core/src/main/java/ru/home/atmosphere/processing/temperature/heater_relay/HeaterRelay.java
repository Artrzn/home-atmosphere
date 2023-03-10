package ru.home.atmosphere.processing.temperature.heater_relay;

public interface HeaterRelay {
    void switchOn() throws RelayException;
    void switchOff() throws RelayException;
}
