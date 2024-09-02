package org.healthmap.openapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.locationtech.jts.geom.Point;


@NoArgsConstructor
@Getter
@ToString   // 차후 필요없게되면 삭제 예정
public class MedicalFacilityDto {
    private String code;        //암호화 요양 기호
    private String name;        //요양기관명(병원명)
    private String address;     //주소
    private String phoneNumber; //전화번호
    private String pageUrl; //병원 홈페이지
    private String postNumber;  //우편번호
    private String type;    //종별코드명
    private String state;   //시도코드명
    private String city;    //시군구코드
    private String town;  //읍면동
    private Point coordinate;

    public MedicalFacilityDto(String code, String name, String address, String phoneNumber, String pageUrl, String postNumber, String typeName, String stateName, String cityName, String emdongName, Point coordinate) {
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
