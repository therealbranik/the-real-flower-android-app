package edu.therealbranik.therealflower.homescreen;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.therealbranik.therealflower.R;

public class OnFriendRequestReceiver extends BroadcastReceiver {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;

    public OnFriendRequestReceiver () {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            try {
                String fromId = extras.getString("fromId");


                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ASASDASDASD")
                        .setSmallIcon(R.drawable.baseline_add_24)
                        .setContentTitle("Friend req from " + fromId)
                        .setContentText("asdasd")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(0, builder.build());


            } catch (Exception e) {

            }
        }
    }
}

