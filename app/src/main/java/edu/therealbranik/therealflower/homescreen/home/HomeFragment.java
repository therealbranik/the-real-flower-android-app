package edu.therealbranik.therealflower.homescreen.home;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.post.Post;
import edu.therealbranik.therealflower.user.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    ArrayList<Post> arrayPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        ListView listView=view.findViewById(R.id.list_view_home);
        final CardsAdapter adapter = new CardsAdapter(getContext());
        listView.setAdapter(adapter);

        arrayPosts=new ArrayList<>();
        mStorage = FirebaseStorage.getInstance();
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();


        db.collection("posts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc: task.getResult()) {
                                Post p = doc.toObject(Post.class).withId(doc.getId());

                                arrayPosts.add(p);
                            }
                            for(int i=0;i<arrayPosts.size();i++)
                            {
                                String stringTimestamp = "";
                                Date date = arrayPosts.get(i).getTimestamp() ;
                                if (date != null) {
                                    DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                                    stringTimestamp = dateFormat.format(date);
                                }

                                adapter.add(new CardModel(arrayPosts.get(i).getUserId(), arrayPosts.get(i).getName(), arrayPosts.get(i).getDescription(), stringTimestamp,R.drawable.maslacak));
                            }
                        }
                    }
                });


        //TO DO: ADD POST's FROM DATABASE


        return view;
    }

}
