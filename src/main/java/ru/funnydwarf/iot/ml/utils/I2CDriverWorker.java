package ru.funnydwarf.iot.ml.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.funnydwarf.iot.ml.I2CAddress;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class I2CDriverWorker {
    private static final List<String> i2csetRowCommand = List.of("i2cset", "-y");
    private static final List<String> i2cgetRowCommand = List.of("i2cget", "-y");
    private static final List<String> i2cdetectRowCommand = List.of("i2cdetect", "-y");
    private static final List<String> i2cdumpRowCommand = List.of("i2cdump", "-y");
    private static final List<String> byteArgs = List.of("b");
    private static final List<String> wordArgs = List.of("w");
    private static final String blockArg = "i";
    private static final int timeout = 5;
    private static final int defaultBlockLength = 32;

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
        process.waitFor(timeout, TimeUnit.SECONDS);
        return readStringFromInputStream(process.getInputStream());
    }

    public static String readDeviceDump(I2CAddress address) throws IOException, InterruptedException {
        List<String> command = appendAddressToCommand(i2cdumpRowCommand, address);
        Process process = new ProcessBuilder(command).start();
        process.waitFor(timeout, TimeUnit.SECONDS);
        return readStringFromInputStream(process.getInputStream());
    }

    private static String read(I2CAddress address, int register, List<String> args) throws IOException, InterruptedException {
        List<String> command = appendAddressToCommand(i2cgetRowCommand, address);
        command.add(numToHexStr(register));
        command.addAll(args);
        Process process = new ProcessBuilder(command).start();
        process.waitFor(timeout, TimeUnit.SECONDS);
        return readStringFromInputStream(process.getInputStream());
    }

    public static int readByte(I2CAddress address, int register) throws IOException, InterruptedException {
        return hexStrToInt(read(address, register, byteArgs));
    }

    public static int readWord(I2CAddress address, int register) throws IOException, InterruptedException {
        return hexStrToInt(read(address, register, wordArgs));
    }

    public static int[] readBlock(I2CAddress address, int register, int length) throws IOException, InterruptedException {
        String[] row = read(address, register, List.of(blockArg, String.valueOf(length))).split("\\s");
        int[] block = new int[length];

        for (int i = 0; i < row.length; i++) {
            block[i] = hexStrToInt(row[i]);
        }

        return block;
    }

    public static int[] readBlock(I2CAddress address, int register) throws IOException, InterruptedException {
        return readBlock(address, register, defaultBlockLength);
    }

    private static void write(I2CAddress address, int register, int[] values, List<String> args) throws IOException, InterruptedException {
        List<String> command = appendAddressToCommand(i2csetRowCommand, address);
        command.add(numToHexStr(register));
        for (int value : values) {
            command.add(numToHexStr(value));
        }
        command.addAll(args);
        new ProcessBuilder(command).start().waitFor(timeout, TimeUnit.SECONDS);
    }

    public static void writeByte(I2CAddress address, int register, int value) throws IOException, InterruptedException {
        write(address, register, new int[]{value}, byteArgs);
    }

    public static void writeWord(I2CAddress address, int register, int value) throws IOException, InterruptedException {
        write(address, register, new int[]{value}, wordArgs);
    }

    public static void writeBlock(I2CAddress address, int register, int[] value) throws IOException, InterruptedException {
        write(address, register, value, List.of(blockArg, String.valueOf(value.length)));
    }

    private static String readStringFromInputStream(InputStream inputStream) {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        s.close();
        return result;
    }

    private static String numToHexStr(int num) {
        return "0x%h".formatted(num);
    }

    private static int hexStrToInt(String hexStr) {
        return Integer.parseUnsignedInt(hexStr.replace("0x", "").replace("\n", ""), 16);
    }

}
