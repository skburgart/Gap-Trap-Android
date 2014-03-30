package com.skburgart.pothole.net;

public class Report {

    private int rid;
    private String androidid;
    private double gforce;
    private double latitude;
    private double longitude;
    private String timestamp;
    
    public int getRid() {
        return rid;
    }
    public void setRid(int rid) {
        this.rid = rid;
    }
    public String getAndroidid() {
        return androidid;
    }
    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }
    public double getGforce() {
        return gforce;
    }
    public void setGforce(double gforce) {
        this.gforce = gforce;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
