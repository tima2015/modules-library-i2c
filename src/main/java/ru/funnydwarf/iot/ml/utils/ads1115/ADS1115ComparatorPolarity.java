package ru.funnydwarf.iot.ml.utils.ads1115;

public enum ADS1115ComparatorPolarity {

    LOW(0b0000000000000000),
    HIGH(0b0000000000001000);
    final short bits;

    ADS1115ComparatorPolarity(int bits) {
        this.bits = (short) bits;
    }
}
