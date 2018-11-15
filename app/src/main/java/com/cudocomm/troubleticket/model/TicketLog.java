package com.cudocomm.troubleticket.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adsxg on 4/5/2017.
 */

public class TicketLog implements Serializable {

    @SerializedName("title")
    private String title;
    @SerializedName("desc")
    private String desc;
    @SerializedName("date")
    private String date;
    @SerializedName("user")
    private String user;
    @SerializedName("log_status")
    private String log_status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLog_status() {
        return log_status;
    }

    public void setLog_status(String log_status) {
        this.user = log_status;
    }
}
