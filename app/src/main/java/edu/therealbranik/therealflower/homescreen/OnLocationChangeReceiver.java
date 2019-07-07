package edu.therealbranik.therealflower.homescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import edu.therealbranik.therealflower.user.Position;

public class OnLocationChangeReceiver extends BroadcastReceiver {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;

    public OnLocationChangeReceiver () {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            try {
                Double lat = extras.getDouble("lat");
                Double lon = extras.getDouble("lon");

                Position position = new Position(user.getUid(), lat, lon, FieldValue.serverTimestamp());

                db.collection("positions").document(user.getUid())
                        .set(position);

            } catch (Exception e) {

            }
        }
    }
}
