package com.example.pc.uptour;

/**
 * Created by PC on 2/14/2018.
 */

public class FoodDetails {

    private String cityID;
    private String foodID;
    private String foodPlaceID;
    private String foodName;

    public FoodDetails(String foodName,String foodID,String foodPlaceID) {
        this.foodID=foodID;
        this.foodName = foodName;
        this.foodPlaceID=foodPlaceID;
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
