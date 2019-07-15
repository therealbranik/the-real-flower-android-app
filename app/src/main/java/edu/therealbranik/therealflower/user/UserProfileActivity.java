package edu.therealbranik.therealflower.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import javax.annotation.Nullable;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.login_register.LoginActivity;
import edu.therealbranik.therealflower.post.AddPostActivity;
import edu.therealbranik.therealflower.ranking.RankingActivity;
import edu.therealbranik.therealflower.settings.SettingsActivity;

public class UserProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;

    Button locationBtn, moreFriendsBtn;
    ImageView avatar,friendAv1,friendAv2,friendAv3,friendAv4, shot1,shot2,shot3,shot4,shot5,shot6;
    TextView profileName, textViewUsername, moreShots, textViewPoints;

    String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUserId = getIntent().getExtras().getString("id");
        getProfileData(mUserId);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        locationBtn = (Button) findViewById(R.id.profile_location_button);
        moreFriendsBtn = (Button) findViewById(R.id.profile_friends_button);

        avatar = (ImageView) findViewById(R.id.profile_avatar);
        friendAv1 = (ImageView) findViewById(R.id.friend_avatar1);
        friendAv2 = (ImageView) findViewById(R.id.friend_avatar2);
        friendAv3 = (ImageView) findViewById(R.id.friend_avatar3);
        shot1 = (ImageView) findViewById(R.id.profile_img_1);
        shot2 = (ImageView) findViewById(R.id.profile_img_2);
        shot3 = (ImageView) findViewById(R.id.profile_img_3);
        shot4 = (ImageView) findViewById(R.id.profile_img_4);
        shot5 = (ImageView) findViewById(R.id.profile_img_5);
        shot6 = (ImageView) findViewById(R.id.profile_img_6);

        profileName = (TextView) findViewById(R.id.profile_name_text);
        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        moreShots = (TextView) findViewById(R.id.profile_more_text);
        textViewPoints = (TextView) findViewById(R.id.textViewPoints);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent i;
        switch (item.getItemId()) {
            case R.id.nav_item_signout: {
                mAuth.signOut();
                i = new Intent(UserProfileActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            }
            case R.id.nav_settings:
                i = new Intent(UserProfileActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_ranking:
                i = new Intent(UserProfileActivity.this, RankingActivity.class);
                startActivity(i);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getProfileData(final String id) {
        db.collection("users").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User u = (User) task.getResult().toObject(User.class).withId(task.getResult().getId());
                            StorageReference avatarRef = mStorage.getReference("images/avatars/" + u.id + ".jpg");
                            avatarRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get().load(uri.toString()).into(avatar);
                                        }
                                    });
                            updateUserData(u);
                        }
                    }
                });
    }

    private void updateUserData (User u) {
        setTitle(u.getFullName());
        profileName.setText(u.getFullName());
        textViewUsername.setText(u.getUsername());
        textViewPoints.setText(String.valueOf(u.getPoints()));
    }

    private void setOnUserChangeListener () {
        db.collection("users").document(mUserId)
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
}
