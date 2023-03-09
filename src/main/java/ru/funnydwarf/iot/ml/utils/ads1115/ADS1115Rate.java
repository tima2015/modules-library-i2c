package ru.funnydwarf.iot.ml.utils.ads1115;

public enum ADS1115Rate {

    Hz8(0b0000000000000000) ,
    Hz16(0b0000000000100000),
    Hz32(0b0000000001000000),
    Hz64(0b0000000001100000),
    Hz128(0b0000000010000000),
    Hz250(0b0000000010100000),
    Hz475(0b0000000011000000),
    Hz860(0b0000000011100000);
    final short bits;

    ADS1115Rate(int bits) {
        this.bits = (short) bits;
    }
}
