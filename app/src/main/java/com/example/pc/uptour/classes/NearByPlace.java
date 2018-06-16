package com.example.pc.uptour.classes;

/**
 * Created by PC on 3/1/2018.
 */

public class NearByPlace {
    private String placeType;
    private String placeName;
    private String placeId;
    private String placeRating;

    public NearByPlace(String placeName, String placeId) {
        this.placeName = placeName;
        this.placeId = placeId;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceRating() {
        return placeRating;
    }
}
