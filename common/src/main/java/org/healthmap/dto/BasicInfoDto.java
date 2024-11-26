package org.healthmap.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.healthmap.dto.serializer.GeoJsonPointDeserializer;
import org.healthmap.dto.serializer.GeoJsonPointSerializer;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

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

    @JsonSerialize(using = GeoJsonPointSerializer.class)
    @JsonDeserialize(using = GeoJsonPointDeserializer.class)
    private GeoJsonPoint coordinate;

    public BasicInfoDto(String code, String name, String address, String phoneNumber, String pageUrl, String postNumber, String typeName, String stateName, String cityName, String emdongName, GeoJsonPoint coordinate) {
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

    public void changeCoordinate(GeoJsonPoint point) {
        this.coordinate = point;
    }
}
