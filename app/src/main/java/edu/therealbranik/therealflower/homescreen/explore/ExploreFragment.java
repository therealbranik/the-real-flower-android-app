package edu.therealbranik.therealflower.homescreen.explore;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;

import edu.therealbranik.therealflower.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    GoogleMap mMap;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

}
