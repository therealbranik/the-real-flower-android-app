package edu.therealbranik.therealflower.login_register;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.homepage.HomeScreenActivity;
import edu.therealbranik.therealflower.user.User;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    EditText editTextUsername;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextPasswordConfirm;
    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextPhoneNumber;
    Button buttonRegister;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = this;

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = (EditText) findViewById(R.id.editTextConfirmPassword);
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }


    private void register () {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmedPassword = editTextPasswordConfirm.getText().toString();

        if (!isFiledsFilled()) {
            Toast.makeText(mContext, R.string.incomplite_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmedPassword)) {
            Toast.makeText(mContext, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String username = editTextUsername.getText().toString();
                            String email = editTextEmail.getText().toString();
                            String firstName = editTextFirstName.getText().toString();
                            String lastName = editTextLastName.getText().toString();
                            String phoneNumber = editTextPhoneNumber.getText().toString();

                            User user = new User(username, firstName, lastName, email, phoneNumber);

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            currentUser.sendEmailVerification();

                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Intent i = new Intent(RegisterActivity.this, HomeScreenActivity.class);
                                            startActivity(i);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, R.string.signin_failed, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    private boolean isFiledsFilled () {
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmedPassword = editTextPasswordConfirm.getText().toString();
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();

        if (username.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || confirmedPassword.isEmpty()
                || firstName.isEmpty()
                || lastName.isEmpty()
                || phoneNumber.isEmpty())
            return false;
        else
            return true;

    }
}
