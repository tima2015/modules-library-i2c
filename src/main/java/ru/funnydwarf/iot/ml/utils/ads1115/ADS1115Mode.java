package ru.funnydwarf.iot.ml.utils.ads1115;

public enum ADS1115Mode {

    SINGLE(0b0000000100000000),
    CONTINUOUS(0b0000000000000000);
    final short bits;

    ADS1115Mode(int bits) {
        this.bits = (short) bits;
    }
}
