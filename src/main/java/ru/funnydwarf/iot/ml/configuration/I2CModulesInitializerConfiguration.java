package ru.funnydwarf.iot.ml.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.InitializationState;
import ru.funnydwarf.iot.ml.Module;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

import java.util.List;

@Configuration
@Slf4j
public class I2CModulesInitializerConfiguration {

    @Bean("AHT10Initializer")
    @Lazy
    public Module.Initializer getAHT10Initializer() {
        return module -> {
            I2CAddress address = (I2CAddress) module.getAddress();
            try {
                if (!I2CDriverWorker.readDetectedDevices(address.bus()).contains(address.deviceAddress())) {
                    return InitializationState.INITIALIZATION_ERROR;
                }
                String init_register = "0xE1";
                List<String> initializeCommand = List.of("0x08", "0x00");
                I2CDriverWorker.writeBlockData(address, init_register, initializeCommand);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                return InitializationState.INITIALIZATION_ERROR;
            }
            return InitializationState.OK;
        };
    }

}
