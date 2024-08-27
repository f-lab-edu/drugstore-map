package org.healthmap.app.controller;

import org.healthmap.app.dto.DrugstoreDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/drugstore")
public class DrugstoreController {

    @GetMapping
    public DrugstoreDto findAllDrugstore() {
        DrugstoreDto drugstoreDto = DrugstoreDto.of(1, "Lee", "서울시", "127.08515659273706", "37.488132562487905");

        return drugstoreDto;
    }

    @GetMapping("/around")
    public ResponseEntity<List<DrugstoreDto>> findDrugstoreAround(
            @RequestParam(name = "longitude") String longitude,
            @RequestParam(name = "latitude") String latitude
    ) {
        List<DrugstoreDto> tempList = new ArrayList<>();
        DrugstoreDto drugstoreDto1 = DrugstoreDto.of(1, "test", "서울시", longitude, latitude);
        DrugstoreDto drugstoreDto2 = DrugstoreDto.of(1, "test", "서울시", longitude, latitude);
        tempList.add(drugstoreDto1);
        tempList.add(drugstoreDto2);

        return ResponseEntity.ok(tempList);
    }

}
