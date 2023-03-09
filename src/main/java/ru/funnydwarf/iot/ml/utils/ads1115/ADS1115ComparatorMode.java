package ru.funnydwarf.iot.ml.utils.ads1115;

public enum ADS1115ComparatorMode {

    WITH_HYSTERESIS(0b0000000000000000),
    WITHOUT_HYSTERESIS(0b0000000000010000);
    final short bits;

    ADS1115ComparatorMode(int bits) {
        this.bits = (short) bits;
    }
}
