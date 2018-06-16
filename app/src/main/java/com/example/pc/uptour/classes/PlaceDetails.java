package com.example.pc.uptour.classes;

import android.graphics.Bitmap;

/**
 * Created by PC on 2/14/2018.
 */

public class PlaceDetails {

    private String placeID;
    private String placePlaceID;
    private String placeName;
    private Bitmap placeImg;

    public PlaceDetails(String placeName,String placeID,String placePlaceID) {
        this.placeID=placeID;
        this.placeName = placeName;
        this.placePlaceID=placePlaceID;
    }

    public Bitmap getPlaceImg() {
        return placeImg;
    }

    public void setPlaceImg(Bitmap placeImg) {
        this.placeImg = placeImg;
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
