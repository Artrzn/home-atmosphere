package ru.home.atmosphere.log;

import java.util.List;

public interface MetricsLogWriter<T extends LogMessage> {
    void log(List<T> messages);
}
