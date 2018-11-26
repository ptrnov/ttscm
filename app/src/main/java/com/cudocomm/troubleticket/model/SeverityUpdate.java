package com.cudocomm.troubleticket.model;

import com.cudocomm.troubleticket.database.model.SeverityUpdateModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by adsxg on 3/27/2017.
 */

public class SeverityUpdate {

    @SerializedName("severities")
    private List<SeverityUpdateModel> severityUpdateModels;

    public List<SeverityUpdateModel> getSeverityUpdateModels() {
        return severityUpdateModels;
    }

    public void setSeverityUpdateModels(List<SeverityUpdateModel> severityUpdateModels) {
        this.severityUpdateModels = severityUpdateModels;
    }
}
