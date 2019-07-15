package edu.therealbranik.therealflower.homescreen;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.homescreen.explore.ExploreFragment;
import edu.therealbranik.therealflower.homescreen.home.HomeFragment;
import edu.therealbranik.therealflower.homescreen.profile.ProfileFragment;
import edu.therealbranik.therealflower.homescreen.social.SocialFragment;
import edu.therealbranik.therealflower.homescreen.social.friends.FriendsFragment;
import edu.therealbranik.therealflower.login_register.LoginActivity;
import edu.therealbranik.therealflower.post.AddPostActivity;
import edu.therealbranik.therealflower.ranking.RankingActivity;
import edu.therealbranik.therealflower.settings.SettingsActivity;
import edu.therealbranik.therealflower.user.FriendRequestReceiver;
import edu.therealbranik.therealflower.user.LocationTrackingService;
import edu.therealbranik.therealflower.user.User;
import edu.therealbranik.therealflower.user.UserProfileActivity;
import edu.therealbranik.therealflower.utility.Permissons;

public class HomescreenActivity extends AppCompatActivity implements FriendsFragment.OnListFragmentInteractionListener {

    public static final String BROADCAST_ON_CHANGE_LOCATION = ".homescreen.OnLocationChangeReceiver";
    public static final String BROADCAST_ON_FRIEND_REQUEST = ".homescreen.OnFriendRequestReceiver";

    private FirebaseAuth mAuth;

    BroadcastReceiver receiverOnChangePosition = null;
    BroadcastReceiver receiverOnFriendRequest = null;

    final Fragment fragmentHome = new HomeFragment();
    final Fragment fragmentSocial = new SocialFragment();
    final Fragment fragmentExplore = new ExploreFragment();
    final Fragment fragmentProfile = new ProfileFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment activeTab = fragmentHome;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(activeTab).show(fragmentHome).commit();
                    activeTab = fragmentHome;
                    setTitle(R.string.title_home);
                    toolbar.getMenu().clear();
                    return true;
                case R.id.navigation_social:
                    fm.beginTransaction().hide(activeTab).show(fragmentSocial).commit();
                    activeTab = fragmentSocial;
                    setTitle(R.string.title_social);
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.fragment_social_menu);
                    return true;
                case R.id.navigation_explore:
                    fm.beginTransaction().hide(activeTab).show(fragmentExplore).commit();
                    activeTab = fragmentExplore;
                    setTitle(R.string.title_explore);
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.fragment_explore_menu);
                    return true;
                case R.id.navigation_profile:
                    fm.beginTransaction().hide(activeTab).show(fragmentProfile).commit();
                    activeTab = fragmentProfile;
                    setTitle(R.string.title_profile);
                    toolbar.getMenu().clear();
                    return true;
            }
            return false;
        }
    };

    private NavigationView.OnNavigationItemSelectedListener mOnDrawerNavigationItemSelectedListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent i;
                    switch (item.getItemId()) {
                        case R.id.nav_item_signout: {
                            mAuth.signOut();
                            i = new Intent(HomescreenActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                            break;
                        }
                        case R.id.nav_settings:
                            i = new Intent(HomescreenActivity.this, SettingsActivity.class);
                            startActivity(i);
                            break;
                        case R.id.nav_ranking:
                            i =new Intent(HomescreenActivity.this, RankingActivity.class);
                            startActivity(i);
                            break;
                    }



                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        fm.beginTransaction().add(R.id.homescreen_tabs_container, fragmentProfile, "3").hide(fragmentProfile).commit();
        fm.beginTransaction().add(R.id.homescreen_tabs_container, fragmentExplore, "3").hide(fragmentExplore).commit();
        fm.beginTransaction().add(R.id.homescreen_tabs_container, fragmentSocial, "2").hide(fragmentSocial).commit();
        fm.beginTransaction().add(R.id.homescreen_tabs_container,fragmentHome, "1").commit();

        mAuth = FirebaseAuth.getInstance();
        BottomNavigationView navView = (BottomNavigationView) findViewById(R.id.bottom_nav_view);
        NavigationView navViewDrawer = (NavigationView) findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navViewDrawer.setNavigationItemSelectedListener(mOnDrawerNavigationItemSelectedListener);
        toolbar = (Toolbar) findViewById(R.id.toolbar_homescreen);
        toolbar.setNavigationIcon(R.drawable.baseline_menu_24);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        fab = (FloatingActionButton) findViewById(R.id.fab_add_post);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomescreenActivity.this, AddPostActivity.class);
                startActivity(i);
            }
        });

        if (!Permissons.Check_FINE_LOCATION(this) && !Permissons.Check_COARSE_LOCATION(this)) {
            Permissons.Request_FINE_LOCATION(this, 1256);
            Permissons.Request_COARSE_LOCATION(this, 1257);
        } else {
            startLocationService();
        }


//        Intent friendService = new Intent(this, FriendRequestService.class);
//        startService(friendService);

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ON_CHANGE_LOCATION);
        receiverOnChangePosition = new OnLocationChangeReceiver();
        registerReceiver( receiverOnChangePosition , intentFilter);
        IntentFilter intentFilterFriendRequest = new IntentFilter(FriendRequestReceiver.BROADCAST_FRIEND_REQUEST);
        receiverOnFriendRequest = new FriendRequestReceiver();
        registerReceiver(receiverOnFriendRequest, intentFilterFriendRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1256 && resultCode == RESULT_OK) {
            startLocationService();
        } else if (requestCode == 1257 && resultCode == RESULT_OK) {
            startLocationService();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;

        if (item.getItemId() == R.id.menu_item_users || item.getItemId() == R.id.menu_item_posts) {
            fragmentExplore.onOptionsItemSelected(item);
        } else if (item.getItemId() == R.id.menu_item_bt_discovery) {
            //TODO
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListFragmentInteraction(String item) {
        Intent i = new Intent(this, UserProfileActivity.class);
        i.putExtra("id", item);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
//        unregisterReceiver(receiverOnChangePosition);
        unregisterReceiver(receiverOnFriendRequest);
        super.onDestroy();
    }

    private void startLocationService () {

        startService(new Intent(HomescreenActivity.this, LocationTrackingService.class));

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(new Intent(HomescreenActivity.this, LocationTrackingService.class));
//        } else {
//            startService(new Intent(HomescreenActivity.this, LocationTrackingService.class));
//        }
    }
}
