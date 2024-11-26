package org.healthmap.openapi.config;

import org.assertj.core.api.Assertions;
import org.healthmap.openapi.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = TestConfig.class)
@ContextConfiguration(classes = TestConfig.class)
class KeyPropertiesTest {
    @Autowired
    KeyProperties keyProperties;

    @Test
    @DisplayName("API key 확인하기")
    public void KeyInfoTest() {
        System.out.println(keyProperties.getServerKey());
        Assertions.assertThat(keyProperties.getServerKey()).isNotNull();
    }
}
