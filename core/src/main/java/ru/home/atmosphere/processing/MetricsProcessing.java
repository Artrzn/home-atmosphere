package ru.home.atmosphere.processing;


public interface MetricsProcessing<T> {
    void process(T metrics) throws ProcessingException;
}
