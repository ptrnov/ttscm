
package com.cudocomm.troubleticket.model.program;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ProgramNew implements Serializable
{

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<Datum> data = new ArrayList<>();
    private final static long serialVersionUID = -5144255452067958297L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ProgramNew() {
    }

    /**
     * 
     * @param status
     * @param data
     */
    public ProgramNew(String status, ArrayList<Datum> data) {
        super();
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

}
