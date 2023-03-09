package ru.funnydwarf.iot.ml.utils;

import ru.funnydwarf.iot.ml.I2CAddress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class I2CDriverWorker {

    private I2CDriverWorker(){}

    private static final List<String> i2csetRowCommand = List.of("i2cset", "-y");
    private static final List<String> i2cgetRowCommand = List.of("i2cget", "-y");
    private static final List<String> i2cdetectRowCommand = List.of("i2cdetect", "-y");
    private static final List<String> i2cdumpRowCommand = List.of("i2cdump", "-y");

    private static List<String> appendAddressToCommand(List<String> rowCommand, I2CAddress address) {
        List<String> command = new ArrayList<>(rowCommand);
        command.add(address.bus());
        command.add(address.deviceAddress());
        return command;
    }

    public static String readDetectedDevices(String bus) throws InterruptedException, IOException {
        List<String> command = new ArrayList<>(i2cdetectRowCommand);
        command.add(bus);
        Process process = new ProcessBuilder(command).start();
        process.waitFor();
        return new String(process.getInputStream().readAllBytes());
    }

    public static String readDeviceDump(I2CAddress address) throws IOException, InterruptedException {
        List<String> command = appendAddressToCommand(i2cdumpRowCommand, address);
        Process process = new ProcessBuilder(command).start();
        process.waitFor();
        return new String(process.getInputStream().readAllBytes());
    }

    public static String readByte(I2CAddress address, String register) throws InterruptedException, IOException {
        List<String> command = appendAddressToCommand(i2cgetRowCommand, address);
        command.add(register);
        Process process = new ProcessBuilder(command).start();
        process.waitFor();
        return new String(process.getInputStream().readAllBytes());
    }

    public static void writeByte(I2CAddress address, String register, String value) throws InterruptedException, IOException {
        List<String> command = appendAddressToCommand(i2csetRowCommand, address);
        command.add(register);
        command.add(value);
        new ProcessBuilder(command).start().waitFor();
    }

    public static String readBlockData(I2CAddress address, String register) throws InterruptedException, IOException {
        List<String> command = appendAddressToCommand(i2cgetRowCommand, address);
        command.add(register);
        command.add("i");
        Process process = new ProcessBuilder(command).start();
        process.waitFor();
        return new String(process.getInputStream().readAllBytes());
    }
    public static void writeBlockData(I2CAddress address, String register, List<String> values) throws IOException, InterruptedException {
        List<String> command = appendAddressToCommand(i2csetRowCommand, address);
        command.add(register);
        command.addAll(values);
        command.add("i");
        new ProcessBuilder(command).start().waitFor();
    }

}
