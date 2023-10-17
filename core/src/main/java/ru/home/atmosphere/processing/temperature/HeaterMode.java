package ru.home.atmosphere.processing.temperature;

public class HeaterMode {

    private final float expectedTemperature;
    private final float hysteresis;
    private boolean isNeedToWarmUp;

    public HeaterMode(float expectedTemperature, float hysteresis) {
        this.expectedTemperature = expectedTemperature;
        this.hysteresis = hysteresis;
    }

    public boolean isNeedWarmUp(float actualTemperature) {
        if (isNeedToChangeMode(actualTemperature)) {
            isNeedToWarmUp = !isNeedToWarmUp;
        }
        return isNeedToWarmUp;
    }

    private boolean isNeedToChangeMode(float temperature) {
        boolean result;
        if (isNeedToWarmUp) {
            result = temperature < expectedTemperature + hysteresis;
        } else {
            result = temperature < expectedTemperature - hysteresis;
        }
        return (isNeedToWarmUp && !result) || (!isNeedToWarmUp && result);
    }
}
