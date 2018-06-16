package com.example.pc.uptour.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pc.uptour.classes.CityDetails;
import com.example.pc.uptour.classes.FoodDetails;
import com.example.pc.uptour.classes.HotelDetails;
import com.example.pc.uptour.classes.PlaceDetails;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by PC on 1/17/2018.
 */

public class DatabaseAccess  {
    private MyDatabaseOpenHelper myDatabaseOpenHelper;
    private SQLiteDatabase mSqLiteDatabase;

    /**
     * constructor for creating
     *
     * @param context
     */
    public DatabaseAccess(Context context) {
        this.myDatabaseOpenHelper = new MyDatabaseOpenHelper(context);
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.mSqLiteDatabase = myDatabaseOpenHelper.getWritableDatabase();
    }

    public  void close(){
        if (mSqLiteDatabase!=null)
            mSqLiteDatabase.close();
    }

    public boolean insertInCities(String placeID,String cityName){
        ContentValues cv=new ContentValues();
        cv.put("city_place_id",placeID);
        long check=mSqLiteDatabase.update("cities",cv,"city_name=?",new String[]{cityName});
        return check>0;
    }

    //check name present in database
    public  String checkCityNameForInsert(String name){
        Cursor cursor=mSqLiteDatabase.rawQuery("SELECT city_id,city_name,city_place_id from cities WHERE city_name='"+name +"'",null);
        if( cursor != null && cursor.moveToFirst() ){
            if (cursor.getString(cursor.getColumnIndex("city_name")).equals(name)&&
                    cursor.getString(cursor.getColumnIndex("city_place_id"))==null) {
                String id=cursor.getString(0);
                cursor.close();
                return id;
            }
            else if (cursor.getString(cursor.getColumnIndex("city_name")).equals(name)&&
                    cursor.getString(cursor.getColumnIndex("city_place_id"))!=null){
                String id=cursor.getString(0);
                cursor.close();
                return id;
            }
        }
        return null;
    }
    //check name present in database
    public  String checkCityName(String name){
        Cursor cursor=mSqLiteDatabase.rawQuery("SELECT city_id,city_name,city_place_id from cities WHERE city_name='"+name +"'",null);
        if( cursor != null && cursor.moveToFirst() ){
            if (cursor.getString(cursor.getColumnIndex("city_name")).equals(name)&&
                    cursor.getString(cursor.getColumnIndex("city_place_id"))!=null) {
                String id=cursor.getString(0);
                cursor.close();
                return id;
            }
        }
        return null;
    }

    //match the address with city
    public String checkSubString(String address){
        Cursor cursor=mSqLiteDatabase.rawQuery("select city_id,city_name,city_pin from cities",null);
        while (cursor.moveToNext()){
            String pin=Integer.toString(cursor.getInt(2));
            if (address.contains(pin)) {
                return cursor.getString(cursor.getColumnIndex("city_id"));
            }
            else {
                String city = cursor.getString(cursor.getColumnIndex("city_name"));
                if (address.contains(city + ", Uttar Pradesh")) {
                    return cursor.getString(cursor.getColumnIndex("city_id"));
                }
            }
        }
        cursor.close();
        return null;
    }

    //inserting hotel
    public boolean insertHotel(String hotePlacelID,String hotelName,String cityID){
        ContentValues cv=new ContentValues();
        cv.put("hotel_name",hotelName);
        cv.put("city_id",cityID);
        cv.put("hotel_place_id",hotePlacelID);
        long check=mSqLiteDatabase.insert("hotels",null,cv);
        return check>0;
    }

    //inserting place
    public boolean insertPlace(String placePlaceID,String placeName,String cityID){
        ContentValues cv=new ContentValues();
        cv.put("place_name",placeName);
        cv.put("city_id",cityID);
        cv.put("place_place_id",placePlaceID);
        long check=mSqLiteDatabase.insert("places",null,cv);
        return check>0;
    }

    //inserting food
    public boolean insertFood(String foodPlaceID,String foodName,String cityID){
        ContentValues cv=new ContentValues();
        cv.put("food_name",foodName);
        cv.put("food_place_id",foodPlaceID);
        //'returnID' is actually the last_insert_rowID
        long returnID,id=getCount(foodName);
        if (id>0) {
            // returnID = db.update("notes", cv, "_id=?", new String[]{Integer.toString(id)});
            return insertInJunction(id,cityID);
        }
        else {
            returnID = mSqLiteDatabase.insert("food", null, cv);
            return insertInJunction(returnID,cityID);
        }
    }


    //check weather food is present or not in db.
    private long getCount(String text) {
        String query = "select food_id from food where food_name = ?";
        Cursor c=mSqLiteDatabase.rawQuery(query, new String[] {text});
        if (c!=null&&c.moveToFirst()) {
            long id=c.getLong(c.getColumnIndex("food_id"));
            c.close();
            return id;
        }
        return 0;
    }

    //inserting food_id and city_id
    private boolean insertInJunction(long foodID,String cityID){
        ContentValues cv=new ContentValues();
        cv.put("food_id",foodID);
        cv.put("city_id",cityID);
        long check=mSqLiteDatabase.insert("jun_city_food",null,cv);
        return check>0;
    }


    public List<HotelDetails> getHotels(String cityID){
        Cursor cursor=mSqLiteDatabase.rawQuery("SELECT * from hotels WHERE hotels.city_id='"+cityID +"'order by hotel_name",null);
        List<HotelDetails> hotelList=new LinkedList<>();
        while (cursor.moveToNext()){
            String hotelPlaceID=cursor.getString(cursor.getColumnIndex("hotel_place_id"));
            if (hotelPlaceID!=null) {
                String hotelName=cursor.getString(cursor.getColumnIndex("hotel_name"));
                String hotelID=cursor.getString(cursor.getColumnIndex("hotel_id"));
                HotelDetails hotel = new HotelDetails(hotelName,hotelID,hotelPlaceID);
                hotelList.add(hotel);
            }
        }
        cursor.close();
        return hotelList;
    }

    public List<FoodDetails> getFood(String cityID){
        String query="SELECT food.*, cities.city_id\n" +
                "FROM cities\n" +
                "INNER JOIN jun_city_food\n" +
                "ON cities.city_id = jun_city_food.city_id\n" +
                "INNER JOIN food\n" +
                "ON food.food_id = jun_city_food.food_id\n" +
                "WHERE jun_city_food.city_id = '"+cityID +"'order by food_name";
        Cursor cursor=mSqLiteDatabase.rawQuery(query,null);
        List<FoodDetails> foodList=new LinkedList<>();
        while (cursor.moveToNext()){
            String foodPlaceID=cursor.getString(cursor.getColumnIndex("jun_city_food.food_place_id"));
            if (foodPlaceID!=null) {
                String foodName=cursor.getString(cursor.getColumnIndex("food_name"));
                String foodID=cursor.getString(cursor.getColumnIndex("food_id"));
                FoodDetails food = new FoodDetails(foodName,foodID,foodPlaceID);
                foodList.add(food);
            }
        }
        cursor.close();
        return foodList;
    }

    public List<PlaceDetails> getPlaces(String cityID){
        Cursor cursor=mSqLiteDatabase.rawQuery("SELECT * from places WHERE places.city_id='"+cityID +"'order by place_name",null);
        List<PlaceDetails> placeList=new LinkedList<>();
        while (cursor.moveToNext()){
            String placePlaceID=cursor.getString(cursor.getColumnIndex("place_place_id"));
            if (placePlaceID!=null) {
                String placeName=cursor.getString(cursor.getColumnIndex("place_name"));
                String placeId=cursor.getString(cursor.getColumnIndex("place_id"));
                PlaceDetails place = new PlaceDetails(placeName,placeId,placePlaceID);
                placeList.add(place);
            }
        }
        cursor.close();
        return placeList;
    }

    public List<CityDetails> getCities(){
        List<CityDetails> cityList=new ArrayList<>();
        Cursor cursor=mSqLiteDatabase.rawQuery("select * from cities order by city_name",null);
        while (cursor.moveToNext()){
            int cityID=cursor.getInt(cursor.getColumnIndex("city_id"));
            String cityName=cursor.getString(cursor.getColumnIndex("city_name"));
            String cityPlaceID=cursor.getString(cursor.getColumnIndex("city_place_id"));
            CityDetails city=new CityDetails(Integer.toString(cityID),cityName,cityPlaceID);
            cityList.add(city);
        }
        cursor.close();
        return cityList;
    }
    public List<CityDetails> getCitiesByTags(String tag){
        List<CityDetails> cityList=new ArrayList<>();
        Cursor cursor=mSqLiteDatabase.rawQuery("select * from cities where city_tag LIKE '%"+tag +"%'order by city_name ",null);
        while (cursor.moveToNext()){
            int cityID=cursor.getInt(cursor.getColumnIndex("city_id"));
            String cityName=cursor.getString(cursor.getColumnIndex("city_name"));
            String cityPlaceID=cursor.getString(cursor.getColumnIndex("city_place_id"));
            CityDetails city=new CityDetails(Integer.toString(cityID),cityName,cityPlaceID);
            cityList.add(city);
        }
        cursor.close();
        return cityList;
    }

    public String getCityDescription(String cityID){
        Cursor cursor=mSqLiteDatabase.rawQuery("select city_description from cities where city_id='"+cityID+"'",null);
        if (cursor!=null&&cursor.moveToFirst()) {
            String description = cursor.getString(cursor.getColumnIndex("city_description"));
            cursor.close();
            return description;
        }
        return "";
    }

    public String getCityPlaceID(String cityID){
        Cursor cursor=mSqLiteDatabase.rawQuery("select city_place_id from cities where city_id='"+cityID+"'",null);
        if (cursor!=null&&cursor.moveToFirst()) {
            String place_id = cursor.getString(cursor.getColumnIndex("city_place_id"));
            cursor.close();
            return place_id;
        }
        return "";
    }
    public String getCityName(String cityID){
        Cursor cursor=mSqLiteDatabase.rawQuery("select city_name from cities where city_id='"+cityID+"'",null);
        if (cursor!=null&&cursor.moveToFirst()) {
            String cityName = cursor.getString(cursor.getColumnIndex("city_name"));
            cursor.close();
            return cityName;
        }
        return "";
    }
}
