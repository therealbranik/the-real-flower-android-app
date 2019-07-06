package edu.therealbranik.therealflower.user;

public class Position {

    private String userId;
    private Double lat;
    private Double lon;
    private boolean valid;

    public Position () {}

    public Position (String userId, double lat, double lon) {
        this.userId = userId;
        this.lat = lat;
        this.lon = lon;
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
}
