package org.healthmap.openapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString   //TODO: 임시 사용
public class MedicalFacilityXmlDto {
    @JacksonXmlProperty(localName = "ykiho")
    private String code;        //암호화 요양 기호
    @JacksonXmlProperty(localName = "yadmNm")
    private String name;        //요양기관명(병원명)
    @JacksonXmlProperty(localName = "addr")
    private String address;     //주소
    @JacksonXmlProperty(localName = "telno")
    private String phoneNumber; //전화번호
    @JacksonXmlProperty(localName = "hospUrl")
    private String pageUrl; //병원 홈페이지
    @JacksonXmlProperty(localName = "postNo")
    private String postNumber;  //우편번호
    @JacksonXmlProperty(localName = "clCdNm")
    private String type;    //종별코드명
    @JacksonXmlProperty(localName = "sidoCdNm")
    private String state;   //시도코드명
    @JacksonXmlProperty(localName = "sgguCdNm")
    private String city;    //시군구코드
    @JacksonXmlProperty(localName = "emdongNm")
    private String town;  //읍면동
    @JacksonXmlProperty(localName = "XPos")
    private String xPos;
    @JacksonXmlProperty(localName = "YPos")
    private String yPos;

    public MedicalFacilityXmlDto(String code, String name, String address, String phoneNumber, String pageUrl, String postNumber, String type, String state, String city, String town, String xPos, String yPos) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.pageUrl = pageUrl;
        this.postNumber = postNumber;
        this.type = type;
        this.state = state;
        this.city = city;
        this.town = town;
        this.xPos = xPos;
        this.yPos = yPos;
    }
}
