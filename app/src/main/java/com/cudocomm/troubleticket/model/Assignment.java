package com.cudocomm.troubleticket.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adsxg on 3/28/2017.
 */

public class Assignment implements Serializable {

    @SerializedName("assignment_id")
    private String assignmentId;
    @SerializedName("assignment_datetime")
    private String assignmentDate;
    @SerializedName("assignment_action_id")
    private int assignmentActionId;
    @SerializedName("assignment_action_name")
    private String assignmentActionName;
    @SerializedName("assignmentInfo")
    private String assignmentInfo;
    @SerializedName("assignment_onsite")
    private int assignmentOnsite;
    @SerializedName("assignment_request_remarks")
    private String assignmentRequestRemarks;
    @SerializedName("ticket")
    private Ticket ticket;
    /*@SerializedName("ticket_id")
    private String ticketId;
    @SerializedName("ticket_date")
    private String ticketDate;
    @SerializedName("ticket_type")
    private int ticketType;
    @SerializedName("ticket_station_id")
    private String ticketStationId;
    @SerializedName("station_name")
    private String stationName;
    @SerializedName("ticket_suspect_id")
    private String ticketSuspectId;
    @SerializedName("suspect_name")
    private String suspectName;
    @SerializedName("ticket_remarks")
    private String ticketRemarks;
    @SerializedName("ticket_severity")
    private int ticketSeverity;
    @SerializedName("ticket_status")
    private int ticketStatus;
    @SerializedName("ticket_creator_id")
    private String ticketCreatorId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("ticket_photo_1")
    private String ticketPhoto1;
    @SerializedName("ticket_photo_2")
    private String ticketPhoto2;
    @SerializedName("ticket_photo_3")
    private String ticketPhoto3;
    @SerializedName("ticket_no")
    private String ticketNo;
    @SerializedName("closett_info")
    private String closeInfo;
    @SerializedName("isuspect1_id")
    private String suspect1Id;
    @SerializedName("isuspect1_name")
    private String suspect1Name;
    @SerializedName("isuspect2_id")
    private String suspect2Id;
    @SerializedName("isuspect2_name")
    private String suspect2Name;
    @SerializedName("isuspect3_id")
    private String suspect3Id;
    @SerializedName("isuspect3_name")
    private String suspect3Name;

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

    public int getTicketType() {
        return ticketType;
    }

    public void setTicketType(int ticketType) {
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

    public String getTicketSuspectId() {
        return ticketSuspectId;
    }

    public void setTicketSuspectId(String ticketSuspectId) {
        this.ticketSuspectId = ticketSuspectId;
    }

    public String getSuspectName() {
        return suspectName;
    }

    public void setSuspectName(String suspectName) {
        this.suspectName = suspectName;
    }

    public String getTicketRemarks() {
        return ticketRemarks;
    }

    public void setTicketRemarks(String ticketRemarks) {
        this.ticketRemarks = ticketRemarks;
    }

    public int getTicketSeverity() {
        return ticketSeverity;
    }

    public void setTicketSeverity(int ticketSeverity) {
        this.ticketSeverity = ticketSeverity;
    }

    public int getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(int ticketStatus) {
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

    public String getCloseInfo() {
        return closeInfo;
    }

    public void setCloseInfo(String closeInfo) {
        this.closeInfo = closeInfo;
    }

    public String getSuspect1Id() {
        return suspect1Id;
    }

    public void setSuspect1Id(String suspect1Id) {
        this.suspect1Id = suspect1Id;
    }

    public String getSuspect1Name() {
        return suspect1Name;
    }

    public void setSuspect1Name(String suspect1Name) {
        this.suspect1Name = suspect1Name;
    }

    public String getSuspect2Id() {
        return suspect2Id;
    }

    public void setSuspect2Id(String suspect2Id) {
        this.suspect2Id = suspect2Id;
    }

    public String getSuspect2Name() {
        return suspect2Name;
    }

    public void setSuspect2Name(String suspect2Name) {
        this.suspect2Name = suspect2Name;
    }

    public String getSuspect3Id() {
        return suspect3Id;
    }

    public void setSuspect3Id(String suspect3Id) {
        this.suspect3Id = suspect3Id;
    }

    public String getSuspect3Name() {
        return suspect3Name;
    }

    public void setSuspect3Name(String suspect3Name) {
        this.suspect3Name = suspect3Name;
    }
*/

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(String assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public int getAssignmentActionId() {
        return assignmentActionId;
    }

    public void setAssignmentActionId(int assignmentActionId) {
        this.assignmentActionId = assignmentActionId;
    }

    public String getAssignmentActionName() {
        return assignmentActionName;
    }

    public void setAssignmentActionName(String assignmentActionName) {
        this.assignmentActionName = assignmentActionName;
    }

    public String getAssignmentInfo() {
        return assignmentInfo;
    }

    public void setAssignmentInfo(String assignmentInfo) {
        this.assignmentInfo = assignmentInfo;
    }

    public int getAssignmentOnsite() {
        return assignmentOnsite;
    }

    public void setAssignmentOnsite(int assignmentOnsite) {
        this.assignmentOnsite = assignmentOnsite;
    }

    public String getAssignmentRequestRemarks() {
        return assignmentRequestRemarks;
    }

    public void setAssignmentRequestRemarks(String assignmentRequestRemarks) {
        this.assignmentRequestRemarks = assignmentRequestRemarks;
    }
}
