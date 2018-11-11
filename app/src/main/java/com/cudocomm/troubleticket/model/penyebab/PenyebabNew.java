
package com.cudocomm.troubleticket.model.penyebab;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PenyebabNew implements Serializable
{

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<DatumPenyebab> data = new ArrayList<>();
    private final static long serialVersionUID = -4349537858271564964L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PenyebabNew() {
    }

    /**
     * 
     * @param status
     * @param data
     */
    public PenyebabNew(String status, ArrayList<DatumPenyebab> data) {
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

    public ArrayList<DatumPenyebab> getData() {
        return data;
    }

    public void setData(ArrayList<DatumPenyebab> data) {
        this.data = data;
    }

}
