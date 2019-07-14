package edu.therealbranik.therealflower.homescreen.profile;


import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.user.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Button locationBtn, moreFriendsBtn;
    ImageView avatar,friendAv1,friendAv2,friendAv3,friendAv4, shot1,shot2,shot3,shot4,shot5,shot6;
    TextView profileName, textViewUsername, moreShots, textViewPoints;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    Map<String,User> hashMapUsers;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        hashMapUsers=new HashMap<>();

        locationBtn=(Button)view.findViewById(R.id.profile_location_button);
        moreFriendsBtn=(Button)view.findViewById(R.id.profile_friends_button);

        avatar=(ImageView)view.findViewById(R.id.profile_avatar);
        friendAv1=(ImageView)view.findViewById(R.id.friend_avatar1);
        friendAv2=(ImageView)view.findViewById(R.id.friend_avatar2);
        friendAv3=(ImageView)view.findViewById(R.id.friend_avatar3);
        shot1=(ImageView)view.findViewById(R.id.profile_img_1);
        shot2=(ImageView)view.findViewById(R.id.profile_img_2);
        shot3=(ImageView)view.findViewById(R.id.profile_img_3);
        shot4=(ImageView)view.findViewById(R.id.profile_img_4);
        shot5=(ImageView)view.findViewById(R.id.profile_img_5);
        shot6=(ImageView)view.findViewById(R.id.profile_img_6);

        profileName=(TextView)view.findViewById(R.id.profile_name_text);
        textViewUsername=(TextView)view.findViewById(R.id.textViewUsername);
        moreShots=(TextView)view.findViewById(R.id.profile_more_text);
        textViewPoints = (TextView) view.findViewById(R.id.textViewPoints);

        mStorage = FirebaseStorage.getInstance();
        mAuth=FirebaseAuth.getInstance();
            db=FirebaseFirestore.getInstance();
            String user_id = mAuth.getCurrentUser().getUid();

            db.collection("users").document(user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                        public void onComplete(Task<DocumentSnapshot> task){
                            if (task.isSuccessful()){
                                DocumentSnapshot doc=task.getResult();
                                User u = task.getResult().toObject(User.class).withId(task.getResult().getId());

                                if (doc.exists()){

                                    // Loading avatar
                                    StorageReference avatarRef = mStorage.getReference("images/avatars/" +  u.id + ".jpg");
                                    avatarRef.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Picasso.get().load(uri.toString()).into(avatar);
                                                }
                                            });
                                    // Loading data
                                    updateUserData(u);



                                }else{

                                }
                            }
                        }
                    });
            setOnUserChangeListener();

        return view;
    }

    private void setOnUserChangeListener () {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("ASDASDASDDSS", "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            User u = documentSnapshot.toObject(User.class).withId(documentSnapshot.getId());
                            updateUserData(u);
                        } else {
                            Log.d("ASDASDASDASD", "Current data: null");
                        }

                    }
                });
    }

    private void updateUserData (User user) {
        profileName.setText(user.getFullName());
        textViewUsername.setText(user.getUsername());
        textViewPoints.setText(String.valueOf(user.getPoints()));
    }
}
