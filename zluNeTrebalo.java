package edu.therealbranik.therealflower.homescreen.explore;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.user.Position;
import edu.therealbranik.therealflower.user.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    private static final String TAG = "ExploreFragment";

    FirebaseFirestore db;
    GoogleMap mMap;
    MapView mMapView;
    HashMap<String, Marker> hashMapMarkerUsers = new HashMap<>();
    HashMap<String, User> hashMapUsers = new HashMap<>();

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.fragment_explore, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();


        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

//                getAllPositions();
                setOnChangePositionsListener();
                // For showing a move to my location button
//                googleMap.setMyLocationEnabled(true);
//
//                // For dropping a marker at a point on the Map
//                LatLng sydney = new LatLng(-34, 151);
//                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
//
//                // For zooming automatically to the location of the marker
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

//    private void getAllPositions () {
//                    db.collection("positions")
//                            .get()
//                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                            Position p = (Position) document.toObject(Position.class);
//                                            Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLat(), p.getLon())));
//                                            hashMapMarkerUsers.put(document.getId(), m);
//                                        }
//                                    } else {
//                                    }
//                                }
//                            });
//
//    }

    private void setOnChangePositionsListener () {
//        Thread thread = new Thread() {
//            @Override
//            public void run () {
//                try {
                    db.collection("positions")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.w(TAG, "Listen failed.", e);
                                        return;
                                    }

                                    for (QueryDocumentSnapshot document : value) {
                                       drawMarker(document.toObject(Position.class));
                                    }
                                }
                            });
//                } catch (Exception e) {
//
//                }
//            }
//        };

//        thread.start();
    }

    private void drawMarker (Position position) {
        if (hashMapUsers.containsKey(position.getUserId())) {
            Marker oldMarker = hashMapMarkerUsers.get(position.getUserId());
            if (oldMarker != null)
                oldMarker.remove();
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(position.getLat(), position.getLon()))
                    .title(hashMapUsers.get(position.getUserId()).getUsername()));
            hashMapMarkerUsers.put(position.getUserId(), m);
        } else {
            db.collection("users").document(position.getUserId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    User user = (User) document.toObject(User.class);
                                    hashMapUsers.put(document.getId(), user);
                                } else {

                                }


                            }
                        }
                    });
        }





    }
}
