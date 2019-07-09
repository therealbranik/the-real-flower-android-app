package edu.therealbranik.therealflower.homescreen.social.available;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.homescreen.social.friend_request.FriendRequest;
import edu.therealbranik.therealflower.user.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class AvailableFragment extends Fragment {

    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private HashMap<Integer, User> indexToUsers = new HashMap<>();

    private ListView listViewAvailableUsers;

    public AvailableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_available, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        listViewAvailableUsers = (ListView) rootView.findViewById(R.id.listViewAvailableUsers);

        getUsers();

        listViewAvailableUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User u = indexToUsers.get(position);
                FriendRequest request = new FriendRequest(mAuth.getUid(), u.id, FriendRequest.REQUEST);
                db.collection("friend_requests")
                        .add(request);

            }
        });

        return rootView;
    }

    private void getUsers() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<String> s = new ArrayList<>();

                                    int i = 0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        User u = document.toObject(User.class).withId(document.getId());
                                        if (!u.id.equals(mAuth.getUid())) {
                                            s.add(u.getUsername());
                                            indexToUsers.put(i, u);
                                            i++;
                                        }
                                    }

                                    ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, s);

                                    listViewAvailableUsers.setAdapter(itemsAdapter);
                                }
                            }
                        });
            }
        });

        thread.start();
    }


}
