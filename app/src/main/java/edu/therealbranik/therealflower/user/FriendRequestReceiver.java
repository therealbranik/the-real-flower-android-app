package edu.therealbranik.therealflower.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class FriendRequestReceiver extends BroadcastReceiver {

    public static final String BROADCAST_FRIEND_REQUEST = ".user.FIREND_REQUEST";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String userId = extras.getString("id");
            String name = extras.getString("name");
            String username = extras.getString("username");
            Toast.makeText(context, "FRIEND REQ FROM " + username, Toast.LENGTH_SHORT).show();
        }
    }
}
