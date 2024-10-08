package org.healthmap.openapi.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest({KeyProperties.class, PropertiesConfig.class})
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
