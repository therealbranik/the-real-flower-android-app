package edu.therealbranik.therealflower.homescreen;

import android.content.Intent;
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
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.login_register.LoginActivity;
import edu.therealbranik.therealflower.post.AddPostActivity;

public class HomescreenActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Toast.makeText(HomescreenActivity.this, item.getItemId()+"", Toast.LENGTH_SHORT).show();
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_social:
//                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_explore:
//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.navigation_profile:
//                    mTextMessage.setText(R.string.title_notifications);
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
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
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
