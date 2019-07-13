package edu.therealbranik.therealflower.post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.user.UserProfileActivity;

public class ShowPostActivity extends AppCompatActivity {

    private String postID;
    private String userID;

    private FirebaseStorage mStorage;
    private FirebaseFirestore db;

    private TextView textViewUsername, textViewName, textViewDescription;
    private ImageView imageViewAvatar, imageViewMainImage;

    private View.OnClickListener onClickListenerAvatar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!userID.isEmpty()) {
                Intent i = new Intent(ShowPostActivity.this, UserProfileActivity.class);
                i.putExtra("id", userID);
                startActivity(i);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        imageViewMainImage = (ImageView) findViewById(R.id.imageViewMainImage);

        imageViewAvatar.setOnClickListener(onClickListenerAvatar);

        postID = getIntent().getExtras().getString("id");

        db.collection("posts").document(postID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isComplete()) {
                            Post post = task.getResult().toObject(Post.class).withId(task.getResult().getId());
                            userID = post.getUserId();
                            loadData(post);
                            loadUserAvatar(post.getUserId());
                            loadPhoto();
                        }
                    }
                });

    }



    private void loadData (Post post) {
        textViewName.setText(post.getName());
        textViewUsername.setText(post.getUser_username());
        textViewDescription.setText(post.getDescription());
    }

    private void loadUserAvatar (String id) {
        StorageReference avatarRef = mStorage.getReference("images/avatars/" +  id + ".jpg");
        avatarRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).fit().into(imageViewAvatar);
                    }
                });
    }

    private void loadPhoto () {
        StorageReference avatarRef = mStorage.getReference("images/posts/" +  postID + ".jpg");
        avatarRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).fit().into(imageViewMainImage);
                    }
                });
    }
}
