package ru.funnydwarf.iot.ml.register;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

import java.io.IOException;

@Getter
@Slf4j
public class I2CBlock implements Readable<I2CAddress>, Writeable<I2CAddress> {
    private final int address;
    private Register[] registers = new Register[0];

    public I2CBlock(int address) {
        this.address = address;
    }

    /**
     * Устанавливает новый размер массива регистров (блока).
     * @param size
     */
    public void setBlockSize(int size) {
        Register[] registers = new Register[size];
        int i;
        //Копирование старых значений в новый массив
        for (i = 0; i < this.registers.length && i < registers.length; i++) {
            registers[i] = this.registers[i];
        }
        //Если ещё осталось место, заполняет его новыми объектами регистров
        for (; i < registers.length; i++) {
            registers[i] = new Register(address + i, Register.Size.BYTE);
        }
        this.registers = registers;
    }

    public int getBlockSize() {
        return registers.length;
    }

    public void setRegistersValue(byte[] value) {
        if (value.length != registers.length) {
            log.warn("setRegistersValue: value.length not equal registers.length!");
        }

        for (int i = 0; i < registers.length; i++) {
            if (value.length >= i) {
                return;
            }
            registers[i].setValue(value[i]);
        }
    }

    @Override
    public void readRegisterValueFromDevice(I2CAddress address) throws IOException, InterruptedException {
        int[] block = I2CDriverWorker.readBlock(address, this.address, registers.length);
        for (int i = 0; i < registers.length; i++) {
            registers[i].setValue(block[i]);
        }
    }


    @Override
    public void writeCurrentRegisterValueToDevice(I2CAddress address) throws IOException, InterruptedException {
        int[] block = new int[registers.length];
        for (int i = 0; i < block.length; i++) {
            block[i] = registers[i].getValue();
        }
        I2CDriverWorker.writeBlock(address, this.address, block);
    }
}
