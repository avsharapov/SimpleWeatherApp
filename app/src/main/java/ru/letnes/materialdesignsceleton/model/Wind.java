
package ru.letnes.materialdesignsceleton.model;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("speed")
    @Expose
    private Double speed;
    @SerializedName("deg")
    @Expose
    private String deg;
    @SerializedName("var_beg")
    @Expose
    private Integer varBeg;
    @SerializedName("var_end")
    @Expose
    private Integer varEnd;
    public Wind (Cursor mCursor) {
        this.speed = mCursor.getDouble(mCursor.getColumnIndex("wind_speed"));
        this.deg = mCursor.getString(mCursor.getColumnIndex("wind_deg"));
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
    public String getDeg() {
        return deg;
    }

    /**
     *
     * @param deg
     *     The deg
     */
    public void setDeg(String deg) {
        this.deg = deg;
    }
    /**
     *
     * @return
     *     The varBeg
     */
    public Integer getVarBeg() {
        return varBeg;
    }

    /**
     *
     * @param varBeg
     *     The var_beg
     */
    public void setVarBeg(Integer varBeg) {
        this.varBeg = varBeg;
    }

    /**
     *
     * @return
     *     The varEnd
     */
    public Integer getVarEnd() {
        return varEnd;
    }

    /**
     *
     * @param varEnd
     *     The var_end
     */
    public void setVarEnd(Integer varEnd) {
        this.varEnd = varEnd;
    }
}
