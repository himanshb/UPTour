package com.example.pc.uptour.classes;

import android.graphics.Bitmap;

/**
 * Created by PC on 2/12/2018.
 */

public class HotelDetails {
    private String cityID;
    private String hotelID;
    private String hotelPlaceID;
    private String hotelName;
    private Bitmap hotelImg;

    public HotelDetails(String hotelName,String hotelID,String hotelPlaceID) {
        this.hotelID=hotelID;
        this.hotelName = hotelName;
        this.hotelPlaceID=hotelPlaceID;
    }

    public Bitmap getHotelImg() {
        return hotelImg;
    }

    public void setHotelImg(Bitmap hotelImg) {
        this.hotelImg = hotelImg;
    }

    public String getCityID() {
        return cityID;
    }

    public String getHotelID() {
        return hotelID;
    }

    public String getHotelPlaceID() {
        return hotelPlaceID;
    }

    public String getHotelName() {
        return hotelName;
    }
}
