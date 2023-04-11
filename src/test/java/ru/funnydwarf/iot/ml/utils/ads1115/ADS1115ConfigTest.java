package ru.funnydwarf.iot.ml.utils.ads1115;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ADS1115ConfigTest {


    void constructor0() {
        ADS1115Config first = new ADS1115Config();
        ADS1115Config second = new ADS1115Config(ADS1115State.READ_DONE_WRITE_START,
                ADS1115Multiplexer.A0_A1,
                ADS1115ProgrammableGainAmplifier.FS2_048V,
                ADS1115Mode.SINGLE,
                ADS1115Rate.Hz128,
                ADS1115ComparatorMode.WITH_HYSTERESIS,
                ADS1115ComparatorPolarity.LOW,
                ADS1115ComparatorLatching.WITHOUT_LOCK,
                ADS1115ComparatorQueue.DISABLE);
        assertEquals(first.getConfigBytes(), second.getConfigBytes());
        assertEquals(first.getMux(), second.getMux());
        assertEquals(first.getPga(), second.getPga());
        assertEquals(first.getMode(), second.getMode());
        assertEquals(first.getRate(), second.getRate());
        assertEquals(first.getComp_mode(), second.getComp_mode());
        assertEquals(first.getComp_lat(), second.getComp_lat());
        assertEquals(first.getComp_que(), second.getComp_que());
        assertEquals(first.getComp_pol(), second.getComp_pol());
        assertEquals(first.getConfigBytePair(), second.getConfigBytePair());

    }

    void constructor1() {
        ADS1115Config first = new ADS1115Config();
        ADS1115Config second = new ADS1115Config((short) 0b1000010110000011);
        assertEquals(first.getConfigBytes(), second.getConfigBytes());
        assertEquals(first.getMux(), second.getMux());
        assertEquals(first.getPga(), second.getPga());
        assertEquals(first.getMode(), second.getMode());
        assertEquals(first.getRate(), second.getRate());
        assertEquals(first.getComp_mode(), second.getComp_mode());
        assertEquals(first.getComp_lat(), second.getComp_lat());
        assertEquals(first.getComp_que(), second.getComp_que());
        assertEquals(first.getComp_pol(), second.getComp_pol());
        assertEquals(first.getConfigBytePair(), second.getConfigBytePair());
    }

    void setMux() {
        ADS1115Config config = new ADS1115Config();
        config.setMux(ADS1115Multiplexer.A0_GND);
        assertEquals(config.getConfigBytes(), 0b1100010110000011);
    }

    void testConfigBytePair() {
        ADS1115Config config = new ADS1115Config();
        config.setMux(ADS1115Multiplexer.A0_GND);
        assertEquals(List.of("0xC5", "0x83"), config.getConfigBytePair());
    }
}