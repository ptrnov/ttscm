package com.cudocomm.troubleticket.model;

import com.cudocomm.troubleticket.database.model.JabatanModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by adsxg on 3/27/2017.
 */

public class MultiJabatanModel {

    @SerializedName("list")
    private List<JabatanModel> jabatanModel;

    public List<JabatanModel> getJabatanModel() {
        return jabatanModel;
    }

    public void setJabatanModel(List<JabatanModel> jabatanModel) {
        this.jabatanModel = jabatanModel;
    }

}
