package edu.therealbranik.therealflower.user;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Position {

    private String userId;
    private Double lat;
    private Double lon;
    private boolean valid;
    private Object timestamp;

    public Position () {}

    public Position (String userId, double lat, double lon, Object timestamp) {
        this.userId = userId;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        valid = true;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
