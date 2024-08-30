package org.healthmap.openapi.api;

import org.assertj.core.api.Assertions;
import org.healthmap.openapi.config.KeyInfo;
import org.healthmap.openapi.config.PropertiesConfig;
import org.healthmap.openapi.dto.MedicalFacilityDto;
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
        List<MedicalFacilityDto> drugstoreDtoList = drugstoreApi.getDrugstoreInfo(1);

        // then
        Assertions.assertThat(drugstoreDtoList).isNotEmpty();
        Assertions.assertThat(drugstoreDtoList.size()).isEqualTo(1000);
    }
}
