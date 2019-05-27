package edu.therealbranik.therealflower.homepage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.login_register.LoginActivity;

public class HomeScreenActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    TextView verif;
    Button signoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mAuth = FirebaseAuth.getInstance();

        verif = (TextView) findViewById(R.id.virified);
        signoutButton = (Button) findViewById(R.id.button_signout);

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent i = new Intent(HomeScreenActivity.this, LoginActivity.class);
                startActivity(i);

                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser.isEmailVerified()) {
            verif.setText("VERIFIED");
        } else {
            verif.setText("UNVERIFIED");
        }
    }
}
