package com.cudocomm.troubleticket.database.model;

import com.cudocomm.troubleticket.model.MultiJabatanModel;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by adsxg on 3/24/2017.
 */

public class JabatanModel extends MultiJabatanModel implements Serializable {

    public static final String ID = "user_id";
    public static final String NAME = "user_name";
    public static final String EMAIL = "user_email";
    public static final String POSITION_ID = "position_id";
    public static final String POSITION_NAME = "position_name";
    public static final String STATION_ID = "station_id";
    public static final String STATION_NAME = "station_name";
    public static final String REGION_ID = "region_id";
    public static final String REGION_NAME = "region_name";
    public static final String DEPARTMENT_ID = "department_id";
    public static final String DEPARTMENT_NAME = "department_name";
    public static final String PICTURE = "user_picture";
    public static final String FCM_REG_ID = "fcm_registered_id";
    public static final String ID_UPDRS = "id_updrs";

    @DatabaseField(columnName="id", generatedId = true)
    private Integer id;
    @DatabaseField(columnName=ID_UPDRS, unique = true)
    @SerializedName("id_updrs")
    private Integer idUpdrs;
    @DatabaseField(columnName=ID)
    @SerializedName("user_id")
    private Integer userId;
    @DatabaseField(columnName=NAME)
    @SerializedName("user_name")
    private String userName;
    @DatabaseField(columnName=EMAIL)
    @SerializedName("user_email")
    private String userEmail;
    @DatabaseField(columnName=POSITION_ID)
    @SerializedName("position_id")
    private Integer positionId;
    @DatabaseField(columnName=POSITION_NAME)
    @SerializedName("position_name")
    private String positionName;
    @DatabaseField(columnName=STATION_ID)
    @SerializedName("station_id")
    private String stationId;
    @DatabaseField(columnName=STATION_NAME)
    @SerializedName("station_name")
    private String stationName;
    @DatabaseField(columnName=REGION_ID)
    @SerializedName("region_id")
    private String regionId;
    @DatabaseField(columnName=REGION_NAME)
    @SerializedName("region_name")
    private String regionName;
    @DatabaseField(columnName=PICTURE)
    @SerializedName("user_picture")
    private String userPicture;
    @DatabaseField(columnName=DEPARTMENT_ID)
    @SerializedName("department_id")
    private String departmentId;
    @DatabaseField(columnName=DEPARTMENT_NAME)
    @SerializedName("department_name")
    private String departmentName;
    @DatabaseField(columnName=FCM_REG_ID)
    @SerializedName("fcm_registered_id")
    private String fcmRegisteredId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdUpdrs() {
        return idUpdrs;
    }

    public void setIdUpdrs(Integer idUpdrs) {
        this.idUpdrs = idUpdrs;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getFcmRegisteredId() {
        return fcmRegisteredId;
    }

    public void setFcmRegisteredId(String fcmRegisteredId) {
        this.fcmRegisteredId = fcmRegisteredId;
    }
}
