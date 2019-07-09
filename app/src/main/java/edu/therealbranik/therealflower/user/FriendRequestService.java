package edu.therealbranik.therealflower.user;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import edu.therealbranik.therealflower.homescreen.social.friend_request.FriendRequest;

public class FriendRequestService extends Service {

    public static final String BROADCAST_ACTION = ".homescreen.OnFriendRequestReceiver";


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Intent intentSend = new Intent(BROADCAST_ACTION);

    public FriendRequestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        db.collection("friend_requests")
                .whereEqualTo("fromId", mAuth.getCurrentUser().getUid())
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("SSDA", "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
                            if (document.getType() == DocumentChange.Type.ADDED) {
                                FriendRequest request = document.getDocument().toObject(FriendRequest.class).withId(document.getDocument().getId());

                                intentSend.putExtra("fromId", document.getDocument().getId());
                                sendBroadcast(intentSend);
                            }
                        }
                    }
                });

        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    Thread thread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//
//        }
//    });



}
