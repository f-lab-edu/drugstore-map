package org.healthmap.openapi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "key")
public class KeyProperties {
    private final String serverKey;
    private final String kakaoKey;
    private final String roadAddressKey;

    public KeyProperties(String serverKey, String kakaoKey, String roadAddressKey) {
        this.serverKey = serverKey;
        this.kakaoKey = kakaoKey;
        this.roadAddressKey = roadAddressKey;
    }

}
