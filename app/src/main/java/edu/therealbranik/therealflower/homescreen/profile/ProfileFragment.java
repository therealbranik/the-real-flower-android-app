package edu.therealbranik.therealflower.homescreen.profile;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.login_register.LoginActivity;
import edu.therealbranik.therealflower.user.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Button locationBtn, moreFriendsBtn;
    ImageView avatar,friendAv1,friendAv2,friendAv3,friendAv4, shot1,shot2,shot3,shot4,shot5,shot6;
    TextView profileName, phoneNumber, moreShots;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
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
        friendAv4=(ImageView)view.findViewById(R.id.friend_avatar4);
        shot1=(ImageView)view.findViewById(R.id.profile_img_1);
        shot2=(ImageView)view.findViewById(R.id.profile_img_2);
        shot3=(ImageView)view.findViewById(R.id.profile_img_3);
        shot4=(ImageView)view.findViewById(R.id.profile_img_4);
        shot5=(ImageView)view.findViewById(R.id.profile_img_5);
        shot6=(ImageView)view.findViewById(R.id.profile_img_6);

        profileName=(TextView)view.findViewById(R.id.profile_name_text);
        phoneNumber=(TextView)view.findViewById(R.id.profile_phone_text);
        moreShots=(TextView)view.findViewById(R.id.profile_more_text);

        mAuth=FirebaseAuth.getInstance();
            db=FirebaseFirestore.getInstance();
            String user_id = mAuth.getCurrentUser().getUid();

            db.collection("users").document(user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                        public void onComplete(Task<DocumentSnapshot> task){
                            if (task.isSuccessful()){
                                DocumentSnapshot doc=task.getResult();
                                if (doc.exists()){
                                    profileName.setText(doc.getString("username"));
                                    phoneNumber.setText(doc.getString("phoneNumber"));
                                }else{

                                }
                            }
                        }
                    });







        return view;
    }

}
