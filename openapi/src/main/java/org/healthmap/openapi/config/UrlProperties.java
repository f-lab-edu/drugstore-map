package org.healthmap.openapi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "openapi-url")
public class UrlProperties {
    private final String drugstoreUrl;
    private final String hospitalUrl;
    private final String detailUrl;
    private final String mapAddressUrl;
    private final String roadAddressUrl;

    public UrlProperties(String drugstoreUrl, String hospitalUrl, String detailUrl, String mapAddressUrl, String roadAddressUrl) {
        this.drugstoreUrl = drugstoreUrl;
        this.hospitalUrl = hospitalUrl;
        this.detailUrl = detailUrl;
        this.mapAddressUrl = mapAddressUrl;
        this.roadAddressUrl = roadAddressUrl;
    }
}
