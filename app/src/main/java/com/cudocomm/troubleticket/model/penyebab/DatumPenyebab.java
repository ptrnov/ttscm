
package com.cudocomm.troubleticket.model.penyebab;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DatumPenyebab implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("penyebab")
    @Expose
    private String penyebab;
    @SerializedName("desc")
    @Expose
    private Object desc;
    private final static long serialVersionUID = -7773453227977270909L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DatumPenyebab() {
    }

    /**
     * 
     * @param id
     * @param penyebab
     * @param desc
     */
    public DatumPenyebab(String id, String penyebab, Object desc) {
        super();
        this.id = id;
        this.penyebab = penyebab;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPenyebab() {
        return penyebab;
    }

    public void setPenyebab(String penyebab) {
        this.penyebab = penyebab;
    }

    public Object getDesc() {
        return desc;
    }

    public void setDesc(Object desc) {
        this.desc = desc;
    }

}
