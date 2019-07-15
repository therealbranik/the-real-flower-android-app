package edu.therealbranik.therealflower.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import edu.therealbranik.therealflower.friendship.FriendRequestActivity;

public class FriendRequestReceiver extends BroadcastReceiver {

    public static final String BROADCAST_FRIEND_REQUEST = ".user.FIREND_REQUEST";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String userId = extras.getString("id");

            Intent i = new Intent(context, FriendRequestActivity.class);
            i.putExtra("id", userId);
            context.startActivity(i);

//            Toast.makeText(context, "FRIEND REQ FROM " + userId, Toast.LENGTH_SHORT).show();
        }
    }
}
