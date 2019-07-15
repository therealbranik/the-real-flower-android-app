package edu.therealbranik.therealflower.homescreen.social;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.homescreen.social.friends.FriendsFragment;
import edu.therealbranik.therealflower.homescreen.social.nearby.NearbyFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFragment extends Fragment {

    private static final int FRIENDS_TAB = 0;
    private static final int NEARBY_TAB = 1;
    private static final int AVAILABLE_TAB = 2;

    final Fragment fragmentFriends = new FriendsFragment();
    final Fragment fragmentNearby = new NearbyFragment();

    Fragment activeTab = fragmentFriends;

    private TabLayout tabLayout;

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_social, container, false);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case FRIENDS_TAB:
                        getFragmentManager().beginTransaction().hide(activeTab).show(fragmentFriends).commit();
                        activeTab = fragmentFriends;
                        return;
                    case NEARBY_TAB:
                        getFragmentManager().beginTransaction().hide(activeTab).show(fragmentNearby).commit();
                        activeTab = fragmentNearby;
                        return;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getFragmentManager().beginTransaction().add(R.id.tabs_container_social, fragmentNearby, "2").hide(fragmentNearby).commit();
        getFragmentManager().beginTransaction().add(R.id.tabs_container_social, fragmentFriends, "1").commit();


        return rootView;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


}
