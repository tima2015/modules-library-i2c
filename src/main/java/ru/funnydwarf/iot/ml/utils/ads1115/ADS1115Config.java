package ru.funnydwarf.iot.ml.utils.ads1115;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

@Getter
public class ADS1115Config {

    private short configBytes = (short) 0b1000010110000011;
    private List<String> configBytePair = List.of("0x85", "0x83");

    private ADS1115State state = ADS1115State.READ_DONE_WRITE_START;
    private ADS1115Multiplexer mux = ADS1115Multiplexer.A0_A1;
    private ADS1115ProgrammableGainAmplifier pga = ADS1115ProgrammableGainAmplifier.FS2_048V;
    private ADS1115Mode mode = ADS1115Mode.SINGLE;
    private ADS1115Rate rate = ADS1115Rate.Hz128;
    private ADS1115ComparatorMode comp_mode = ADS1115ComparatorMode.WITH_HYSTERESIS;
    private ADS1115ComparatorPolarity comp_pol = ADS1115ComparatorPolarity.LOW;
    private ADS1115ComparatorLatching comp_lat = ADS1115ComparatorLatching.WITHOUT_LOCK;
    private ADS1115ComparatorQueue comp_que = ADS1115ComparatorQueue.DISABLE;

    public ADS1115Config(ADS1115State state, ADS1115Multiplexer mux, ADS1115ProgrammableGainAmplifier pga, ADS1115Mode mode, ADS1115Rate rate, ADS1115ComparatorMode comp_mode, ADS1115ComparatorPolarity comp_pol, ADS1115ComparatorLatching comp_lat, ADS1115ComparatorQueue comp_que) {
        this.state = state;
        this.mux = mux;
        this.pga = pga;
        this.mode = mode;
        this.rate = rate;
        this.comp_mode = comp_mode;
        this.comp_pol = comp_pol;
        this.comp_lat = comp_lat;
        this.comp_que = comp_que;
        updateConfigBytes();
    }

    public ADS1115Config() {}

    public ADS1115Config(short configBytes) {
        this.configBytes = configBytes;
        updateConfigBytePair();

        this.state = (configBytes & ADS1115State.READ_DONE_WRITE_START.bits) == ADS1115State.READ_DONE_WRITE_START.bits ? ADS1115State.READ_DONE_WRITE_START : ADS1115State.READ_WORKING_WRITE_NOTHING;

        for (ADS1115Multiplexer value : ADS1115Multiplexer.values()) {
            if (((configBytes & 0b0111000000000000) ^ value.bits) == 0) {
                this.mux = value;
            }
        }

        for (ADS1115ProgrammableGainAmplifier value : ADS1115ProgrammableGainAmplifier.values()) {
            if (((configBytes & 0b0000111000000000) ^ value.bits) == 0) {
                this.pga = value;
            }
        }

        this.mode = (configBytes & ADS1115Mode.SINGLE.bits) == ADS1115Mode.SINGLE.bits ? ADS1115Mode.SINGLE : ADS1115Mode.CONTINUOUS;

        for (ADS1115Rate value : ADS1115Rate.values()) {
            if (((configBytes & 0b0000000011100000) ^ value.bits) == 0) {
                this.rate = value;
            }
        }

        this.comp_mode = (configBytes & ADS1115ComparatorMode.WITHOUT_HYSTERESIS.bits) == ADS1115ComparatorMode.WITHOUT_HYSTERESIS.bits ? ADS1115ComparatorMode.WITHOUT_HYSTERESIS : ADS1115ComparatorMode.WITH_HYSTERESIS;
        this.comp_pol =  (configBytes & ADS1115ComparatorPolarity.HIGH.bits) == ADS1115ComparatorPolarity.HIGH.bits ? ADS1115ComparatorPolarity.HIGH : ADS1115ComparatorPolarity.LOW;
        this.comp_lat = (configBytes & ADS1115ComparatorLatching.WITHOUT_LOCK.bits) == ADS1115ComparatorLatching.WITHOUT_LOCK.bits ? ADS1115ComparatorLatching.WITH_LOCK : ADS1115ComparatorLatching.WITHOUT_LOCK;

        for (ADS1115ComparatorQueue value : ADS1115ComparatorQueue.values()) {
            if (((configBytes & 0b0000000000000011) ^ value.bits) == 0) {
                this.comp_que = value;
            }
        }
    }

    private void updateConfigBytes(){
        configBytes = 0;
        configBytes |= state.bits;
        configBytes |= mux.bits;
        configBytes |= pga.bits;
        configBytes |= mode.bits;
        configBytes |= rate.bits;
        configBytes |= comp_mode.bits;
        configBytes |= comp_pol.bits;
        configBytes |= comp_lat.bits;
        configBytes |= comp_que.bits;
        updateConfigBytePair();
    }

    private void updateConfigBytePair() {
        String[] hexBytes = HexFormat.of().withUpperCase().withDelimiter(" ").withPrefix("0x").formatHex(new byte[]{
                (byte) ((configBytes >> 8) & 0xff),
                (byte) (configBytes & 0xff)
        }).split(" ");
        configBytePair = List.of(hexBytes);
    }

    public void setState(ADS1115State state) {
        this.state = state;
        updateConfigBytes();
    }

    public void setMux(ADS1115Multiplexer mux) {
        this.mux = mux;
        updateConfigBytes();
    }

    public void setPga(ADS1115ProgrammableGainAmplifier pga) {
        this.pga = pga;
        updateConfigBytes();
    }

    public void setMode(ADS1115Mode mode) {
        this.mode = mode;
        updateConfigBytes();
    }

    public void setRate(ADS1115Rate rate) {
        this.rate = rate;
        updateConfigBytes();
    }

    public void setComp_mode(ADS1115ComparatorMode comp_mode) {
        this.comp_mode = comp_mode;
        updateConfigBytes();
    }

    public void setComp_pol(ADS1115ComparatorPolarity comp_pol) {
        this.comp_pol = comp_pol;
        updateConfigBytes();
    }

    public void setComp_lat(ADS1115ComparatorLatching comp_lat) {
        this.comp_lat = comp_lat;
        updateConfigBytes();
    }

    public void setComp_que(ADS1115ComparatorQueue comp_que) {
        this.comp_que = comp_que;
        updateConfigBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ADS1115Config config = (ADS1115Config) o;
        return configBytes == config.configBytes && mux == config.mux && pga == config.pga && mode == config.mode && rate == config.rate && comp_mode == config.comp_mode && comp_pol == config.comp_pol && comp_lat == config.comp_lat && comp_que == config.comp_que;
    }

    @Override
    public int hashCode() {
        return Objects.hash(configBytes, mux, pga, mode, rate, comp_mode, comp_pol, comp_lat, comp_que);
    }
}
