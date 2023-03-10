package ru.home.atmosphere.processing.temperature;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeaterModeTests {

    private float expectedTemperature = 25.5f;
    private float hysteresis = 0.3f;
    private HeaterMode heaterMode = new HeaterMode(expectedTemperature, hysteresis);

    @ParameterizedTest
    @MethodSource("getArgumentsForCoolDown")
    public void isNeedWarmUp_actualModeIsCoolDown_expectedModeReturned(float testedValue, boolean expected) {
        boolean result = heaterMode.isNeedWarmUp(testedValue);

        assertEquals(expected, result);
    }

    private static Stream<Arguments> getArgumentsForCoolDown() {
        return Stream.of(
                Arguments.of(22f, true),
                Arguments.of(25.3f, false),
                Arguments.of(25.7f, false),
                Arguments.of(26f, false)
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForWarmUp")
    public void isNeedWarmUp_actualModeIsWarmUp_expectedModeReturned(float testedValue, boolean expected) {
        switchModeToWarmUp();

        boolean result = heaterMode.isNeedWarmUp(testedValue);

        assertEquals(expected, result);
    }

    private static Stream<Arguments> getArgumentsForWarmUp() {
        return Stream.of(
                Arguments.of(22f, true),
                Arguments.of(25.3f, true),
                Arguments.of(25.7f, true),
                Arguments.of(26f, false)
        );
    }

    private void switchModeToWarmUp() {
        heaterMode.isNeedWarmUp(1f);
    }
}
