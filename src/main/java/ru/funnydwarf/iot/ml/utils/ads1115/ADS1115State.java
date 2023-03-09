package ru.funnydwarf.iot.ml.utils.ads1115;

public enum ADS1115State {
    READ_WORKING_WRITE_NOTHING(0b0000000000000000), READ_DONE_WRITE_START(0b1000000000000000);
    final short bits;

    ADS1115State(int bits) {
        this.bits = (short) bits;
    }
}
