package edu.therealbranik.therealflower.login_register;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.homepage.HomeScreenActivity;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonLogin;
    Button buttonSignup;
    Button buttonGoogle;
    Button buttonFacebook;


    private void LogIn(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, R.string.incomplite_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(LoginActivity.this, HomeScreenActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void SingIn (String email, String password) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.edit_text_email);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);
        buttonLogin = (Button) findViewById(R.id.button_login);
        buttonSignup = (Button) findViewById(R.id.button_siginup);
        buttonGoogle = (Button) findViewById(R.id.button_google);
        buttonFacebook = (Button) findViewById(R.id.button_facebook);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                LogIn(email, password);

            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                SingIn(email, password);
            }
        });
    }
}
