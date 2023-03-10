package ru.home.atmosphere.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.home.atmosphere.atmosphere_metrics.HomeAtmosphereMetrics;
import ru.home.atmosphere.log.MetricsLogReader;
import ru.home.atmosphere.processing.ProcessingException;
import ru.home.atmosphere.processing.co2.Co2LogMessage;
import ru.home.atmosphere.processing.co2.Co2Processing;
import ru.home.atmosphere.processing.humidity.HumidityLogMessage;
import ru.home.atmosphere.processing.humidity.HumidityProcessing;
import ru.home.atmosphere.processing.temperature.TemperatureLogMessage;
import ru.home.atmosphere.processing.temperature.TemperatureProcessing;
import ru.home.atmosphere.sensor.HomeAtmosphere;

import java.util.Optional;

@Component
public class WorkCycle {

    private final static Logger LOGGER = LogManager.getLogger(WorkCycle.class);
//    private HomeAtmosphere homeAtmosphere;
//    private TemperatureProcessing temperatureProcessing;
//    private HumidityProcessing humidityProcessing;
//    private Co2Processing co2Processing;
//
//    public WorkCycle(HomeAtmosphere homeAtmosphere, TemperatureProcessing temperatureProcessing,
//                     HumidityProcessing humidityProcessing, Co2Processing co2Processing) {
//        this.homeAtmosphere = homeAtmosphere;
//        this.temperatureProcessing = temperatureProcessing;
//        this.humidityProcessing = humidityProcessing;
//        this.co2Processing = co2Processing;
//    }
//
//    @Scheduled(fixedDelayString = "${workCycle}")
//    public void scheduleFixedDelayTask() {
//        try {
//          HomeAtmosphereMetrics homeAtmosphereMetrics = homeAtmosphere.getMetrics();
//          temperatureProcessing.process(homeAtmosphereMetrics.getTemperature());
//          humidityProcessing.process(homeAtmosphereMetrics.getHumidity());
//          co2Processing.process(homeAtmosphereMetrics.getCo2PPM());
//        } catch (Exception e) {
//            LOGGER.error("Error while execute work cycle.", e);
//        }
//    }

    private HomeAtmosphere homeAtmosphere;
    private TemperatureProcessing temperatureProcessing;
    private HumidityProcessing humidityProcessing;
    private Co2Processing co2Processing;
    private MetricsLogReader<Co2LogMessage> co2LogReader;
    private MetricsLogReader<HumidityLogMessage> humidityLogReader;
    private MetricsLogReader<TemperatureLogMessage> temperatureLogReader;

    public WorkCycle(HomeAtmosphere homeAtmosphere, TemperatureProcessing temperatureProcessing,
                     HumidityProcessing humidityProcessing, Co2Processing co2Processing,
                     MetricsLogReader<Co2LogMessage> co2LogReader, MetricsLogReader<HumidityLogMessage> humidityLogReader,
                     MetricsLogReader<TemperatureLogMessage> temperatureLogReader) {
        this.homeAtmosphere = homeAtmosphere;
        this.temperatureProcessing = temperatureProcessing;
        this.humidityProcessing = humidityProcessing;
        this.co2Processing = co2Processing;
        this.co2LogReader = co2LogReader;
        this.humidityLogReader = humidityLogReader;
        this.temperatureLogReader = temperatureLogReader;
    }

        @Scheduled(fixedDelayString = "120000")
    public void scheduleFixedDelayTask() {
            Optional<HomeAtmosphereMetrics> optional = homeAtmosphere.getMetrics();
            if (optional.isPresent()) {
                HomeAtmosphereMetrics metrics  = optional.get();
                try {
                    temperatureProcessing.process(metrics.getTemperature());
                } catch (ProcessingException e) {
                    e.printStackTrace();
                }
                try {
                    humidityProcessing.process(metrics.getHumidity());
                } catch (ProcessingException e) {
                    e.printStackTrace();
                }
                try {
                    co2Processing.process(metrics.getCo2PPM());
                } catch (ProcessingException e) {
                    e.printStackTrace();
                }
            }
//            Timestamp from =  Timestamp.valueOf(LocalDateTime.now().minusHours(7));
//            Timestamp to =  Timestamp.valueOf(LocalDateTime.now());
//            List<TemperatureLogMessage> temperatureLogMessages = temperatureLogReader.readLogBySensorId("bedroom",from, to);
//            System.out.println("------------------TEMPERATURE------------------");
//            for (int i = 0 ; i <2; i++) {
//                TemperatureLogMessage temperatureLogMessage = temperatureLogMessages.get(i);
//                System.out.print(temperatureLogMessage.getSensorId());
//                System.out.print(" ");
//                System.out.print(temperatureLogMessage.getMeasureTimestamp());
//                System.out.print(" ");
//                System.out.print(temperatureLogMessage.getMetric().getValue());
//                System.out.print(" ");
//                System.out.println(temperatureLogMessage.getHeaterRelayState());
//            }
//            System.out.println("------------------HUMIDITY------------------");
//            List<HumidityLogMessage> humidityLogMessages = humidityLogReader.readLogBySensorId("bedroom",from, to);
//            for (int i = 0 ; i <2; i++) {
//                HumidityLogMessage humidityLogMessage = humidityLogMessages.get(i);
//                System.out.print(humidityLogMessage.getSensorId());
//                System.out.print(" ");
//                System.out.print(humidityLogMessage.getMeasureTimestamp());
//                System.out.print(" ");
//                System.out.println(humidityLogMessage.getMetric().getValue());
//            }
//            System.out.println("------------------CO2------------------");
//            List<Co2LogMessage> co2LogMessages = co2LogReader.readLogBySensorId("bedroom", from, to);
//            for (int i = 0 ; i <2; i++) {
//                Co2LogMessage co2LogMessage = co2LogMessages.get(i);
//                System.out.print(co2LogMessage.getSensorId());
//                System.out.print(" ");
//                System.out.print(co2LogMessage.getMeasureTimestamp());
//                System.out.print(" ");
//                System.out.println(co2LogMessage.getMetric().getValue());
//            }
        }
}

//@Component
//public class WorkCycle {
//
//    private final static Logger LOGGER = LogManager.getLogger(WorkCycle.class);
//    private HomeThermostat homeThermostat;
//
//    public WorkCycle(HomeThermostat homeThermostat) {
//        this.homeThermostat = homeThermostat;
//    }
//
//    @Scheduled(fixedDelayString = "${workCycle}")
//    public void scheduleFixedDelayTask() {
//        try {
//            homeThermostat.workCycle();
//        } catch (Exception e) {
//            LOGGER.error("Error while execute work cycle.", e);
//        }
//    }
//}
