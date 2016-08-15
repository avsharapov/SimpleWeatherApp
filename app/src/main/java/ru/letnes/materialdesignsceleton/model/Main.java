
package ru.letnes.materialdesignsceleton.model;

import android.database.Cursor;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Main {

    @SerializedName("temp")
    @Expose
    private Float temp;
    @SerializedName("pressure")
    @Expose
    private Integer pressure;
    @SerializedName("humidity")
    @Expose
    private Integer humidity;

    public Main (Cursor mCursor){
        this.temp = mCursor.getFloat(mCursor.getColumnIndex("main_temp"));
        this.pressure = mCursor.getInt(mCursor.getColumnIndex("main_pressure"));
        this.humidity = mCursor.getInt(mCursor.getColumnIndex("main_humidity"));
    }
    /**
     *
     * @return
     *     The temp
     */
    public Float getTemp() {
        return temp;
    }

    /**
     *
     * @param temp
     *     The temp
     */
    public void setTemp(Float temp) {
        this.temp = temp;
    }

    /**
     *
     * @return
     *     The pressure
     */
    public Integer getPressure() {
        return pressure;
    }

    /**
     *
     * @param pressure
     *     The pressure
     */
    public void setPressure(Integer pressure) {
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


}
