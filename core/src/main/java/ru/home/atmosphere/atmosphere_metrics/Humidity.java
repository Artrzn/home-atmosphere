package ru.home.atmosphere.atmosphere_metrics;

import java.sql.Timestamp;

public class Humidity {

    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Humidity{" +
                "value=" + value +
                '}';
    }
}
