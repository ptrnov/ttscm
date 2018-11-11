package com.cudocomm.troubleticket.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adsxg on 3/28/2017.
 */

public class Escalation implements Serializable {

    @SerializedName("escalation_id")
    private String escalationId;
    @SerializedName("escalation_datetime")
    private String escalationDate;
    @SerializedName("escalation_info")
    private String escalationInfo;
    @SerializedName("escalation_fromuserid")
    private String escalationFromUserId;
    @SerializedName("escalation_fromusername")
    private String escalationFromUserName;
    @SerializedName("escalation_action")
    private String escalationAction;
    @SerializedName("escalation_require")
    private int escalationRequire;
    @SerializedName("ticket")
    private Ticket ticket;

    public String getEscalationId() {
        return escalationId;
    }

    public void setEscalationId(String escalationId) {
        this.escalationId = escalationId;
    }

    public String getEscalationDate() {
        return escalationDate;
    }

    public void setEscalationDate(String escalationDate) {
        this.escalationDate = escalationDate;
    }

    public String getEscalationInfo() {
        return escalationInfo;
    }

    public void setEscalationInfo(String escalationInfo) {
        this.escalationInfo = escalationInfo;
    }

    public String getEscalationFromUserId() {
        return escalationFromUserId;
    }

    public void setEscalationFromUserId(String escalationFromUserId) {
        this.escalationFromUserId = escalationFromUserId;
    }

    public String getEscalationFromUserName() {
        return escalationFromUserName;
    }

    public void setEscalationFromUserName(String escalationFromUserName) {
        this.escalationFromUserName = escalationFromUserName;
    }

    public String getEscalationAction() {
        return escalationAction;
    }

    public void setEscalationAction(String escalationAction) {
        this.escalationAction = escalationAction;
    }

    public int getEscalationRequire() {
        return escalationRequire;
    }

    public void setEscalationRequire(int escalationRequire) {
        this.escalationRequire = escalationRequire;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
