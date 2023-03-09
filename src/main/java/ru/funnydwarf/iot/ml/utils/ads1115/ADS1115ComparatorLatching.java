package ru.funnydwarf.iot.ml.utils.ads1115;

public enum ADS1115ComparatorLatching {

    WITHOUT_LOCK(0b0000000000000000),
    WITH_LOCK(0b0000000000000100);
    final short bits;

    ADS1115ComparatorLatching(int bits) {
        this.bits = (short) bits;
    }
}
