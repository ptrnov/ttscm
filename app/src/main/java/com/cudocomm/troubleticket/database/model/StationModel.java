package com.cudocomm.troubleticket.database.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by adsxg on 4/18/2017.
 */

public class StationModel implements Serializable {

    public static final String ID = "id";
    public static final String STATION_ID = "station_id";
    public static final String STATION_NAME = "station_name";
    public static final String DEPARTMENT_ID = "department_id";
    public static final String REGION_ID = "region_id";
    public static final String STATION_LAT = "station_lat";
    public static final String STATION_LONG = "station_long";
    public static final String AC_NIELSEN = "ac_nielsen";
    public static final String STATION_BY_PASS = "station_bypass";

    @DatabaseField(columnName=ID, generatedId = true)
    private Integer id;
    @SerializedName(STATION_ID)
    @DatabaseField(columnName=STATION_ID, unique = true)
    private Integer stationId;
    @SerializedName(STATION_NAME)
    @DatabaseField(columnName=STATION_NAME)
    private String stationName;
    @SerializedName(DEPARTMENT_ID)
    @DatabaseField(columnName=DEPARTMENT_ID)
    private Integer departmentId;
    @SerializedName(REGION_ID)
    @DatabaseField(columnName=REGION_ID)
    private String regionId;
    @SerializedName(STATION_LAT)
    @DatabaseField(columnName=STATION_LAT)
    private String stationLat;
    @SerializedName(STATION_LONG)
    @DatabaseField(columnName=STATION_LONG)
    private String stationLong;
    @SerializedName(AC_NIELSEN)
    @DatabaseField(columnName=AC_NIELSEN)
    private Integer acNielsen;
    @SerializedName(STATION_BY_PASS)
    @DatabaseField(columnName=STATION_BY_PASS)
    private Integer stationByPass;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getStationLat() {
        return stationLat;
    }

    public void setStationLat(String stationLat) {
        this.stationLat = stationLat;
    }

    public String getStationLong() {
        return stationLong;
    }

    public void setStationLong(String stationLong) {
        this.stationLong = stationLong;
    }

    public Integer getAcNielsen() {
        return acNielsen;
    }

    public void setAcNielsen(Integer acNielsen) {
        this.acNielsen = acNielsen;
    }

    public Integer getStationByPass() {
        return stationByPass;
    }

    public void setStationByPass(Integer stationByPass) {
        this.stationByPass = stationByPass;
    }
}
