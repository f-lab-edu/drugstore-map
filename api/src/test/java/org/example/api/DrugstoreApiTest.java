package org.example.api;

import org.assertj.core.api.Assertions;
import org.example.config.KeyInfo;
import org.example.config.PropertiesConfig;
import org.example.dto.DrugstoreDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

@WebMvcTest({DrugstoreApi.class, KeyInfo.class, PropertiesConfig.class})
class DrugstoreApiTest {
    @Autowired
    DrugstoreApi drugstoreApi;

    @Test
    @DisplayName("데이터를 가져오는지 확인")
    public void getDrugstoreDtoList() throws IOException, ParserConfigurationException, SAXException {
        List<DrugstoreDto> drugstoreDtoList = drugstoreApi.getDrugstoreInfo(1);

        // then
        Assertions.assertThat(drugstoreDtoList).isNotEmpty();
        Assertions.assertThat(drugstoreDtoList.size()).isEqualTo(1000);
    }
}
