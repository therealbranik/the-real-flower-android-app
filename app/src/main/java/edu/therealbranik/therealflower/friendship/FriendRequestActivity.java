package edu.therealbranik.therealflower.friendship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.user.FriendRequest;
import edu.therealbranik.therealflower.user.User;

public class FriendRequestActivity extends AppCompatActivity {

    private String mUserReqId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;

    private TextView textViewDialogText;
    private Button buttonAccept;
    private Button buttonDecline;
    private ImageView imageViewAvatar;

    View.OnClickListener onClickListenerAccept = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createFriendShip(mAuth.getCurrentUser().getUid(), mUserReqId);
            finish();
        }
    };

    View.OnClickListener onClickListenerDecline = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        mUserReqId = getIntent().getExtras().getString("id");

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewDialogText = (TextView) findViewById(R.id.textViewdialogText);
        buttonAccept = (Button) findViewById(R.id.buttonAccept);
        buttonAccept.setOnClickListener(onClickListenerAccept);
        buttonDecline = (Button) findViewById(R.id.buttonDecline);
        buttonDecline.setOnClickListener(onClickListenerDecline);
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);

        StorageReference avatarRef = mStorage.getReference("images/avatars/" + mUserReqId + ".jpg");
        avatarRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imageViewAvatar);
                    }
                });

        db.collection("users").document(mUserReqId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User u = task.getResult().toObject(User.class).withId(task.getResult().getId());

                            textViewDialogText.setText(getResources().getString(R.string.friend_req_from) + ": " + u.getFullName() + "[" + u.getUsername() + "]");
                        }
                    }
                });
    }

    private void createFriendShip (String friendAId, String friendBid) {

        Map<String, Object> dataA = new HashMap<>();
        Map<String, Object> dataB = new HashMap<>();
        dataA.put(friendBid, true);
        dataB.put(friendAId, true);

        db.collection("friends").document(friendAId)
                .set(dataA, SetOptions.merge());

        db.collection("friends").document(friendBid)
                .set(dataB, SetOptions.merge());

    }
}
