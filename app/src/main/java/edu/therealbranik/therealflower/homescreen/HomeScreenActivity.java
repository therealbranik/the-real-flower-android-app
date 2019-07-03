package edu.therealbranik.therealflower.homescreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import edu.therealbranik.therealflower.MainActivity;
import edu.therealbranik.therealflower.R;

public class HomeScreenActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    Button buttonSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mAuth = FirebaseAuth.getInstance();

        buttonSignout = (Button) findViewById(R.id.buttonSignout);
        buttonSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i = new Intent(HomeScreenActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
