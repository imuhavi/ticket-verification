package com.example.ticketverification.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("ticket_no")
    @Expose
    private String ticket_no;


    @SerializedName("status_code")
    @Expose
    private int statusCode;
    @SerializedName("ticket")
    @Expose
    private String ticket;

    @SerializedName("message")
    @Expose
    private String message;


    public Data(int statusCode, String ticket, String message) {
        this.statusCode = statusCode;
        this.ticket = ticket;
        this.message = message;
    }

    public void setTicket_no(String ticket_no) {
        this.ticket_no = ticket_no;
    }

    public String getTicket_no() {
        return ticket_no;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "{" +
                "statusCode=" + statusCode +
                ", ticket='" + ticket + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public void setStatusCode(int    statusCode) {
        this.statusCode = statusCode;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}