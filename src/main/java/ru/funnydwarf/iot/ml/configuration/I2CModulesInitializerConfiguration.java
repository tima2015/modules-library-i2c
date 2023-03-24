package ru.funnydwarf.iot.ml.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.InitializationState;
import ru.funnydwarf.iot.ml.Module;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

import java.util.List;

@Configuration
@Slf4j
public class I2CModulesInitializerConfiguration {

    @Slf4j
    @Component
    @Lazy
    public static class AHT10Initializer implements Module.Initializer {
        @Override
        public InitializationState initialize(Module module) {
            I2CAddress address = (I2CAddress) module.getAddress();
            try {
                int init_register = 0xE1;
                int[] initializeCommand = new int[]{ 0x08, 0x00 };
                I2CDriverWorker.writeBlock(address, init_register, initializeCommand);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                return InitializationState.INITIALIZATION_ERROR;
            }
            return InitializationState.OK;
        }
    }

}
