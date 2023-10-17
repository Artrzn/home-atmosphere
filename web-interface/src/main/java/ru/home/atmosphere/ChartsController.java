package ru.home.atmosphere;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.home.atmosphere.log.MetricsLogReader;
import ru.home.atmosphere.processing.co2.Co2LogMessage;
import ru.home.atmosphere.processing.humidity.HumidityLogMessage;
import ru.home.atmosphere.processing.temperature.TemperatureLogMessage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RestController
public class ChartsController {

    private final static Logger LOGGER = LogManager.getLogger(ChartsController.class);
    private final MetricsLogReader<Co2LogMessage> co2LogReader;
    private final MetricsLogReader<HumidityLogMessage> humidityLogReader;
    private final MetricsLogReader<TemperatureLogMessage> temperatureLogReader;

    public ChartsController(MetricsLogReader<Co2LogMessage> co2LogReader, MetricsLogReader<HumidityLogMessage> humidityLogReader,
                            MetricsLogReader<TemperatureLogMessage> temperatureLogReader) {
        this.co2LogReader = co2LogReader;
        this.humidityLogReader = humidityLogReader;
        this.temperatureLogReader = temperatureLogReader;
    }

    @GetMapping("/co2Chart")
    public List<Co2LogMessage> getCo2Logs(@RequestParam(name = "dateFrom") long dateFrom,
                                          @RequestParam(name = "dateTo") long dateTo) {
        LOGGER.info("Receive getCo2Logs request with parameters: dateFrom={}, dateTo:{}.", dateFrom, dateTo);
        Timestamp from = Timestamp.from(Instant.ofEpochMilli(dateFrom));
        Timestamp to = Timestamp.from(Instant.ofEpochMilli(dateTo));
        return co2LogReader.readLog(from, to);
    }

    @GetMapping("/humidityChart")
    public List<HumidityLogMessage> getHumidityLogs(@RequestParam(name = "dateFrom") long dateFrom,
                                                    @RequestParam(name = "dateTo") long dateTo) {
        LOGGER.info("Receive getHumidityLogs request with parameters: dateFrom={}, dateTo:{}.", dateFrom, dateTo);
        Timestamp from = Timestamp.from(Instant.ofEpochMilli(dateFrom));
        Timestamp to = Timestamp.from(Instant.ofEpochMilli(dateTo));
        return humidityLogReader.readLog(from, to);
    }

    @GetMapping("/temperatureChart")
    public List<TemperatureLogMessage> getTemperatureLogs(@RequestParam(name = "dateFrom") long dateFrom,
                                                          @RequestParam(name = "dateTo") long dateTo) {
        LOGGER.info("Receive getTemperatureLogs request with parameters: dateFrom={}, dateTo:{}.", dateFrom, dateTo);
        Timestamp from = Timestamp.from(Instant.ofEpochMilli(dateFrom));
        Timestamp to = Timestamp.from(Instant.ofEpochMilli(dateTo));
        return temperatureLogReader.readLog(from, to);
    }
}
