package edu.therealbranik.therealflower.homescreen.social.friend_request;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FriendRequest {
    public static final int REQUEST = 0;
    public static final int ACCEPT = 1;
    public static final int CANCEL = 2;

    @Exclude
    public String id;
    private String fromId;
    private String toId;
    private int type;

    public FriendRequest () {}

    public FriendRequest (String fromId, String toId, int type) {
        this.fromId = fromId;
        this.toId = toId;
        this.type = type;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public <T extends FriendRequest> T withId(@NonNull final String id) {
        this.id = id;
        return (T) this;
    }

}
