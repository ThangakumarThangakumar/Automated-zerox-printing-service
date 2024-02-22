package com.example.sreconlinexeroxautomation;
public class UserList {

    private String facname,leaveType,leavereason,startdate,enddate,status;

    public UserList() {

    }

    public UserList(String facname, String leavereason, String startdate, String enddate, String status) {
        this.facname = facname;
        this.leavereason = leavereason;
        this.startdate = startdate;
        this.enddate = enddate;
        this.status = status;
    }

    public String getFacname() {
        return facname;
    }

    public void setFacname(String facname) {
        this.facname = facname;
    }

    public String getLeavereason() {
        return leavereason;
    }

    public void setLeavereason(String leavereason) {
        this.leavereason = leavereason;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}