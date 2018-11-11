package com.cudocomm.troubleticket.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ISuspect implements Serializable {

    @SerializedName("suspect_id")
    private String suspectId;
    @SerializedName("suspect_name")
    private String suspectName;
    @SerializedName("parent_id")
    private String parentId;

    public String getSuspectId() {
        return suspectId;
    }

    public void setSuspectId(String suspectId) {
        this.suspectId = suspectId;
    }

    public String getSuspectName() {
        return suspectName;
    }

    public void setSuspectName(String suspectName) {
        this.suspectName = suspectName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
