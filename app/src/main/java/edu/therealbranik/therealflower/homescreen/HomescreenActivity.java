package edu.therealbranik.therealflower.homescreen;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.homescreen.explore.ExploreFragment;
import edu.therealbranik.therealflower.homescreen.home.HomeFragment;
import edu.therealbranik.therealflower.homescreen.profile.ProfileFragment;
import edu.therealbranik.therealflower.homescreen.social.SocialFragment;
import edu.therealbranik.therealflower.login_register.LoginActivity;
import edu.therealbranik.therealflower.post.AddPostActivity;
import edu.therealbranik.therealflower.settings.SettingsActivity;
import edu.therealbranik.therealflower.user.LocationTrackingService;

public class HomescreenActivity extends AppCompatActivity {

    public static final String BROADCAST_ON_CHANGE_LOCATION = ".homescreen.OnLocationChangeReceiver";

    private FirebaseAuth mAuth;

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
                    return true;
                case R.id.navigation_social:
                    fm.beginTransaction().hide(activeTab).show(fragmentSocial).commit();
                    activeTab = fragmentSocial;
                    setTitle(R.string.title_social);
                    return true;
                case R.id.navigation_explore:
                    fm.beginTransaction().hide(activeTab).show(fragmentExplore).commit();
                    activeTab = fragmentExplore;
                    setTitle(R.string.title_explore);
                    return true;
                case R.id.navigation_profile:
                    fm.beginTransaction().hide(activeTab).show(fragmentProfile).commit();
                    activeTab = fragmentProfile;
                    setTitle(R.string.title_profile);
                    return true;
            }
            return false;
        }
    };

    private NavigationView.OnNavigationItemSelectedListener mOnDrawerNavigationItemSelectedListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_item_signout: {
                            mAuth.signOut();
                            Intent i = new Intent(HomescreenActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                        case R.id.nav_settings:
                            Intent i = new Intent(HomescreenActivity.this, SettingsActivity.class);
                            startActivity(i);
                    }
                    return false;
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

        startService(new Intent(this, LocationTrackingService.class));

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ON_CHANGE_LOCATION);
        registerReceiver( new OnLocationChangeReceiver() , intentFilter);
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

        return super.onOptionsItemSelected(item);
    }

}
