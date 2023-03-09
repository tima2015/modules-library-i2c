package ru.funnydwarf.iot.ml;

public record I2CAddress(String bus, String deviceAddress, Class<? extends Module> device){}
