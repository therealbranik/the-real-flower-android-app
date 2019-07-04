package edu.therealbranik.therealflower.post;

public class Post {
    private String name;
    private String description;
    private int longitude;
    private int latitude;

    Post (String name, String description, int longitude, int latitude) {
        this.name = name;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    Post () {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }
}
