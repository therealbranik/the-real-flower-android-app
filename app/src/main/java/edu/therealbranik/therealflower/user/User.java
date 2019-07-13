package edu.therealbranik.therealflower.user;


import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    @Exclude
    public String id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private int points;
    private FirebaseFirestore db;

    public User () {
        db = FirebaseFirestore.getInstance();
    }

    public User (String username, String fullName, String email, String phoneNumber) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.db = FirebaseFirestore.getInstance();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullNameame(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void addPoints (int points) {
        if (this.points >= 0) {
            this.points += points;
        } else {
            this.points = points;
        }
    }

    public void subPoints (int points) {
        if (this.points > points) {
            this.points -= points;
        } else {
            this.points = 0;
        }
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void save () {
        db.collection("users")
                .add(this);
    }

    public <T extends User> T withId(@NonNull final String id) {
        this.id = id;
        return (T) this;
    }


}
