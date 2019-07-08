package edu.therealbranik.therealflower.homescreen.explore;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.homescreen.HomescreenActivity;
import edu.therealbranik.therealflower.post.Post;
import edu.therealbranik.therealflower.post.ShowPostActivity;
import edu.therealbranik.therealflower.settings.SettingsActivity;
import edu.therealbranik.therealflower.user.Position;
import edu.therealbranik.therealflower.user.User;
import edu.therealbranik.therealflower.user.UserProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    private static final String TAG = "ExploreFragment";
    private boolean SHOW_USERS = true;
    private boolean SHOW_POSTS = true;
    private ListenerRegistration onChangePositionListener;
    private ListenerRegistration onChangePostsListener;

    FirebaseFirestore db;
    GoogleMap mMap;
    MapView mMapView;
    HashMap<String, Marker> hashMapMarkerUsers = new HashMap<>();
    HashMap<String, Marker> hashMapMarkerPosts = new HashMap<>();
    HashMap<String, User> hashMapUsers = new HashMap<>();
    HashMap<String, String> markerIDtoPostID = new HashMap<>();
    HashMap<String, String> markerIDtoUserID = new HashMap<>();

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        String markerID = marker.getId();
                        String postID = markerIDtoPostID.get(markerID);
                        if (postID != null) {
                            Intent i = new Intent(getActivity(), ShowPostActivity.class);
                            i.putExtra("id", postID);
                            startActivity(i);
                            return;
                        }

                        String userID = markerIDtoUserID.get(markerID);
                        if (userID != null) {
                            Intent i = new Intent(getActivity(), UserProfileActivity.class);
                            i.putExtra("id", userID);
                            startActivity(i);
                            return;
                        }

                    }
                });

                drawAllPositions();
                drawAllPosts();
                onChangePositionListener = setOnChangePositionsListener();
                onChangePostsListener = setOnChangePostListener();
                // For showing a move to my location button
//                mMap.setMyLocationEnabled(true);

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

    private void drawAllPositions () {
        db.collection("positions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                drawUserMarker(document.toObject(Position.class));
                            }
                        } else {

                        }
                    }
                });
    }

    private void drawAllPosts () {
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                drawPostMarker(document.toObject(Post.class), document.getId());
                            }
                        }
                    }
                });
    }

    private ListenerRegistration setOnChangePositionsListener () {
        return (ListenerRegistration) db.collection("positions")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.w(TAG, "Listen failed.", e);
                                        return;
                                    }
                                    if (SHOW_USERS) {
                                        for (QueryDocumentSnapshot document : value) {
                                            drawUserMarker(document.toObject(Position.class));
                                        }
                                    }
                                }
                });

    }

    private ListenerRegistration setOnChangePostListener () {
        return (ListenerRegistration) db.collection("posts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        if (SHOW_USERS) {
                            for (QueryDocumentSnapshot document : value) {
                                drawPostMarker(document.toObject(Post.class), document.getId());
                            }
                        }
                    }
                });

    }


    private void drawUserMarker (Position position) {
        if (hashMapUsers.containsKey(position.getUserId())) {
            Marker oldMarker = hashMapMarkerUsers.get(position.getUserId());
            if (oldMarker != null) {
                markerIDtoUserID.remove(oldMarker.getId());
                oldMarker.remove();
            }

            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(position.getLat(), position.getLon()))
                    .title(hashMapUsers.get(position.getUserId()).getUsername())
                    .snippet(hashMapUsers.get(position.getUserId()).getFullName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_person_pin_black_24dp)));
            markerIDtoUserID.put(m.getId(), position.getUserId());
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

    private void drawPostMarker (Post post, String id) {
        Marker oldMarker = hashMapMarkerPosts.get(id);
        if (oldMarker != null) {
            markerIDtoPostID.remove(oldMarker.getId());
            oldMarker.remove();
        }

        Marker m = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(post.getLatitude(), post.getLongitude()))
                .title(post.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        markerIDtoPostID.put(m.getId(), id);
        hashMapMarkerPosts.put(id, m);
    }

    private void setShowUsers (boolean show) {
        if (show) {
            onChangePositionListener = setOnChangePositionsListener();
            SHOW_USERS = true;
        } else {
            SHOW_USERS = false;
            onChangePositionListener.remove();
            for (Map.Entry<String, Marker> entry : hashMapMarkerUsers.entrySet()) {
                Marker m = entry.getValue();
                if (m != null)
                    m.remove();
            }
        }
    }

    private void setShowPosts (boolean show) {
        if (show) {
            onChangePostsListener = setOnChangePostListener();
            SHOW_POSTS = true;
        } else {
            SHOW_POSTS = false;
            onChangePostsListener.remove();
            for (Map.Entry<String, Marker> entry : hashMapMarkerPosts.entrySet()) {
                Marker m = entry.getValue();
                if (m != null)
                    m.remove();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_users:
                setShowUsers(!SHOW_USERS);
                if (SHOW_USERS) {
                    item.setTitle(R.string.users_on);
                } else {
                    item.setTitle(R.string.users_off);
                }
                return true;
            case R.id.menu_item_posts:
                setShowPosts(!SHOW_POSTS);
                if (SHOW_POSTS) {
                    item.setTitle(R.string.posts_on);
                } else {
                    item.setTitle(R.string.posts_off);
                }
                return true;
        }
        return false;
    }
//
//    @Override
//    public boolean onMarkerClick(final Marker marker) {
//        String markerID = marker.getId();
//        String postID = markerIDtoPostID.get(markerID);
//        if (postID != null) {
//            Intent i = new Intent(getActivity(), SettingsActivity.class);
//            startActivity(i);
//
//            return false;
//        }
//
//        return true;
//    }
}
