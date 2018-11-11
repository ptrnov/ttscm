package com.cudocomm.troubleticket.database.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by adsxg on 6/18/2017.
 */

public class Suspect1Model implements Serializable {

    public static final String ID = "id";
    public static final String SUSPECT_ID = "suspect_id";
    public static final String NAME = "suspect_name";
    public static final String MODULE = "suspect_module";

    @DatabaseField(columnName=ID, generatedId = true)
    private Integer id;
    @SerializedName(SUSPECT_ID)
    @DatabaseField(columnName=SUSPECT_ID, unique = true)
    private Integer suspectId;
    @DatabaseField(columnName=NAME)
    @SerializedName(NAME)
    private String suspectName;
    @DatabaseField(columnName=MODULE)
    @SerializedName(MODULE)
    private Integer moduleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSuspectId() {
        return suspectId;
    }

    public void setSuspectId(Integer suspectId) {
        this.suspectId = suspectId;
    }

    public String getSuspectName() {
        return suspectName;
    }

    public void setSuspectName(String suspectName) {
        this.suspectName = suspectName;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }
}
