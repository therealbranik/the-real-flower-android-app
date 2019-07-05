package edu.therealbranik.therealflower.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.therealbranik.therealflower.utility.Permissons;

import edu.therealbranik.therealflower.R;

public class AddPostMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    static final int PERMISSION_ACCESS_FINE_LOCATION = 10001;
    static final int PERMISSION_ACCESS_COARSE_LOCATION = 10001;

    private GoogleMap mMap;
    private double mLongitude;
    private double mLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(R.string.set_location);

        mLatitude = getIntent().getExtras().getDouble("lat");
        mLongitude = getIntent().getExtras().getDouble("lon");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabAddLocOnClickListener);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

       if (!Permissons.Check_FINE_LOCATION(AddPostMapsActivity.this) && !Permissons.Check_COARSE_LOCATION(AddPostMapsActivity.this)) {
           Permissons.Request_FINE_LOCATION(AddPostMapsActivity.this, PERMISSION_ACCESS_FINE_LOCATION);
           Permissons.Request_COARSE_LOCATION(AddPostMapsActivity.this, PERMISSION_ACCESS_COARSE_LOCATION);
       }

       setOnMapClickListener();
       drawMarker();
       if (validation()) {
            LatLng location = new LatLng(mLatitude, mLongitude);
            mMap.addMarker(new MarkerOptions().position(location).title("Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20.0f));
       }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
                return;
            }
        }
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setOnMapClickListener () {
        if (mMap == null)
            return;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mLongitude = latLng.longitude;
                mLatitude = latLng.latitude;
                drawMarker();
            }
        });
    }

    private View.OnClickListener fabAddLocOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Double.isNaN(mLatitude) || Double.isNaN(mLongitude)) {
                Toast.makeText(AddPostMapsActivity.this, R.string.choose_location, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent latLng = new Intent();
            latLng.putExtra("lat", mLatitude);
            latLng.putExtra("lon", mLongitude);
            setResult(Activity.RESULT_OK, latLng);
            finish();
        }
    };

    private void drawMarker () {
        if (validation()) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)));
        }
    }

    private boolean validation () {
        if (Double.isNaN(mLatitude) || Double.isNaN(mLongitude)) {
            return false;
        }

        return true;
    }
}
