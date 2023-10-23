package ru.funnydwarf.iot.ml.register;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

import java.awt.*;
import java.io.IOException;


@Getter
@NoArgsConstructor
public class AddressLedI2CAdapterConfig implements Config {
    private static final byte DEVICE_ADDRESS = 0x08;
    private static final byte PATTERN_LENGTH_REGISTER_ADDRESS = 0x00;
    private static final byte LED_COUNT_REGISTER_ADDRESS = 0x01;
    private static final byte BRIGHTNESS_REGISTER_ADDRESS = 0x02;
    private static final byte PATTERN_REGISTER_ADDRESS = 0x03;
    private static final byte UPDATE_DELAY_REGISTER_ADDRESS = 0x04;

    private final AddressLedI2CAdapterRegister patternLengthRegister = new AddressLedI2CAdapterRegister(PATTERN_LENGTH_REGISTER_ADDRESS, Register.Size.BYTE);
    private final AddressLedI2CAdapterRegister ledCountRegister = new AddressLedI2CAdapterRegister(LED_COUNT_REGISTER_ADDRESS, Register.Size.WORD);
    private final AddressLedI2CAdapterRegister brightnessRegister = new AddressLedI2CAdapterRegister(BRIGHTNESS_REGISTER_ADDRESS, Register.Size.BYTE);
    private final I2CBlock patternI2CBlock = new I2CBlock(PATTERN_REGISTER_ADDRESS);
    private final AddressLedI2CAdapterRegister updateDelayRegister = new AddressLedI2CAdapterRegister(UPDATE_DELAY_REGISTER_ADDRESS, Register.Size.WORD);
    public static class AddressLedI2CAdapterRegister extends Register implements Readable<I2CAddress>, Writeable<I2CAddress> {

        public AddressLedI2CAdapterRegister(int address, Size size) {
            super(address, size);
        }

        @Override
        public void readRegisterValueFromDevice(I2CAddress address) throws IOException, InterruptedException {
            if (getSize().equals(Size.BYTE)) {
                setValue(I2CDriverWorker.readByte(address, getAddress()));
            } else if (getSize().equals(Size.WORD)) {
                setValue(I2CDriverWorker.readWord(address, getAddress()));
            } else {
                throw new RuntimeException("Unknown or unsupported data size!");
            }
        }

        @Override
        public void writeCurrentRegisterValueToDevice(I2CAddress address) throws IOException, InterruptedException {
            if (getSize().equals(Size.BYTE)) {
                I2CDriverWorker.writeByte(address, getAddress(), getValue());
            } else if (getSize().equals(Size.WORD)) {
                I2CDriverWorker.writeWord(address, getAddress(), getValue());
            } else {
                throw new RuntimeException("Unknown or unsupported data size!");
            }
        }

        public void setValueAndWriteToDevice(I2CAddress address, int value) throws IOException, InterruptedException {
            super.setValue(value);
            writeCurrentRegisterValueToDevice(address);
        }
    }
}
