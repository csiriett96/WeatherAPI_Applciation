package com.example.catty.comp448_assignmenttwo;

public class City {
    public static final String TABLE_NAME = "weather";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CITY= "city";


    private int id;
    private String city;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                +COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +COLUMN_CITY + " TEXT" + ")";

    public City(){
    }

    public City(int id, String city){
        this.id = id;
        this.city = city;
    }

    public int getId(){
        return id;
    }

    public String getCity(){
        return city;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
