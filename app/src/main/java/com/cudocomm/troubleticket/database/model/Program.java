package com.cudocomm.troubleticket.database.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Program implements Serializable {

    public static final String PROGRAM_IDX = "program_index";
    public static final String PROGRAM_ID = "program_id";
    public static final String PROGRAM_NAME = "program_name";
    public static final String PROGRAM_DESC = "program_desc";

    @DatabaseField(columnName=PROGRAM_IDX, generatedId = true)
    private Integer id;
    @DatabaseField(columnName=PROGRAM_ID, unique = true)
    @SerializedName(PROGRAM_ID)
    private Integer programId;
    @DatabaseField(columnName=PROGRAM_NAME)
    @SerializedName(PROGRAM_NAME)
    private String programName;
    @DatabaseField(columnName=PROGRAM_DESC)
    @SerializedName(PROGRAM_DESC)
    private String programDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
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

    @Override
    public String toString() {
        return programName;
    }
}

