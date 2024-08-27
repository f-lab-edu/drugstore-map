package org.healthmap.openapi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "key")
public class KeyInfo {
    private final String serverKey;

    public KeyInfo(String serverKey) {
        this.serverKey = serverKey;
    }
}
