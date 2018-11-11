package com.cudocomm.troubleticket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adsxg on 5/6/2017.
 */

public class Ticket2 implements Serializable {

    @SerializedName("ticket_id")
    @Expose
    private String ticketId;
    @SerializedName("ticket_date")
    @Expose
    private String ticketDate;
    @SerializedName("ticket_type")
    @Expose
    private String ticketType;
    @SerializedName("ticket_station_id")
    @Expose
    private String ticketStationId;
    @SerializedName("station_name")
    @Expose
    private String stationName;
    @SerializedName("ticket_remarks")
    @Expose
    private String ticketRemarks;
    @SerializedName("ticket_severity")
    @Expose
    private String ticketSeverity;
    @SerializedName("ticket_status")
    @Expose
    private String ticketStatus;
    @SerializedName("ticket_creator_id")
    @Expose
    private String ticketCreatorId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("ticket_photo_1")
    @Expose
    private String ticketPhoto1;
    @SerializedName("ticket_photo_2")
    @Expose
    private String ticketPhoto2;
    @SerializedName("ticket_photo_3")
    @Expose
    private String ticketPhoto3;
    @SerializedName("ticket_no")
    @Expose
    private String ticketNo;
    @SerializedName("isuspect1_id")
    @Expose
    private String isuspect1Id;
    @SerializedName("isuspect1_name")
    @Expose
    private String isuspect1Name;
    @SerializedName("isuspect2_id")
    @Expose
    private String isuspect2Id;
    @SerializedName("isuspect2_name")
    @Expose
    private String isuspect2Name;
    @SerializedName("isuspect3_id")
    @Expose
    private String isuspect3Id;
    @SerializedName("isuspect3_name")
    @Expose
    private String isuspect3Name;

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(String ticketDate) {
        this.ticketDate = ticketDate;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketStationId() {
        return ticketStationId;
    }

    public void setTicketStationId(String ticketStationId) {
        this.ticketStationId = ticketStationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getTicketRemarks() {
        return ticketRemarks;
    }

    public void setTicketRemarks(String ticketRemarks) {
        this.ticketRemarks = ticketRemarks;
    }

    public String getTicketSeverity() {
        return ticketSeverity;
    }

    public void setTicketSeverity(String ticketSeverity) {
        this.ticketSeverity = ticketSeverity;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getTicketCreatorId() {
        return ticketCreatorId;
    }

    public void setTicketCreatorId(String ticketCreatorId) {
        this.ticketCreatorId = ticketCreatorId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTicketPhoto1() {
        return ticketPhoto1;
    }

    public void setTicketPhoto1(String ticketPhoto1) {
        this.ticketPhoto1 = ticketPhoto1;
    }

    public String getTicketPhoto2() {
        return ticketPhoto2;
    }

    public void setTicketPhoto2(String ticketPhoto2) {
        this.ticketPhoto2 = ticketPhoto2;
    }

    public String getTicketPhoto3() {
        return ticketPhoto3;
    }

    public void setTicketPhoto3(String ticketPhoto3) {
        this.ticketPhoto3 = ticketPhoto3;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getIsuspect1Id() {
        return isuspect1Id;
    }

    public void setIsuspect1Id(String isuspect1Id) {
        this.isuspect1Id = isuspect1Id;
    }

    public String getIsuspect1Name() {
        return isuspect1Name;
    }

    public void setIsuspect1Name(String isuspect1Name) {
        this.isuspect1Name = isuspect1Name;
    }

    public String getIsuspect2Id() {
        return isuspect2Id;
    }

    public void setIsuspect2Id(String isuspect2Id) {
        this.isuspect2Id = isuspect2Id;
    }

    public String getIsuspect2Name() {
        return isuspect2Name;
    }

    public void setIsuspect2Name(String isuspect2Name) {
        this.isuspect2Name = isuspect2Name;
    }

    public String getIsuspect3Id() {
        return isuspect3Id;
    }

    public void setIsuspect3Id(String isuspect3Id) {
        this.isuspect3Id = isuspect3Id;
    }

    public String getIsuspect3Name() {
        return isuspect3Name;
    }

    public void setIsuspect3Name(String isuspect3Name) {
        this.isuspect3Name = isuspect3Name;
    }

}
