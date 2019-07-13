package edu.therealbranik.therealflower.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView textViewFullname;
    private TextView textViewTelNumber;
    private ImageButton imageButtonLocation;
    private ImageView imageViewAvatar;
    private Group groupContent;
    private ProgressBar progressBarLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        getProfileData(getIntent().getExtras().getString("id"));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        textViewFullname = (TextView) findViewById(R.id.textViewFullname);
        textViewTelNumber = (TextView) findViewById(R.id.textViewTelNumber);
        imageButtonLocation = (ImageButton) findViewById(R.id.imageButtonLocation);
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        groupContent = (Group) findViewById(R.id.groupContent);
        progressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
        setLoading(true);
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
                return true;
            }
            case R.id.nav_settings:
                i = new Intent(UserProfileActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.nav_ranking:
                i =new Intent(UserProfileActivity.this, RankingActivity.class);
                startActivity(i);
                return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getProfileData (final String id) {
        db.collection("users").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User u = (User) task.getResult().toObject(User.class).withId(task.getResult().getId());
                            textViewFullname.setText(u.getFullName());
                            if (u.getPhoneNumber() != null)
                                textViewTelNumber.setText(u.getPhoneNumber());
                            StorageReference avatarRef = mStorage.getReference("images/avatars/" +  u.id + ".jpg");
                            avatarRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.get().load(uri.toString()).into(imageViewAvatar);
                                        setLoading(false);
                                    }
                                });
                        }
                    }
                });
    }

    private void setLoading (boolean loading) {
        if (loading) {
            groupContent.setVisibility(View.GONE);
            progressBarLoading.setVisibility(View.VISIBLE);
        } else {
            progressBarLoading.setVisibility(View.GONE);
            groupContent.setVisibility(View.VISIBLE);
        }
    }
}
