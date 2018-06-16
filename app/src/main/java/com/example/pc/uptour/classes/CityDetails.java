package com.example.pc.uptour.classes;

/**
 * Created by PC on 1/19/2018.
 */

public class CityDetails {
    private String cityID;
    private String cityName;
    private String cityPlaceID;

    public CityDetails(String cityID, String cityName, String cityPlaceID) {
        this.cityID = cityID;
        this.cityName = cityName;
        this.cityPlaceID = cityPlaceID;
    }

    public String getCityID() {
        return cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCityPlaceID() {
        return cityPlaceID;
    }
}
