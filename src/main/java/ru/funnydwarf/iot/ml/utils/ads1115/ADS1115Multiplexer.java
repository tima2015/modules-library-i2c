package ru.funnydwarf.iot.ml.utils.ads1115;

public enum ADS1115Multiplexer{
    A0_A1(0b0000000000000000),
    A0_A3(0b0001000000000000),
    A1_A3(0b0010000000000000),
    A2_A3(0b0011000000000000),
    A0_GND(0b0100000000000000),
    A1_GND(0b0101000000000000),
    MA2_GND(0b0110000000000000),
    A3_GND(0b0111000000000000);

    final short bits;

    ADS1115Multiplexer(int bits) {
        this.bits = (short) bits;
    }

}
