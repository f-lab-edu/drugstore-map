package org.healthmap.openapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@Getter
@ToString   // 차후 필요없게되면 삭제 예정
public class MedicalInfoDto {
    private String code;        //암호화 요양 기호
    private String name;        //요양기관명(병원명)
    private String address;     //주소
    private String phoneNumber; //전화번호
    private String pageUrl; //병원 홈페이지
    private String postNumber;  //우편번호
    private String typeName;    //종별코드명
    private String stateName;   //시도코드명
    private String cityName;    //시군구코드
    private String emdongName;  //읍면동
    private String xPos;
    private String yPos;

    public MedicalInfoDto(String code, String name, String address, String phoneNumber, String pageUrl, String postNumber, String typeName, String stateName, String cityName, String emdongName, String xPos, String yPos) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.pageUrl = pageUrl;
        this.postNumber = postNumber;
        this.typeName = typeName;
        this.stateName = stateName;
        this.cityName = cityName;
        this.emdongName = emdongName;
        this.xPos = xPos;
        this.yPos = yPos;
    }
}
