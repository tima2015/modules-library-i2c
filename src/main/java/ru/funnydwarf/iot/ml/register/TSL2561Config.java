package ru.funnydwarf.iot.ml.register;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

import java.io.IOException;

@Getter
@NoArgsConstructor
public class TSL2561Config {

    private static final int COMMAND_BIT = 0b10000000;
    private static final int CLEAR_BIT = 0b01000000;
    private static final int WORD_BIT = 0b00100000;
    private static final int BLOCK_BIT = 0b00010000;

    private final ControlRegister controlRegister = new ControlRegister();
    private final TimingRegister timingRegister = new TimingRegister();
    private final IDRegister idRegister = new IDRegister();
    private final DataRegister data0LowRegister = new DataRegister(0x0C);
    private final DataRegister data0HighRegister = new DataRegister(0x0D);
    private final DataRegister data1LowRegister = new DataRegister(0x0E);
    private final DataRegister data1HighRegister = new DataRegister(0x0F);


    @Getter
    public static final class TimingRegister extends Register implements Writeable<I2CAddress>, Readable<I2CAddress> {

        private Gain gain = Gain.LOW;
        private Manual manual = Manual.START;
        private Integrate integrate = Integrate.CYCLE_402_MS;

        private TimingRegister() {
            super(0x1, Size.BYTE);
            setValue(0b0001_1011);
        }

        @Override
        protected void updateValueByValueParts() {
            super.updateValueByValueParts();
            int newValue = 0;
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
            int value = I2CDriverWorker.readByte(address, COMMAND_BIT | getAddress());
            setGain(ValuePart.getFromBits(value, Gain.inBits, Gain.gains));
            setManual(ValuePart.getFromBits(value, Manual.inBits, Manual.manuals));
            setIntegrate(ValuePart.getFromBits(value, Integrate.inBits, Integrate.integrates));
            assertValue(value);
        }

        @Override
        public void writeCurrentRegisterValueToDevice(I2CAddress address) throws IOException, InterruptedException {
            I2CDriverWorker.writeByte(address,COMMAND_BIT | getAddress(), getValue());
        }

        public static final class Gain extends ValuePart {

            private Gain(int bits) {
                super(bits);
            }

            public static final Gain LOW = new Gain(0b0000_0000);
            public static final Gain HIGH = new Gain(0b0001_0000);
            private static final int inBits = 0b0001_0000;
            private static final Gain[] gains = new Gain[] { LOW, HIGH };
        }

        public static final class Manual extends ValuePart {

            private Manual(int bits) {
                super(bits);
            }

            public static final Manual START = new Manual(0b0000_1000);
            public static final Manual STOP = new Manual(0b0000_0000);
            private static final int inBits = 0b0000_1000;
            private static final Manual[] manuals = new Manual[] {START, STOP};
        }

        @Getter
        public static final class Integrate extends ValuePart {

            private int milliseconds;
            private Integrate(int bits, int milliseconds) {
                super(bits);
                this.milliseconds = milliseconds;
            }

            public static final Integrate CYCLE_13_7_MS = new Integrate(0b0000_0000, 14);
            public static final Integrate CYCLE_101_MS = new Integrate(0b0000_0001, 101);
            public static final Integrate CYCLE_402_MS = new Integrate(0b0000_0010, 402);
            public static final Integrate NA = new Integrate(0b0000_0011, 0);
            private static final int inBits = 0b0000_0011;
            private static final Integrate[] integrates = new Integrate[] { CYCLE_13_7_MS, CYCLE_101_MS, CYCLE_402_MS, NA};
        }
    }

    @Getter
    public static final class ControlRegister extends Register implements Writeable<I2CAddress>, Readable<I2CAddress> {

        private Power power = Power.POWER_DOWN;
        public ControlRegister() {
            super(0x0, Size.BYTE);
            setValue(0b0000_0000);
        }

        @Override
        protected void updateValueByValueParts() {
            super.updateValueByValueParts();
            int newValue = 0;
            newValue |= power.getBits();
            setValue(newValue);
        }

        public void setPower(Power power) {
            this.power = power;
            updateValueByValueParts();
        }

        @Override
        public void readRegisterValueFromDevice(I2CAddress address) throws IOException, InterruptedException {
            int value = I2CDriverWorker.readByte(address, COMMAND_BIT | getAddress());
            setPower(ValuePart.getFromBits(value, Power.inBits, Power.powers));
            assertValue(value);
        }

        @Override
        public void writeCurrentRegisterValueToDevice(I2CAddress address) throws IOException, InterruptedException {
            I2CDriverWorker.writeByte(address, COMMAND_BIT | getAddress(), getValue());
        }

        public static final class Power extends ValuePart {
            private Power(int bits) {
                super(bits);
            }

            public static final Power POWER_DOWN = new Power(0b0000_0000);
            public static final Power POWER_UP = new Power(0b0000_0011);

            private static final int inBits = 0b0000_0011;
            private static final Power[] powers = new Power[] { POWER_DOWN, POWER_UP };
        }
    }

    // TODO: 09.03.2023 Interrupt Threshold Register & Interrupt Control Register

    @Getter
    public static final class IDRegister extends Register implements Readable<I2CAddress> {

        private PartNumber partNumber = PartNumber.UNKNOWN;
        private int revisionNumber = 0b0000_0000;
        private IDRegister() {
            super(0x0A, Size.BYTE);
        }

        @Override
        protected void updateValueByValueParts() {
            super.updateValueByValueParts();
            int newValue = 0;
            newValue |= partNumber.bits;
            newValue |= revisionNumber;
            setValue(newValue);
        }

        public void setPartNumber(PartNumber partNumber) {
            this.partNumber = partNumber;
            updateValueByValueParts();
        }

        public void setRevisionNumber(int revisionNumber) {
            this.revisionNumber = revisionNumber;
            updateValueByValueParts();
        }

        @Override
        public void readRegisterValueFromDevice(I2CAddress address) throws IOException, InterruptedException {
            int value = I2CDriverWorker.readByte(address, getAddress());
            setPartNumber(ValuePart.getFromBits(value, PartNumber.inBits, PartNumber.partNumbers));
            setRevisionNumber(value & 0b0000_1111);
            assertValue(value);
        }

        public static final class PartNumber extends ValuePart {
            private PartNumber(int bits) {
                super(bits);
            }

            public static final PartNumber UNKNOWN = new PartNumber(0b1111_0000);
            public static final PartNumber TSL2560 = new PartNumber(0b0000_0000);
            public static final PartNumber TSL2561 = new PartNumber(0b0001_0000);
            private static final int inBits = 0b1111_0000;
            private static final PartNumber[] partNumbers = new PartNumber[] {UNKNOWN, TSL2560, TSL2561};
        }
    }

    @Getter
    public static final class DataRegister extends Register implements Readable<I2CAddress> {

        private int data = 0b0000_0000;

        private DataRegister(int address) {
            super(address, Size.BYTE);
        }

        @Override
        protected void updateValueByValueParts() {
            super.updateValueByValueParts();
            setValue(data);
        }

        public void setData(int data) {
            this.data = data;
            updateValueByValueParts();
        }

        @Override
        public void readRegisterValueFromDevice(I2CAddress address) throws IOException, InterruptedException {
            int value = I2CDriverWorker.readByte(address, COMMAND_BIT | WORD_BIT | getAddress());
            setData(value);
            assertValue(value);
        }
    }
}
