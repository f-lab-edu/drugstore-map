package org.healthmap.mapapi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "key")
public class KeyProperties {
    private final String kakaoKey;

    public KeyProperties(String kakaoKey) {
        this.kakaoKey = kakaoKey;
    }
}
