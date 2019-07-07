package edu.therealbranik.therealflower.post;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import edu.therealbranik.therealflower.R;

public class ShowPostActivity extends AppCompatActivity {

    private String postID;

    TextView textViewPostID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        postID = getIntent().getExtras().getString("id");
        textViewPostID = (TextView) findViewById(R.id.textViewPostId);
        textViewPostID.setText(postID);
    }

}
