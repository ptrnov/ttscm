
package com.cudocomm.troubleticket.model.program;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Datum implements Serializable
{

    @SerializedName("program_id")
    @Expose
    private String programId;
    @SerializedName("program_name")
    @Expose
    private String programName;
    @SerializedName("program_desc")
    @Expose
    private String programDesc;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("tv_station")
    @Expose
    private String tvStation;
    private final static long serialVersionUID = 6071702984870546101L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Datum() {
    }

    /**
     * 
     * @param programId
     * @param tvStation
     * @param programDesc
     * @param active
     * @param programName
     */
    public Datum(String programId, String programName, String programDesc, String active, String tvStation) {
        super();
        this.programId = programId;
        this.programName = programName;
        this.programDesc = programDesc;
        this.active = active;
        this.tvStation = tvStation;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramDesc() {
        return programDesc;
    }

    public void setProgramDesc(String programDesc) {
        this.programDesc = programDesc;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTvStation() {
        return tvStation;
    }

    public void setTvStation(String tvStation) {
        this.tvStation = tvStation;
    }

}
