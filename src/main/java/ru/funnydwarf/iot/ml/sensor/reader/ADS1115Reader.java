package ru.funnydwarf.iot.ml.sensor.reader;

import ru.funnydwarf.iot.ml.sensor.MeasurementData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ADS1115Reader implements Reader {

    public record MUXAndMeasurementTemplatePair(int MUX, MeasurementData template) {
    }

    private final List<MUXAndMeasurementTemplatePair> configData;

    public ADS1115Reader(List<MUXAndMeasurementTemplatePair> configData) {
        this.configData = configData;
    }

    @Override
    public MeasurementData[] read(Object address) {
        MeasurementData[] measurementData = new MeasurementData[configData.size()];
        for (int i = 0; i < measurementData.length; i++) {
            MUXAndMeasurementTemplatePair pair = configData.get(i);
            // TODO: 01.11.2022
        }
        return new MeasurementData[0];
    }

    @Override
    public MeasurementData[] getTemplateRead() {
        MeasurementData[] measurementData = new MeasurementData[configData.size()];
        for (int i = 0; i < measurementData.length; i++) {
            MUXAndMeasurementTemplatePair pair = configData.get(i);
            measurementData[i] = new MeasurementData(Double.MIN_VALUE, pair.template.unitName(),
                    pair.template.measurementName(), new Date(1));
        }
        return measurementData;
    }

    public static class ADS1115ReaderBuilder {

        int MUX_A0_A1 = 0b000;
        int MUX_A0_A3 = 0b001;
        int MUX_A1_A3 = 0b010;
        int MUX_A2_A3 = 0b011;
        int MUX_A0_GND = 0b100;
        int MUX_A1_GND = 0b101;
        int MUX_A2_GND = 0b110;
        int MUX_A3_GND = 0b111;

        private List<MUXAndMeasurementTemplatePair> configData = new ArrayList<>();

        public ADS1115ReaderBuilder forMUX(int MUX, String unitName, String measurementNames) {
            configData.add(new MUXAndMeasurementTemplatePair(MUX,
                    new MeasurementData(Double.MIN_VALUE, unitName, measurementNames, new Date(1))));
            return this;
        }

        public ADS1115Reader build() {
            return new ADS1115Reader(configData);
        }
    }

}
