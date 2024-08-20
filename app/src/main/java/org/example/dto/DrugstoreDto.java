package org.example.dto;

public class DrugstoreDto {
    private int id;
    private String name;
    private String address;
    private String longitude;
    private String latitude;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    private DrugstoreDto(int id, String name, String address, String longitude, String latitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static DrugstoreDto of(int id, String name, String address, String longitude, String latitude) {
        return new DrugstoreDto(id, name, address, longitude, latitude);
    }
}
