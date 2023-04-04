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

    @Bean("TSL2561RowBroadbandMeasurementDescription")
    @Lazy
    @Autowired
    public MeasurementDescription getTSL2561RowBroadbandMeasurementDescription(MeasurementDescriptionRepository mdr) {
        return MeasurementDescriptionRepository.findOrCreate(mdr, "λ", "Wavelength", "TSL2561 Row broadband");
    }

    @Bean("TSL2561RowIRMeasurementDescription")
    @Lazy
    @Autowired
    public MeasurementDescription getTSL2561RowIRMeasurementDescription(MeasurementDescriptionRepository mdr) {
        return MeasurementDescriptionRepository.findOrCreate(mdr, "λ", "Wavelength", "TSL2561 Row infrared radiation");
    }

    @Bean("TSL2561IlluminanceMeasurementDescription")
    @Lazy
    @Autowired
    public MeasurementDescription getTSL2561IlluminanceMeasurementDescription(MeasurementDescriptionRepository mdr) {
        return MeasurementDescriptionRepository.findOrCreate(mdr, "lx", "Illuminance", "TSL2561 Illuminance");
    }

    @Bean("TSL2561MeasurementDescription")
    @Lazy
    @Autowired
    public MeasurementDescription[] getTSL2561MeasurementDescription(@Qualifier("TSL2561RowBroadbandMeasurementDescription") MeasurementDescription broadband,
                                                                     @Qualifier("TSL2561RowIRMeasurementDescription") MeasurementDescription ir,
                                                                     @Qualifier("TSL2561IlluminanceMeasurementDescription") MeasurementDescription illuminance) {
        return new MeasurementDescription[]{ broadband, ir, illuminance };
    }

}
