package com.cudocomm.troubleticket.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adsxg on 6/22/2017.
 */

public class TopTenSuspect implements Serializable {

    @SerializedName("isuspect1_id")
    private Integer suspectId;
    @SerializedName("isuspect1_name")
    private String suspectName;
    @SerializedName("total")
    private String total;

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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
