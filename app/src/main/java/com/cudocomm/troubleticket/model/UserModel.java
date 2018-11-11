package com.cudocomm.troubleticket.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adsxg on 3/24/2017.
 */

public class UserModel implements Serializable {

    @SerializedName("user_id")
    private int userId;
    @SerializedName("id_updrs")
    private int id_updrs;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("user_email")
    private String userEmail;
    @SerializedName("position_id")
    private int positionId;
    @SerializedName("position_name")
    private String positionName;
    @SerializedName("station_id")
    private int stationId;
    @SerializedName("station_name")
    private String stationName;
    @SerializedName("region_id")
    private int regionId;
    @SerializedName("region_name")
    private String regionName;
    @SerializedName("branch_name")
    private String branchName;
    @SerializedName("user_picture")
    private String userPicture;
    @SerializedName("department_id")
    private int departmentId;
    @SerializedName("department_name")
    private String departmentName;

    public UserModel() {

    }

    public UserModel(int userId, int id_updrs, String userName, String userEmail) {
        this.userId = userId;
        this.id_updrs = id_updrs;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public UserModel(int userId, int id_updrs, String userName, String userEmail, String positionName, String branchName) {
        this.userId = userId;
        this.id_updrs = id_updrs;
        this.userName = userName;
        this.userEmail = userEmail;
        this.positionName = positionName;
        this.branchName = branchName;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public int getId_updrs() {
        return id_updrs;
    }

    public void setId_updrs(int id_updrs) {
        this.id_updrs = id_updrs;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
