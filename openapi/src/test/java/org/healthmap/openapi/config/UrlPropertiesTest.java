package org.healthmap.openapi.config;

import org.assertj.core.api.Assertions;
import org.healthmap.openapi.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestConfig.class)
class UrlPropertiesTest {
    @Autowired
    UrlProperties urlProperties;
    Logger log = LoggerFactory.getLogger(UrlPropertiesTest.class);

    @Test
    @DisplayName("drugstoreUrl이 있는지 확인한다")
    public void drugstoreUrl() {
        log.info("drugstoreUrl : {}", urlProperties.getDrugstoreUrl());
        Assertions.assertThat(urlProperties.getDrugstoreUrl()).isNotNull();
        Assertions.assertThat(urlProperties.getDrugstoreUrl()).isNotEmpty();
    }

    @Test
    @DisplayName("hospitalUrl이 있는지 확인한다")
    public void hospitalUrl() {
        log.info("hospitalUrl : {}", urlProperties.getHospitalUrl());
        Assertions.assertThat(urlProperties.getHospitalUrl()).isNotNull();
        Assertions.assertThat(urlProperties.getHospitalUrl()).isNotEmpty();
    }
}
