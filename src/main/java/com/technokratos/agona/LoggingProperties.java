package com.technokratos.agona;

import com.technokratos.agona.enums.Output;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custom.logging")
@Getter
@Setter
public class LoggingProperties {

    private boolean enabled = true;
    private Output output = Output.CONSOLE;
    private String filePath = "logs/application.log";

}