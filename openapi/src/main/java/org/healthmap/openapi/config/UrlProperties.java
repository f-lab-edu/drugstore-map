package org.healthmap.openapi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "openapi-url")
public class UrlProperties {
    private final String drugstoreUrl;
    private final String hospitalUrl;

    public UrlProperties(String drugstoreUrl, String hospitalUrl) {
        this.drugstoreUrl = drugstoreUrl;
        this.hospitalUrl = hospitalUrl;
    }
}
