package ru.funnydwarf.iot.ml.utils.ads1115;

public enum ADS1115ProgrammableGainAmplifier {
    FS6_144V(0b0000000000000000, 0.1875),
    FS4_096V(0b0000001000000000, 0.125),
    FS2_048V(0b0000010000000000, 0.0625),
    FS1_024V(0b0000011000000000, 0.03125),
    FS0_512V(0b0000100000000000, 0.015625),
    FS0_256V(0b0000101000000000, 0.0078125);

    final short bits;
    public final double significanceOfOneBit;

    ADS1115ProgrammableGainAmplifier(int bits, double significanceOfOneBit) {
        this.bits = (short) bits;
        this.significanceOfOneBit = significanceOfOneBit;
    }
}
