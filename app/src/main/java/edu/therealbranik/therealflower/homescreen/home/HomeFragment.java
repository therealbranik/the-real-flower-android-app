package edu.therealbranik.therealflower.homescreen.home;


import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import edu.therealbranik.therealflower.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ListView listView;
    CardView cardView;
    ImageView profile_img,post_img1,post_img2;
    TextView name,decription,timestamp;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        listView=view.findViewById(R.id.list_view_home);
        cardView=view.findViewById(R.id.card_view);

        CardsAdapte

        return view;
    }

}
