package ru.home.atmosphere.log;

import java.sql.Timestamp;
import java.util.List;

public interface MetricsLogReader<T extends LogMessage> {

    List<T> readLog(Timestamp from, Timestamp to);

    List<T> readLogBySensorId(String sensorId, Timestamp from, Timestamp to);
}
