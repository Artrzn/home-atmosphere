package ru.home.atmosphere.atmosphere_metrics;

public class Co2PPM {

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Co2PPM{" +
                "value=" + value +
                '}';
    }
}
