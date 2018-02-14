package com.example.pc.uptour;

/**
 * Created by PC on 2/14/2018.
 */

public class PlaceDetails {
    private String cityID;
    private String placeID;
    private String placePlaceID;
    private String placeName;

    public PlaceDetails(String placeName,String placeID,String placePlaceID) {
        this.placeID=placeID;
        this.placeName = placeName;
        this.placePlaceID=placePlaceID;
    }

    public String getCityID() {
        return cityID;
    }

    public String getPlaceID() {
        return placeID;
    }

    public String getPlacePlaceID() {
        return placePlaceID;
    }

    public String getPlaceName() {
        return placeName;
    }
}
