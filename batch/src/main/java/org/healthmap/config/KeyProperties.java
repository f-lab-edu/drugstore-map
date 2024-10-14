package org.healthmap.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "key")
public class KeyProperties {
    private final String serverKey;

    public KeyProperties(String serverKey) {
        this.serverKey = serverKey;
    }
}
