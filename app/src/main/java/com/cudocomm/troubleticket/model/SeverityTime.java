package com.cudocomm.troubleticket.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adsxg on 4/18/2017.
 */

public class SeverityTime implements Serializable {

    @SerializedName("severity_name")
    private String severityName;
    @SerializedName("severity_time")
    private String severityTime;

    public String getSeverityName() {
        return severityName;
    }

    public void setSeverityName(String severityName) {
        this.severityName = severityName;
    }

    public String getSeverityTime() {
        return severityTime;
    }

    public void setSeverityTime(String severityTime) {
        this.severityTime = severityTime;
    }
}
