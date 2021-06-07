package com.example.ticketverification.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ticket {
    @SerializedName("ticket_no")
    @Expose
    private String ticketNo;

    @SerializedName("message")
    @Expose
    private String message;

    public String getStatus_code() {
        return status_code;
    }

    @SerializedName("status_code")
    @Expose
    private String status_code;

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
