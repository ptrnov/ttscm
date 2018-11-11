package com.cudocomm.troubleticket.database.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by adsxg on 6/18/2017.
 */

public class SeverityModel implements Serializable {

    public static final String ID = "id";
    public static final String SEVERITY_ID = "severity_id";
    public static final String NAME = "severity_name";
    public static final String TIME = "severity_time";

    @DatabaseField(columnName=ID, generatedId = true)
    private Integer id;
    @SerializedName(SEVERITY_ID)
    @DatabaseField(columnName=SEVERITY_ID, unique = true)
    private Integer severityId;
    @DatabaseField(columnName=NAME)
    @SerializedName(NAME)
    private String severityName;
    @DatabaseField(columnName=TIME)
    @SerializedName(TIME)
    private String severityTime;

    public Integer getSeverityId() {
        return severityId;
    }

    public void setSeverityId(Integer severityId) {
        this.severityId = severityId;
    }

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
