package ru.letnes.materialdesignsceleton.adapters;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import ru.letnes.materialdesignsceleton.R;
import ru.letnes.materialdesignsceleton.model.WeatherData;
import ru.letnes.materialdesignsceleton.service.APIservice;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DbAdapter {
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
    private static final int DATABASE_VERSION = 20;
    private static final String DATABASE_NAME = "data";
    private static final String DATA_TABLE_CITYS = "city";

    private static final String TABLE_CREATE_CITYS = "create table city "
            + "(_id integer primary key autoincrement, name text,sys_country text, number integer, lastupdate integer, "
            + "weather_description text, weather_id integer, main_pressure text, main_temp double,"
            + "main_humidity integer, wind_speed float, wind_deg text, sys_sunrise long, sys_sunset long, weather_icon text, city_id text);";


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(TABLE_CREATE_CITYS);


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + DATA_TABLE_CITYS);

            onCreate(db);
        }
    }

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

        return this;

    }

    public DbAdapter openReadOnly() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();

        return this;
    }

    public void close() {
        mDbHelper.close();

    }

    public void addTestData(){


        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        Resources res = mCtx.getResources();
        XmlResourceParser _xml = res.getXml(R.xml.city_records);
        try {
            if(getCitys().size() == 0) {
                int eventType = _xml.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if ((eventType == XmlPullParser.START_TAG)
                            && (_xml.getName().equals("record"))) {


                        values.put("name", _xml.getAttributeValue(0));
                        values.put("lastupdate", _xml.getAttributeValue(1));
                        values.put("weather_description", _xml.getAttributeValue(2));
                        values.put("weather_id", _xml.getAttributeValue(3));
                        values.put("main_pressure", _xml.getAttributeValue(4));
                        values.put("main_temp", _xml.getAttributeValue(5));
                        values.put("main_humidity", _xml.getAttributeValue(6));
                        values.put("wind_speed", _xml.getAttributeValue(7));
                        values.put("wind_deg", _xml.getAttributeValue(8));
                        values.put("sys_sunrise", _xml.getAttributeValue(9));
                        values.put("sys_sunset", _xml.getAttributeValue(10));
                        values.put("sys_country", _xml.getAttributeValue(11));
                        values.put("weather_icon", _xml.getAttributeValue(12));
                        values.put("city_id", _xml.getAttributeValue(13));
                        mDb.insert(DATA_TABLE_CITYS, null, values);
                    }
                    eventType = _xml.next();
                }
            } else {
                Intent in = new Intent("EXIST");
                mCtx.sendBroadcast(in);
            }
        }
        // Catch errors
        catch (XmlPullParserException | IOException e) {
            Log.e("ERROR", e.getMessage(), e);
        } finally {
            // Close the xml file
            Intent in = new Intent("SWAP");
            mCtx.sendBroadcast(in);
            _xml.close();

        }
    }

    public ArrayList<WeatherData> getCitys(){

        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        ArrayList<WeatherData> Citys = new ArrayList<WeatherData>();
        try {
            Cursor mCursor = mDb.query(true, DATA_TABLE_CITYS, null, null, null, null, null, null, null);

            if (mCursor != null) {
                mCursor.moveToFirst();



                while(!mCursor.isAfterLast()) {

                    WeatherData wdata = new WeatherData(mCursor);

                    Citys.add(wdata);
                    mCursor.moveToNext();
                }

                mCursor.close();
            }


            return Citys;
        } catch (Exception e) {
            Log.v("ERROR",e.toString());
            return null;
        }
    }

    public ArrayList<WeatherData> getCity(String cityTitle)throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        ArrayList<WeatherData> city = new ArrayList<WeatherData>();

        //Cursor mCursor = mDb.rawQuery("SELECT * FROM " + DATA_TABLE_CITYS + " WHERE name=?",a);
        Cursor mCursor = mDb.query(true, DATA_TABLE_CITYS, null, "name = ?", new String[] { cityTitle }, null, null, null, null);

        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            WeatherData wdata = new WeatherData(mCursor);

            city.add(wdata);

            mCursor.close();
        }

        return city;
    }

    public void updateCity(String name,Boolean flag){
        final String mName = name;
        final Boolean mFlag = flag;

        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        APIservice apiService = APIservice.retrofit.create(APIservice.class);
        Observable<WeatherData> call = apiService.weatherQuery(name, "metric", "ru", mCtx.getString(R.string.open_weather_maps_app_id));
        call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherData -> {
                    ContentValues values = new ContentValues();
                    WeatherData o = weatherData;
                    values.put("name", o.getName());
                    values.put("lastupdate", o.getDt());
                    values.put("weather_description", o.getWeather().get(0).getDescription());
                    values.put("weather_id", o.getWeather().get(0).getId());
                    values.put("main_pressure", o.getMain().getPressure());
                    values.put("main_temp", o.getMain().getTemp());
                    values.put("main_humidity", o.getMain().getHumidity());
                    values.put("wind_speed", o.getWind().getSpeed());
                    values.put("wind_deg", o.getWind().getDeg());
                    values.put("sys_sunrise", o.getSys().getSunrise());
                    values.put("sys_sunset", o.getSys().getSunset());
                    values.put("sys_country", o.getSys().getCountry());
                    values.put("weather_icon", o.getWeather().get(0).getIcon());
                    values.put("city_id", o.getWeather().get(0).getId());
                    mDb.update(DATA_TABLE_CITYS, values, "name = ?",new String[] { mName });
                    if(mFlag) {
                        Intent in = new Intent("SWAP");
                        mCtx.sendBroadcast(in);
                    } else {
                        Intent in = new Intent("UPDATE");
                        mCtx.sendBroadcast(in);
                    }
                });

    }

    public void updateCitys(){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        ArrayList<WeatherData> Citys;

        Citys = getCitys();
        for(int i=0;i<Citys.size();i++) {
            if(i == (Citys.size()-1)) {
                updateCity(Citys.get(i).getName(), true);
            }else{
                updateCity(Citys.get(i).getName(), false);
            }

        }

    }

    public void setCity(String name){

        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        if(getCity(name).size() == 0){
            APIservice apiService = APIservice.retrofit.create(APIservice.class);
            Observable<WeatherData> call = apiService.weatherQuery(name,"metric","ru", mCtx.getString(R.string.open_weather_maps_app_id));
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(weatherData -> {
                        ContentValues values = new ContentValues();

                        WeatherData o = weatherData;
                        values.put("name", o.getName());

                        values.put("lastupdate", o.getDt());
                        values.put("weather_description", o.getWeather().get(0).getDescription());
                        values.put("weather_id", o.getWeather().get(0).getId());
                        values.put("main_pressure", o.getMain().getPressure());
                        values.put("main_temp", o.getMain().getTemp());
                        values.put("main_humidity", o.getMain().getHumidity());
                        values.put("wind_speed", o.getWind().getSpeed());
                        values.put("wind_deg", o.getWind().getDeg());
                        values.put("sys_sunrise", o.getSys().getSunrise());
                        values.put("sys_sunset", o.getSys().getSunset());
                        values.put("sys_country", o.getSys().getCountry());
                        values.put("weather_icon", o.getWeather().get(0).getIcon());
                        values.put("city_id", o.getWeather().get(0).getId());
                        mDb.insert(DATA_TABLE_CITYS, null, values);

                        Intent in = new Intent("SWAP");
                        mCtx.sendBroadcast(in);
                    });
        } else {
            Intent in = new Intent("INUSE");
            mCtx.sendBroadcast(in);
        }

    }

    public void deleteCity(String name){

        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        int clearCount = mDb.delete(DATA_TABLE_CITYS, "name = '" + name +"'", null);
        Intent in = new Intent("SWAP");
        mCtx.sendBroadcast(in);
    }

    public int deleteCitys(){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        int clearCount = mDb.delete(DATA_TABLE_CITYS, null, null);
        Intent in = new Intent("SWAP");
        mCtx.sendBroadcast(in);
        return clearCount;
    }


}
