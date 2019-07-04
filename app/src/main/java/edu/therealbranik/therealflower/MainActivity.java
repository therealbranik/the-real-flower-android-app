package edu.therealbranik.therealflower;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.therealbranik.therealflower.homescreen.HomescreenActivity;
import edu.therealbranik.therealflower.login_register.LoginActivity;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        Intent i;
        if (user == null) {
            i = new Intent(this, LoginActivity.class);
        } else {
            i = new Intent(this, HomescreenActivity.class);
        }
//        i = new Intent(this, ProbaActivity.class);
        startActivity(i);
        finish();
    }
}
