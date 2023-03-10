package ru.funnydwarf.iot.ml;


import lombok.Getter;

@Getter
public class I2CModuleGroup extends ModuleGroup {

    private final String bus;

    public I2CModuleGroup(String bus) {
        super("I2C", "Inter-Integrated Circuit");
        this.bus = bus;
    }

    @Override
    protected InitializationState initialize() throws Exception {
        ProcessBuilder builder = new ProcessBuilder("i2cdetect", "-l");
        Process process = builder.start();
        process.waitFor();
        if (new String(process.getInputStream().readAllBytes()).contains("i2c-" + bus)) {
            return InitializationState.OK;
        }
        return InitializationState.INITIALIZATION_ERROR;
    }
}
