package com.cudocomm.troubleticket.model;

import com.cudocomm.troubleticket.database.model.SeverityModel;
import com.cudocomm.troubleticket.database.model.StationModel;
import com.cudocomm.troubleticket.database.model.Suspect1Model;
import com.cudocomm.troubleticket.database.model.Suspect2Model;
import com.cudocomm.troubleticket.database.model.Suspect3Model;
import com.cudocomm.troubleticket.database.model.Suspect4Model;
import com.cudocomm.troubleticket.database.model.UserLoginModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by adsxg on 3/27/2017.
 */

public class MasterModel {

    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    @SerializedName("severitytime2")
    private List<SeverityModel> severityModels;

    @SerializedName("suspect_1")
    private List<Suspect1Model> suspect1Models;
    @SerializedName("suspect_2")
    private List<Suspect2Model> suspect2Models;
    @SerializedName("suspect_3")
    private List<Suspect3Model> suspect3Models;
    @SerializedName("suspect_4")
    private List<Suspect4Model> suspect4Models;
    @SerializedName("userlogin2")
    private UserLoginModel userLoginModel;

    @SerializedName("stations")
    private List<StationModel> stations;

    public String getStatus() {
        return status;
    }

    public String getUserId() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SeverityModel> getSeverityModels() {
        return severityModels;
    }

    public void setSeverityModels(List<SeverityModel> severityModels) {
        this.severityModels = severityModels;
    }

    public List<Suspect1Model> getSuspect1Models() {
        return suspect1Models;
    }

    public void setSuspect1Models(List<Suspect1Model> suspect1Models) {
        this.suspect1Models = suspect1Models;
    }

    public List<Suspect2Model> getSuspect2Models() {
        return suspect2Models;
    }

    public void setSuspect2Models(List<Suspect2Model> suspect2Models) {
        this.suspect2Models = suspect2Models;
    }

    public List<Suspect3Model> getSuspect3Models() {
        return suspect3Models;
    }

    public void setSuspect3Models(List<Suspect3Model> suspect3Models) {
        this.suspect3Models = suspect3Models;
    }

    public List<Suspect4Model> getSuspect4Models() {
        return suspect4Models;
    }

    public void setSuspect4Models(List<Suspect4Model> suspect4Models) {
        this.suspect4Models = suspect4Models;
    }

    public UserLoginModel getUserLoginModel() {
        return userLoginModel;
    }

    public void setUserLoginModel(UserLoginModel userLoginModel) {
        this.userLoginModel = userLoginModel;
    }

    public List<StationModel> getStations() {
        return stations;
    }

    public void setStations(List<StationModel> stations) {
        this.stations = stations;
    }
}
