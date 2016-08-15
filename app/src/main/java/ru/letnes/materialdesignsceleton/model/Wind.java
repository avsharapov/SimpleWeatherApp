
package ru.letnes.materialdesignsceleton.model;

import android.database.Cursor;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("speed")
    @Expose
    private Double speed;
    @SerializedName("deg")
    @Expose
    private Integer deg;

    public Wind (Cursor mCursor) {
        this.speed = mCursor.getDouble(mCursor.getColumnIndex("wind_speed"));
        this.deg = mCursor.getInt(mCursor.getColumnIndex("wind_deg"));
    }
    /**
     *
     * @return
     *     The speed
     */
    public Double getSpeed() {
        return speed;
    }

    /**
     *
     * @param speed
     *     The speed
     */
    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    /**
     *
     * @return
     *     The deg
     */
    public Integer getDeg() {
        return deg;
    }

    /**
     *
     * @param deg
     *     The deg
     */
    public void setDeg(Integer deg) {
        this.deg = deg;
    }

}
