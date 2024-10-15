package org.healthmap.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BasicInfoDto {
    private String code;        //암호화 요양 기호
    private String name;        //요양기관명(병원명)
    private String address;     //주소
    private String phoneNumber; //전화번호
    private String pageUrl;     //병원 홈페이지
    private String postNumber;  //우편번호
    private String type;        //종별코드명
    private String state;       //시도코드명
    private String city;        //시군구코드
    private String town;        //읍면동
    private String coordinate;   //좌표

    public BasicInfoDto(String code, String name, String address, String phoneNumber, String pageUrl, String postNumber, String typeName, String stateName, String cityName, String emdongName, String coordinate) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.pageUrl = pageUrl;
        this.postNumber = postNumber;
        this.type = typeName;
        this.state = stateName;
        this.city = cityName;
        this.town = emdongName;
        this.coordinate = coordinate;
    }
}
