package ru.funnydwarf.iot.ml.sensor.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

import java.io.IOException;

@Getter
@NoArgsConstructor
public class TSL2561Config {

    private final ControlRegister controlRegister = new ControlRegister();
    private final TimingRegister timingRegister = new TimingRegister();
    private final IDRegister idRegister = new IDRegister();
    private final DataRegister data0LowRegister = new DataRegister((short) 0x0C);
    private final DataRegister data0HighRegister = new DataRegister((short) 0x0D);
    private final DataRegister data1LowRegister = new DataRegister((short) 0x0E);
    private final DataRegister data1HighRegister = new DataRegister((short) 0x0F);


    @Getter
    public static final class TimingRegister extends Register implements Writeable, Readable {

        private Gain gain = Gain.LOW;
        private Manual manual = Manual.START;
        private Integrate integrate = Integrate.CYCLE_402_MS;

        private TimingRegister() {
            super((short) 0x1, Size.BYTE);
            setValue((short) 0b1111111100011011);
        }

        @Override
        protected void updateValueByValueParts() {
            super.updateValueByValueParts();
            short newValue = 0;
            newValue |= gain.getBits();
            newValue |= manual.getBits();
            newValue |= integrate.getBits();
            setValue(newValue);
        }

        public void setGain(Gain gain) {
            this.gain = gain;
            updateValueByValueParts();
        }

        public void setManual(Manual manual) {
            this.manual = manual;
            updateValueByValueParts();
        }

        public void setIntegrate(Integrate integrate) {
            this.integrate = integrate;
            updateValueByValueParts();
        }

        @Override
        public void readRegisterValueFromDevice(I2CAddress address) throws IOException, InterruptedException {
            short value = Short.parseShort(I2CDriverWorker.readByte(address, getHexAddress()), 16);
            setGain(ValuePart.getFromBits(value, Gain.inBits, Gain.gains));
            setManual(ValuePart.getFromBits(value, Manual.inBits, Manual.manuals));
            setIntegrate(ValuePart.getFromBits(value, Integrate.inBits, Integrate.integrates));
            assertValue(value);
        }

        @Override
        public void writeCurrentRegisterValueToDevice(I2CAddress address) throws IOException, InterruptedException {
            I2CDriverWorker.writeByte(address, getHexAddress(), getHexValues().get(0));
        }

        public static final class Gain extends ValuePart {

            private Gain(int bits) {
                super((short) bits);
            }

            public static final Gain LOW = new Gain(0b0000000000000000);
            public static final Gain HIGH = new Gain(0b0000000000010000);

            private static final short inBits = 0b0000000000010000;
            private static final Gain[] gains = new Gain[] { LOW, HIGH };
        }

        public static final class Manual extends ValuePart {

            private Manual(int bits) {
                super((short) bits);
            }

            public static final Manual START = new Manual(0b0000000000001000);
            public static final Manual STOP = new Manual(0b0000000000000000);

            private static final short inBits = 0b0000000000001000;
            private static final Manual[] manuals = new Manual[] {START, STOP};
        }

        public static final class Integrate extends ValuePart {

            private Integrate(int bits) {
                super((short) bits);
            }

            public static final Integrate CYCLE_13_7_MS = new Integrate(0b0000000000000000);
            public static final Integrate CYCLE_101_MS = new Integrate(0b0000000000000001);
            public static final Integrate CYCLE_402_MS = new Integrate(0b0000000000000010);
            public static final Integrate NA = new Integrate(0b0000000000000011);

            private static final short inBits = 0b0000000000000011;
            private static final Integrate[] integrates = new Integrate[] { CYCLE_13_7_MS, CYCLE_101_MS, CYCLE_402_MS, NA};
        }
    }

    @Getter
    public static final class ControlRegister extends Register implements Writeable, Readable {

        private Power power;
        public ControlRegister() {
            super((short) 0x0, Size.BYTE);
            setValue((short) 0b1111111100000000);
        }

        @Override
        protected void updateValueByValueParts() {
            super.updateValueByValueParts();
            short newValue = 0;
            newValue |= power.getBits();
            setValue(newValue);
        }

        public void setPower(Power power) {
            this.power = power;
            updateValueByValueParts();
        }

        @Override
        public void readRegisterValueFromDevice(I2CAddress address) throws IOException, InterruptedException {
            short value = Short.parseShort(I2CDriverWorker.readByte(address, getHexAddress()), 16);
            setPower(ValuePart.getFromBits(value, Power.inBits, Power.powers));
            assertValue(value);
        }

        @Override
        public void writeCurrentRegisterValueToDevice(I2CAddress address) throws IOException, InterruptedException {
            // TODO: 11.03.2023
        }

        public static final class Power extends ValuePart {
            private Power(int bits) {
                super((short) bits);
            }

            public static final Power POWER_DOWN = new Power(0b0000000000000000);
            public static final Power POWER_UP = new Power(0b0000000000000011);

            private static final short inBits = 0b0000000000000011;
            private static final Power[] powers = new Power[] { POWER_DOWN, POWER_UP };
        }
    }

    // TODO: 09.03.2023 Interrupt Threshold Register & Interrupt Control Register

    @Getter
    public static final class IDRegister extends Register implements Readable {

        private PartNumber partNumber = PartNumber.UNKNOWN;
        private short revisionNumber = 0b0000000000000000;
        private IDRegister() {
            super((short) 0x0A, Size.BYTE);
        }

        @Override
        protected void updateValueByValueParts() {
            super.updateValueByValueParts();
            short newValue = 0;
            newValue |= partNumber.bits;
            newValue |= revisionNumber;
            setValue(newValue);
        }

        public void setPartNumber(PartNumber partNumber) {
            this.partNumber = partNumber;
            updateValueByValueParts();
        }

        public void setRevisionNumber(short revisionNumber) {
            this.revisionNumber = revisionNumber;
            updateValueByValueParts();
        }

        @Override
        public void readRegisterValueFromDevice(I2CAddress address) throws IOException, InterruptedException {
            short value = Short.parseShort(I2CDriverWorker.readByte(address, getHexAddress()),16);
            setPartNumber(ValuePart.getFromBits(value, PartNumber.inBits, PartNumber.partNumbers));
            setRevisionNumber((short) (value & 0b0000000000001111));
            assertValue(value);
        }

        public static final class PartNumber extends ValuePart {
            private PartNumber(int bits) {
                super((short) bits);
            }

            public static final PartNumber UNKNOWN = new PartNumber(0b0000000011110000);
            public static final PartNumber TSL2560 = new PartNumber(0b0000000000000000);
            public static final PartNumber TSL2561 = new PartNumber(0b0000000000010000);

            private static final short inBits = 0b0000000011110000;
            private static final PartNumber[] partNumbers = new PartNumber[] {UNKNOWN, TSL2560, TSL2561};
        }
    }

    @Getter
    public static final class DataRegister extends Register implements Readable {

        private short data = 0b0000000000000000;

        private DataRegister(short address) {
            super(address, Size.BYTE);
        }

        @Override
        protected void updateValueByValueParts() {
            super.updateValueByValueParts();
            setValue(data);
        }

        public void setData(short data) {
            this.data = data;
            updateValueByValueParts();
        }

        @Override
        public void readRegisterValueFromDevice(I2CAddress address) throws IOException, InterruptedException {
            short value = Short.parseShort(I2CDriverWorker.readByte(address, getHexAddress()), 16);
            setData(value);
            assertValue(value);
        }
    }
}
