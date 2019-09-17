package com.example.catty.comp448_assignmenttwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weather_db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(City.CREATE_TABLE);
    }

    public void resetDatabase(){
        onUpgrade(this.getWritableDatabase(), 1, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + City.TABLE_NAME);
        onCreate(db);
    }

    //Insert city into the database
    public long insertCity(String city){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(City.COLUMN_CITY, city);

        long id = db.insert(City.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    //Get a city from the database by id
    public City getCity(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(City.TABLE_NAME,
                            new String[]{City.COLUMN_ID, City.COLUMN_CITY},
                            City.COLUMN_ID + "=?",
                            new String[]{String.valueOf(id)},null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        City city = new City(
                cursor.getInt(cursor.getColumnIndex(City.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(City.COLUMN_CITY)));

        cursor.close();
        return city;
    }

    //Get all cities from the database
    public List<City> getAllCities(){
        List<City> cities = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + City.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                City city = new City(
                        cursor.getInt(cursor.getColumnIndex(City.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(City.COLUMN_CITY))
                );
                cities.add(city);

            }while(cursor.moveToNext());
        }
        db.close();
        return cities;
    }

    //Get count of cities in database
    public int getCityCount(){
        String countQuery = "SELECT * FROM " + City.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    //Delete a city from the database using the city object
    public void deleteCity(City city){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(City.TABLE_NAME, City.COLUMN_ID + " = ?",
                new String[]{String.valueOf(city.getId())});
        db.close();
    }

}
