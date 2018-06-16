package com.example.pc.uptour.classes;

/**
  Created by PC on 2/17/2018.
 */

public class PlaceAutocompleteDetail {
    private String placeName,placeID,placeAddress;

    public PlaceAutocompleteDetail(String placeName, String placeID, String placeAddress) {
        this.placeName = placeName;
        this.placeID = placeID;
        this.placeAddress = placeAddress;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceID() {
        return placeID;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }
}
