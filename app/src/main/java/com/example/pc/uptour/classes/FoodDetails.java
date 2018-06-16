package com.example.pc.uptour.classes;

import android.graphics.Bitmap;

/**
 * Created by PC on 2/14/2018.
 */

public class FoodDetails {

    private String cityID;
    private String foodID;
    private String foodPlaceID;
    private String foodName;
    private Bitmap foodImg;

    public FoodDetails(String foodName,String foodID,String foodPlaceID) {
        this.foodID=foodID;
        this.foodName = foodName;
        this.foodPlaceID=foodPlaceID;
    }

    public Bitmap getFoodImg() {
        return foodImg;
    }

    public void setFoodImg(Bitmap foodImg) {
        this.foodImg = foodImg;
    }

    public String getCityID() {
        return cityID;
    }

    public String getFoodID() {
        return foodID;
    }

    public String getFoodPlaceID() {
        return foodPlaceID;
    }

    public String getFoodName() {
        return foodName;
    }
}
