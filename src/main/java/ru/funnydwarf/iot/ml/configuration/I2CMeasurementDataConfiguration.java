package ru.funnydwarf.iot.ml.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.funnydwarf.iot.ml.sensor.MeasurementDescription;
import ru.funnydwarf.iot.ml.sensor.MeasurementDescriptionRepository;

@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class I2CMeasurementDataConfiguration {

    @Bean("AHT10TemperatureMeasurementDescription")
    @Lazy
    @Autowired
    public MeasurementDescription getAHT10TemperatureMeasurementDescription(MeasurementDescriptionRepository mdr) {
        return MeasurementDescriptionRepository.findOrCreate(mdr, "°C", "Temperature", "AHT10 temperature");
    }

    @Bean("AHT10AirHumidityMeasurementDescription")
    @Lazy
    @Autowired
    public MeasurementDescription getAHT10AirHumidityMeasurementDescription(MeasurementDescriptionRepository mdr) {
        return MeasurementDescriptionRepository.findOrCreate(mdr, "%", "Air Humidity", "AHT10 air humidity");
    }

    @Bean("AHT10MeasurementDescriptions")
    @Lazy
    @Autowired
    public MeasurementDescription[] getAHT10MeasurementDescriptions(
            @Qualifier("AHT10TemperatureMeasurementDescription") MeasurementDescription temperature,
            @Qualifier("AHT10AirHumidityMeasurementDescription") MeasurementDescription airHumidity) {
        return new MeasurementDescription[] {temperature, airHumidity};
    }

    @Bean("TSL2561MeasurementDescription")
    @Lazy
    @Autowired
    public MeasurementDescription getTSL2561MeasurementDescription(MeasurementDescriptionRepository mdr) {
        return MeasurementDescriptionRepository.findOrCreate(mdr, "lx", "Illuminance", "TSL2561 Illuminance");
    }

}
