package edu.therealbranik.therealflower.homescreen.home;


import android.net.Uri;

import java.util.Date;

public class CardModel {
    private String userID;
    private String nameId;
    private String descriptionId;
    private String timestampID;
    private String postImage1ID;

    public CardModel(String userID, String nameId, String descriptionId, String timestampID, String postImage1ID) {
        this.postImage1ID=postImage1ID;
        this.userID=userID;
        this.descriptionId=descriptionId;
        this.nameId=nameId;
        this.timestampID=timestampID;
    }


    public String getUserID() {
        return userID;
    }


    public String getDescriptionId() {
        return descriptionId;
    }

    public String getNameId() {
        return nameId;
    }

    public String getTimestampID() {
        return timestampID;
    }

    public String getPostImage1ID() {
        return postImage1ID;
    }

}
