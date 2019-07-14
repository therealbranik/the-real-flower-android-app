package edu.therealbranik.therealflower.post;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.login_register.LoginActivity;
import edu.therealbranik.therealflower.ranking.RankingActivity;
import edu.therealbranik.therealflower.settings.SettingsActivity;
import edu.therealbranik.therealflower.user.User;
import edu.therealbranik.therealflower.utility.UploadImageActivity;

public class AddPostActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int GET_LOCATION = 9882;
    public static final int GET_IMAGE = 9883;


    private double mLongitude;
    private double mLatitude;
    private Post mPost;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;

    private Uri photoUri;

    private Button buttonGetLocation;
    private Button buttonPost;
    private TextView textViewName;
    private TextView textViewDescription;
    private ImageView imageViewAddPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        mLatitude = Double.NaN;
        mLongitude = Double.NaN;
        buttonGetLocation = (Button) findViewById(R.id.buttonLocation);
        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddPostActivity.this, AddPostMapsActivity.class);
                i.putExtra("lat", mLatitude);
                i.putExtra("lon", mLongitude);
                startActivityForResult(i, GET_LOCATION);
            }
        });
        buttonPost = (Button) findViewById(R.id.buttonPost);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePost();
            }
        });
        textViewName = (TextView) findViewById(R.id.editTextName);
        textViewDescription = (TextView) findViewById(R.id.editTextDescription);
        imageViewAddPhoto = (ImageView) findViewById(R.id.imageViewAddPhoto);
        imageViewAddPhoto.setOnClickListener(onClickListenerImageAddPhoto);
        imageViewAddPhoto.setDrawingCacheEnabled(true);
        imageViewAddPhoto.buildDrawingCache();
    }

    private View.OnClickListener onClickListenerImageAddPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(AddPostActivity.this, UploadImageActivity.class);
            startActivityForResult(i, GET_IMAGE);
        }
    };

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
        getMenuInflater().inflate(R.menu.add_post, menu);
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
                i = new Intent(AddPostActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            }
            case R.id.nav_settings:
                i = new Intent(AddPostActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.nav_ranking:
                i =new Intent(AddPostActivity.this, RankingActivity.class);
                startActivity(i);
                return true;
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == GET_LOCATION) {
                mLongitude = data.getExtras().getDouble("lon");
                mLatitude= data.getExtras().getDouble("lat");
            } else if (requestCode == GET_IMAGE) {

                photoUri = Uri.parse(data.getStringExtra("uri"));
                Picasso.get().load(photoUri).fit().into(imageViewAddPhoto);
            }

        } catch (Exception e) {

        }
    }

    private boolean validation () {
        String name = textViewName.getText().toString();
        String description = textViewDescription.getText().toString();
        if (name.isEmpty()
                || description.isEmpty()
                || Double.isNaN(mLatitude)
                || Double.isNaN(mLongitude))
            return false;

        return true;
    }

    private void makePost () {
        if (!validation()) {
            //TODO WARNING
            return;
        }

        String name = textViewName.getText().toString();
        String description = textViewDescription.getText().toString();

        FirebaseUser user = mAuth.getCurrentUser();

        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User u = task.getResult().toObject(User.class).withId(task.getResult().getId());

                            Post post = new Post(user.getUid(), name, description, mLongitude, mLatitude, u.getUsername(), u.getFullName());

                            DocumentReference postRef = db.collection("posts").document();
                            String postId = postRef.getId();
                            Toast.makeText(AddPostActivity.this, postId, Toast.LENGTH_SHORT).show();
                            postRef.set(post)
//                                    .add(post)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                db.collection("users").document(user.getUid()).update("points", FieldValue.increment(10));
                                                uploadPhoto(postId);
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void uploadPhoto(String postId) {
        if (photoUri != null) {
            try {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String fileName = postId + ".jpg";
                StorageReference avatarsRef = mStorage.getReference().child("images/posts/" + fileName);

                Bitmap bitmap = ((BitmapDrawable) imageViewAddPhoto.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();


                UploadTask uploadTask = avatarsRef.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        finish();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(AddPostActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
