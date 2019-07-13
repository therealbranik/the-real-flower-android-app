package edu.therealbranik.therealflower.ranking;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.user.User;
import edu.therealbranik.therealflower.user.UserProfileActivity;

public class RankingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ListView listViewRanking;

    private List<String> listUsersNameAndUsername = new ArrayList<>();
    private HashMap<Integer, User> indexToUser = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        listViewRanking = (ListView) findViewById(R.id.listViewRanking);
        listViewRanking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(RankingActivity.this, UserProfileActivity.class);
                i.putExtra("id", indexToUser.get(position).id);
                startActivity(i);
            }
        });

        getUsersSorted();
        setOnChangeUserListener();
//        loadUsersToListView();

    }

    private void getUsersSorted () {
        db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(100).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            clearDataStrucutres();
                            for (DocumentSnapshot doc: task.getResult()) {
                                User u = doc.toObject(User.class).withId(doc.getId());
                                addUserToDataStructures(u);
                            }

                            loadUsersToListView();
                        }
                    }
                });
    }

    private void addUserToDataStructures (User u) {
        listUsersNameAndUsername.add(String.valueOf(listUsersNameAndUsername.size() + 1) + ". " +  u.getFullName() + " [" + u.getUsername() + "]" + " (" + u.getPoints() + ")");
        indexToUser.put(listUsersNameAndUsername.size() - 1, u);
    }

    private void setOnChangeUserListener () {
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                clearDataStrucutres();
                getUsersSorted();
                loadUsersToListView();
            }
        });
    }

    private void clearDataStrucutres () {
        listUsersNameAndUsername.clear();
        indexToUser.clear();
    }

    private void loadUsersToListView () {
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listUsersNameAndUsername);
        listViewRanking.setAdapter(itemsAdapter);
    }
}
