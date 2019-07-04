package edu.therealbranik.therealflower.login_register;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.homescreen.HomescreenActivity;
import edu.therealbranik.therealflower.user.User;
import edu.therealbranik.therealflower.utility.UploadImageActivity;

public class RegisterActivity extends AppCompatActivity {

    public static final int UPLOAD_FROM_FILE_SYSTEM = 1001;
    public static final int UPLOAD_FROM_CAMERA = 1002;
    private static int IMAMGE_MODE = 0;

    private boolean AVATAR_UPLOADED;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference mStorageRef;

    EditText editTextUsername;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextPasswordConfirm;
    EditText editTextFullName;
    Button buttonRegister;
    ImageView imageViewAvatar;

    Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AVATAR_UPLOADED = false;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = (EditText) findViewById(R.id.editTextConfirmPassword);
        editTextFullName = (EditText) findViewById(R.id.editTextFullName);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);

//        buttonRegister.setEnabled(false);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRegister.setEnabled(false);
                register();
            }
        });

        imageViewAvatar.setDrawingCacheEnabled(true);
        imageViewAvatar.buildDrawingCache();
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, UploadImageActivity.class);
                startActivityForResult(i, 1);
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        }

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_FIRST_USER && resultCode == UPLOAD_FROM_FILE_SYSTEM
                && data != null) {

            imageURI = Uri.parse(data.getStringExtra("uri"));

            Picasso.get().load(imageURI).fit().into(imageViewAvatar);
            IMAMGE_MODE = UPLOAD_FROM_FILE_SYSTEM;
        }
    }

    private void register() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmedPassword = editTextPasswordConfirm.getText().toString();

        if (!isFiledsFilled()) {
            Toast.makeText(this, R.string.incomplite_fields, Toast.LENGTH_SHORT).show();
            buttonRegister.setEnabled(true);
            return;
        }

        if (!password.equals(confirmedPassword)) {
            Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
            buttonRegister.setEnabled(true);
            return;
        }

        if (imageURI == null) {
            Toast.makeText(this, R.string.image_required, Toast.LENGTH_SHORT).show();
            buttonRegister.setEnabled(true);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            currentUser.sendEmailVerification();
                            uploadData();
                        }
                    }
                }).
                addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        buttonRegister.setEnabled(true);
                    }
                });
    }

    private boolean isFiledsFilled() {
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmedPassword = editTextPasswordConfirm.getText().toString();
        String fullname = editTextFullName.getText().toString();

        if (username.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || confirmedPassword.isEmpty()
                || fullname.isEmpty()
        )

            return false;
        else
            return true;

    }

    private void uploadAvatar() {
        if (IMAMGE_MODE == UPLOAD_FROM_FILE_SYSTEM && imageURI != null) {
            try {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String fileName = currentUser.getUid().toString() + ".jpg";
                StorageReference avatarsRef = mStorageRef.child("images/avatars/" + fileName);

                Bitmap bitmap = ((BitmapDrawable) imageViewAvatar.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();


                UploadTask uploadTask = avatarsRef.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        AVATAR_UPLOADED = true;
                        Intent i = new Intent(RegisterActivity.this, HomescreenActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "Failed uploaded avatar!", Toast.LENGTH_LONG).show();
                                buttonRegister.setEnabled(true);
                            }
                        });
            } catch (Exception e) {
                Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadData() {
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String fullName = editTextFullName.getText().toString();

        User user = new User(username, fullName, email, null);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        uploadAvatar();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, R.string.signin_failed, Toast.LENGTH_SHORT).show();
                        buttonRegister.setEnabled(true);
                    }
                });
    }
}
