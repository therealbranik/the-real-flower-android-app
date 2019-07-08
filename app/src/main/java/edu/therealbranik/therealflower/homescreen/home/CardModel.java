package edu.therealbranik.therealflower.homescreen.home;

public class CardModel {
    private int profileImageId;
    private int nameId;
    private int descriptionId;
    private int timestampID;
    private int postImage1ID;
    private int postImage2ID;

    public CardModel(int profileImageId, int nameId, int descriptionId,int timestampID,int postImage1ID,int postImage2ID) {
        this.postImage1ID=postImage1ID;
        this.postImage2ID=postImage2ID;
        this.profileImageId=profileImageId;
        this.descriptionId=descriptionId;
        this.nameId=nameId;
        this.timestampID=timestampID;
    }

    public int getProfileImageId() {
        return profileImageId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    public int getNameId() {
        return nameId;
    }

    public int getTimestampID() {
        return timestampID;
    }

    public int getPostImage1ID() {
        return postImage1ID;
    }

    public int getPostImage2ID() {
        return postImage2ID;
    }
}
