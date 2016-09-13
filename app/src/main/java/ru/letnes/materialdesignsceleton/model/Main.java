
package ru.letnes.materialdesignsceleton.model;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Main {

    @SerializedName("temp")
    @Expose
    private Double temp;
    @SerializedName("pressure")
    @Expose
    private String pressure;
    @SerializedName("humidity")
    @Expose
    private Integer humidity;
    @SerializedName("temp_min")
    @Expose
    private Double tempMin;
    @SerializedName("temp_max")
    @Expose
    private Double tempMax;
    public Main (Cursor mCursor){
        this.temp = mCursor.getDouble(mCursor.getColumnIndex("main_temp"));
        this.pressure = mCursor.getString(mCursor.getColumnIndex("main_pressure"));
        this.humidity = mCursor.getInt(mCursor.getColumnIndex("main_humidity"));
    }
    /**
     *
     * @return
     *     The temp
     */
    public Double getTemp() {
        return temp;
    }

    /**
     *
     * @param temp
     *     The temp
     */
    public void setTemp(Double temp) {
        this.temp = temp;
    }

    /**
     *
     * @return
     *     The pressure
     */
    public String getPressure() {
        return pressure;
    }

    /**
     *
     * @param pressure
     *     The pressure
     */
    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    /**
     *
     * @return
     *     The humidity
     */
    public Integer getHumidity() {
        return humidity;
    }

    /**
     *
     * @param humidity
     *     The humidity
     */
    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }
    /**
     *
     * @return
     *     The tempMin
     */
    public Double getTempMin() {
        return tempMin;
    }

    /**
     *
     * @param tempMin
     *     The temp_min
     */
    public void setTempMin(Double tempMin) {
        this.tempMin = tempMin;
    }

    /**
     *
     * @return
     *     The tempMax
     */
    public Double getTempMax() {
        return tempMax;
    }

    /**
     *
     * @param tempMax
     *     The temp_max
     */
    public void setTempMax(Double tempMax) {
        this.tempMax = tempMax;
    }

}
