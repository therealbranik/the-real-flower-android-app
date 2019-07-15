package edu.therealbranik.therealflower.homescreen.home;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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

import javax.annotation.Nullable;

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

    private ListView listView;
    private CardsAdapter adapter ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        listView = view.findViewById(R.id.list_view_home);

        adapter = new CardsAdapter(getContext());

        arrayPosts=new ArrayList<>();
        mStorage = FirebaseStorage.getInstance();
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        getPosts();
        refreshPosts();
        onChangePostsListener();

        return view;
    }

    private void refreshPosts () {
        listView.setAdapter(adapter);
    }

    private void onChangePostsListener () {
        db.collection("posts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        clearDataStuctures();
                        getPosts();
                        refreshPosts();
                    }
                });
    }

    private void getPosts () {
        db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            clearDataStuctures();
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

                                adapter.add(new CardModel(arrayPosts.get(i).getUserId(), arrayPosts.get(i).getName(), arrayPosts.get(i).getDescription(), stringTimestamp,arrayPosts.get(i).id));
                            }
                        }
                    }
                });
    }

    private void clearDataStuctures () {
        arrayPosts.clear();
        adapter.clear();
    }
}
