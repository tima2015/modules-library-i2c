package ru.funnydwarf.iot.ml.sensor.register;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.funnydwarf.iot.ml.utils.ads1115.ADS1115Multiplexer;

import java.lang.annotation.Retention;
import java.util.HexFormat;
import java.util.List;

@Getter
public class Register {

    public enum Size {
        BYTE, WORD
    }

    private final short address;
    private final String hexAddress;
    private final Size size;
    private short value = 0;
    private List<String> hexValues;

    public Register(short address, Size size) {
        this.address = address;
        this.size = size;
        hexAddress = HexFormat.of().withUpperCase().withDelimiter(" ").withPrefix("0x").formatHex(new byte[]{
                (byte) (address & 0xff)
        }).split(" ")[0];
    }

    private void updateConfigBytePair() {
        String[] hexBytes = HexFormat.of().withUpperCase().withDelimiter(" ").withPrefix("0x").formatHex(new byte[]{
                (byte) ((value >> 8) & 0xff),
                (byte) (value & 0xff)
        }).split(" ");
        hexValues = size == Size.BYTE ? List.of(hexBytes[1]) : List.of(hexBytes);
    }

    protected final void setValue(short value) {
        this.value = value;
        updateConfigBytePair();
    }

    protected void updateValueByValueParts() {

    }

    protected final void assertValue(short value) {
        if (value != this.value) {
            throw new RuntimeException("Control register value check fail! Row value = [" + Integer.toBinaryString(value) + "], after parse register value = [" + Integer.toBinaryString(this.value) + "]!");
        }
    }

    @AllArgsConstructor
    @Getter
    public static class ValuePart {
        protected final short bits;

        protected static <T extends ValuePart> T getFromBits(short value, short forBits, T[] valueParts) {
            for (T part : valueParts) {
                if (((value & forBits) ^ part.bits) == 0) {
                    return part;
                }
            }
            throw new RuntimeException("Can't find ValuePart for value = [" + Integer.toBinaryString(value) + "] in forBits = [" + Integer.toBinaryString(forBits) + "]!");
        }
    }
}
