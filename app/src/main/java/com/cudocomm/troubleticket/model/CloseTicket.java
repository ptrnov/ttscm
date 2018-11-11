package com.cudocomm.troubleticket.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adsxg on 4/28/2017.
 */

public class CloseTicket implements Serializable {

    @SerializedName("closett_id")
    private String ticketId;
    @SerializedName("closett_datetime")
    private String ticketDate;
    @SerializedName("closett_info")
    private String ticketInfo;
    @SerializedName("closett_userid")
    private String closeById;
    @SerializedName("closed_by")
    private String closeByName;
    @SerializedName("ticket")
    private Ticket ticket;

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

    public String getTicketInfo() {
        return ticketInfo;
    }

    public void setTicketInfo(String ticketInfo) {
        this.ticketInfo = ticketInfo;
    }

    public String getCloseById() {
        return closeById;
    }

    public void setCloseById(String closeById) {
        this.closeById = closeById;
    }

    public String getCloseByName() {
        return closeByName;
    }

    public void setCloseByName(String closeByName) {
        this.closeByName = closeByName;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
