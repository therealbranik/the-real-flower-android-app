package edu.therealbranik.therealflower.login_register;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.File;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.user.User;
import edu.therealbranik.therealflower.utility.UploadImageActivity;

public class RegisterActivity extends AppCompatActivity {

    public static final int UPLOAD_FROM_FILE_SYSTEM = 1001;
    public static final int UPLOAD_FROM_CAMERA = 1002;
    private static int IMAMGE_MODE = 0;

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

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

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

    private void register () {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmedPassword = editTextPasswordConfirm.getText().toString();

//        if (!isFiledsFilled()) {
//            Toast.makeText(this, R.string.incomplite_fields, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!password.equals(confirmedPassword)) {
//            Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (imageURI == null) {
            Toast.makeText(this, R.string.image_required, Toast.LENGTH_SHORT).show();
            return;
        }

        uploadAvatar();
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser currentUser = mAuth.getCurrentUser();
//                            currentUser.sendEmailVerification();
//                            uploadData();
//                        }
//                    }
//                });
    }

    private boolean isFiledsFilled () {
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

    private void uploadAvatar () {
        if (IMAMGE_MODE == UPLOAD_FROM_FILE_SYSTEM && imageURI != null) {
            try {
                FirebaseUser currentUser = mAuth.getCurrentUser();
//                String fileName = currentUser.getUid().toString() + ".jpg";
                String fileName = "asdasdasd" + ".jpg";
                StorageReference avatarsRef = mStorageRef.child("images/avatars/" + fileName);

                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = avatarsRef.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(RegisterActivity.this, "Success uploaded avatar!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "Failed uploaded avatar!", Toast.LENGTH_LONG).show();
                            }
                        });
            } catch (Exception e) {
                Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadData () {
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
                    }
                });
    }

//    private String getRealPathFromURI(Uri contentURI) {
//        String result;
//        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = contentURI.getPath();
//        } else {
//            cursor.moveToFirst();
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            result = cursor.getString(idx);
//            cursor.close();
//        }
//        return result;
//    }
}
