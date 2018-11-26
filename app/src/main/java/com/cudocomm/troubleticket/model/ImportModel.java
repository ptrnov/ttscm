package com.cudocomm.troubleticket.model;

import com.cudocomm.troubleticket.database.model.SeverityModel;
import com.cudocomm.troubleticket.database.model.SeverityUpdateModel;
import com.cudocomm.troubleticket.database.model.StationModel;
import com.cudocomm.troubleticket.database.model.Suspect1Model;
import com.cudocomm.troubleticket.database.model.Suspect2Model;
import com.cudocomm.troubleticket.database.model.Suspect3Model;
import com.cudocomm.troubleticket.database.model.Suspect4Model;
import com.cudocomm.troubleticket.database.model.UserLoginModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by adsxg on 3/27/2017.
 */

public class ImportModel {

    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    @SerializedName("userlogin")
    private List<UserLoginModel> userModel;
    /*@SerializedName("initialsuspects")
    private List<InitialSuspect> initialSuspects;
    @SerializedName("stations")
    private List<Station> stations;
    @SerializedName("ticketsummaries")
    private List<TicketStatusSumm> ticketStatusSumms;
    @SerializedName("ticketseveritysummaries")
    private List<TicketSeveritySumm> ticketSeveritySumms;
    @SerializedName("ticketseveritytypesummaries")
    private List<TicketSeverityTypeSumm> ticketSeverityTypeSumms;*/
    @SerializedName("downtimesuspects1")
    private List<ISuspect> downTimeSuspects1;
    @SerializedName("kerusakansuspects1")
    private List<ISuspect> kerusakanSuspects1;
    @SerializedName("severitytime")
    private List<SeverityTime> severityTimes;
    @SerializedName("severities")
    private List<Severity> severities;

    @SerializedName("totalticket")
    private CounterModel totalTicket;

    @SerializedName("needapproval")
    private CounterModel needApproval;

    @SerializedName("mytaskcounter")
    private CounterModel myTaskCounter;

    @SerializedName("severitytime2")
    private List<SeverityModel> severityModels;

    @SerializedName("suspect_1")
    private List<Suspect1Model> suspect1Models;
    @SerializedName("suspect_2")
    private List<Suspect2Model> suspect2Models;
    @SerializedName("suspect_3")
    private List<Suspect3Model> suspect3Models;
    @SerializedName("suspect_4")
    private List<Suspect4Model> suspect4Models;
//    @SerializedName("userlogin2")
//    private UserLoginModel userLoginModel;
    @SerializedName("stations")
    private List<StationModel> stations;

    public List<Severity> getSeverities() {
        return severities;
    }

    public void setSeverities(List<Severity> severities) {
        this.severities = severities;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<UserLoginModel> getUserModel() {
        return userModel;
    }

    public void setUserModel(List<UserLoginModel> userModel) {
        this.userModel = userModel;
    }

    /*public List<InitialSuspect> getInitialSuspects() {
        return initialSuspects;
    }

    public void setInitialSuspects(List<InitialSuspect> initialSuspects) {
        this.initialSuspects = initialSuspects;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public List<TicketStatusSumm> getTicketStatusSumms() {
        return ticketStatusSumms;
    }

    public void setTicketStatusSumms(List<TicketStatusSumm> ticketStatusSumms) {
        this.ticketStatusSumms = ticketStatusSumms;
    }

    public List<TicketSeveritySumm> getTicketSeveritySumms() {
        return ticketSeveritySumms;
    }

    public void setTicketSeveritySumms(List<TicketSeveritySumm> ticketSeveritySumms) {
        this.ticketSeveritySumms = ticketSeveritySumms;
    }

    public List<TicketSeverityTypeSumm> getTicketSeverityTypeSumms() {
        return ticketSeverityTypeSumms;
    }

    public void setTicketSeverityTypeSumms(List<TicketSeverityTypeSumm> ticketSeverityTypeSumms) {
        this.ticketSeverityTypeSumms = ticketSeverityTypeSumms;
    }
*/

    public List<ISuspect> getDownTimeSuspects1() {
        return downTimeSuspects1;
    }

    public void setDownTimeSuspects1(List<ISuspect> downTimeSuspects1) {
        this.downTimeSuspects1 = downTimeSuspects1;
    }

    public List<SeverityTime> getSeverityTimes() {
        return severityTimes;
    }

    public void setSeverityTimes(List<SeverityTime> severityTimes) {
        this.severityTimes = severityTimes;
    }

    public List<ISuspect> getKerusakanSuspects1() {
        return kerusakanSuspects1;
    }

    public void setKerusakanSuspects1(List<ISuspect> kerusakanSuspects1) {
        this.kerusakanSuspects1 = kerusakanSuspects1;
    }

    public CounterModel getNeedApproval() {
        return needApproval;
    }

    public void setNeedApproval(CounterModel needApproval) {
        this.needApproval = needApproval;
    }

    public CounterModel getMyTaskCounter() {
        return myTaskCounter;
    }

    public void setMyTaskCounter(CounterModel myTaskCounter) {
        this.myTaskCounter = myTaskCounter;
    }

    public CounterModel getTotalTicket() {
        return totalTicket;
    }

    public void setTotalTicket(CounterModel totalTicket) {
        this.totalTicket = totalTicket;
    }

    public List<SeverityModel> getSeverityModels() {
        return severityModels;
    }

    public void setSeverityModels(List<SeverityModel> severityModels) {
        this.severityModels = severityModels;
    }

    public List<Suspect1Model> getSuspect1Models() {
        return suspect1Models;
    }

    public void setSuspect1Models(List<Suspect1Model> suspect1Models) {
        this.suspect1Models = suspect1Models;
    }

    public List<Suspect2Model> getSuspect2Models() {
        return suspect2Models;
    }

    public void setSuspect2Models(List<Suspect2Model> suspect2Models) {
        this.suspect2Models = suspect2Models;
    }

    public List<Suspect3Model> getSuspect3Models() {
        return suspect3Models;
    }

    public void setSuspect3Models(List<Suspect3Model> suspect3Models) {
        this.suspect3Models = suspect3Models;
    }

    public List<Suspect4Model> getSuspect4Models() {
        return suspect4Models;
    }

    public void setSuspect4Models(List<Suspect4Model> suspect4Models) {
        this.suspect4Models = suspect4Models;
    }

//    public UserLoginModel getUserLoginModel() {
//        return userLoginModel;
//    }
//
//    public void setUserLoginModel(UserLoginModel userLoginModel) {
//        this.userLoginModel = userLoginModel;
//    }

    public List<StationModel> getStations() {
        return stations;
    }

    public void setStations(List<StationModel> stations) {
        this.stations = stations;
    }
}
