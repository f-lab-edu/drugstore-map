package org.healthmap.mapapi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "kakao-map")
public class UrlProperties {
    private final String mapAddressUrl;

    public UrlProperties(String mapAddressUrl) {
        this.mapAddressUrl = mapAddressUrl;
    }
}
