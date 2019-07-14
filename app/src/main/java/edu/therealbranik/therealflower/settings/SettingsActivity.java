package edu.therealbranik.therealflower.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.user.LocationTrackingService;

public class SettingsActivity extends AppCompatActivity {

    private Button buttonStartTracking;
    private Button buttonStopTracking;

    View.OnClickListener onClickListenerStartTracking = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent svc = new Intent(SettingsActivity.this, LocationTrackingService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(svc);
            } else {
                startService(svc);
            }
        }
    };

    View.OnClickListener onClickListenerStopTracking = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent svc = new Intent(SettingsActivity.this, LocationTrackingService.class);
            stopService(svc);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonStartTracking = (Button) findViewById(R.id.buttonStartTracking);
        buttonStartTracking.setOnClickListener(onClickListenerStartTracking);
        buttonStopTracking = (Button) findViewById(R.id.buttonStopTracking);
        buttonStopTracking.setOnClickListener(onClickListenerStopTracking);

    }

}
