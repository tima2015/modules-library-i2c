package ru.funnydwarf.iot.ml.utils.ads1115;

public enum ADS1115ComparatorQueue {

    ONE(0b0000000000000000),
    TWO(0b0000000000000001),
    FOUR(0b0000000000000010),
    DISABLE(0b0000000000000011);
    final short bits;

    ADS1115ComparatorQueue(int bits) {
        this.bits = (short) bits;
    }

}
