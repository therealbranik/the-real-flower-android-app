package edu.therealbranik.therealflower.homescreen.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import edu.therealbranik.therealflower.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        ListView listView=view.findViewById(R.id.list_view_home);
        CardsAdapter adapter = new CardsAdapter(getContext());

        listView.setAdapter(adapter);
        //TO DO: ADD POST's FROM DATABASE
        adapter.addAll();

        return view;
    }

}
