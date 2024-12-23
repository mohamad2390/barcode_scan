package com.tafavotco.samarapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.ui.AboutUsFragment;
import com.tafavotco.samarapp.ui.ActivityListFragment;
import com.tafavotco.samarapp.ui.HomeFragment;
import com.tafavotco.samarapp.ui.InquiryFragment;
import com.tafavotco.samarapp.ui.Login;
import com.tafavotco.samarapp.ui.ScanBarCodeFragment;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    PreferencesHelper preferencesHelper;
    NavigationView navigationView;
    TextView event_title , headerNavPhone ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar , R.string.open_nav , R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        event_title.setText(preferencesHelper.getEventTitle());

        View headerView = navigationView.getHeaderView(0);

        headerNavPhone = headerView.findViewById(R.id.header_nav_phone);
        headerNavPhone.setText(preferencesHelper.getUsername());

        replaceFragment(new HomeFragment());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_registration) {
                    preferencesHelper.setActivityTitle("");
                    Fragment fragment = new ScanBarCodeFragment();
                    Bundle bundle = new Bundle();
                    Bundle bundle_method = new Bundle();
                    Bundle bundle_activity_id = new Bundle();
                    bundle_method.putString("method", "registration");
                    bundle_activity_id.putString("activity_id", "");
                    bundle.putBundle("bundle_method",bundle_method);
                    bundle.putBundle("bundle_activity_id",bundle_activity_id);
                    fragment.setArguments(bundle);
                    replaceFragment(fragment);
                } else if (id == R.id.nav_checkIn) {
                    preferencesHelper.setActivityTitle("");
                    Fragment fragment = new ScanBarCodeFragment();
                    Bundle bundle = new Bundle();
                    Bundle bundle_method = new Bundle();
                    Bundle bundle_activity_id = new Bundle();
                    bundle_method.putString("method", "checkIn");
                    bundle_activity_id.putString("activity_id", "");
                    bundle.putBundle("bundle_method",bundle_method);
                    bundle.putBundle("bundle_activity_id",bundle_activity_id);
                    fragment.setArguments(bundle);
                    replaceFragment(fragment);
                } else if (id == R.id.nav_checkOut) {
                    preferencesHelper.setActivityTitle("");
                    Fragment fragment = new ScanBarCodeFragment();
                    Bundle bundle = new Bundle();
                    Bundle bundle_method = new Bundle();
                    Bundle bundle_activity_id = new Bundle();
                    bundle_method.putString("method", "checkOut");
                    bundle_activity_id.putString("activity_id", "");
                    bundle.putBundle("bundle_method",bundle_method);
                    bundle.putBundle("bundle_activity_id",bundle_activity_id);
                    fragment.setArguments(bundle);
                    replaceFragment(fragment);
                } else if (id == R.id.nav_get_activities) {
                    Fragment fragment = new ActivityListFragment();
                    replaceFragment(fragment);
                } else if (id == R.id.nav_inquiry) {
                    preferencesHelper.setActivityTitle("");
                    Fragment fragment = new InquiryFragment();
                    replaceFragment(fragment);
                } else if (id == R.id.nav_about_us) {
                    preferencesHelper.setActivityTitle("");
                    Fragment fragment = new AboutUsFragment();
                    replaceFragment(fragment);
                } else if (id == R.id.nav_logout) {
                    preferencesHelper.setToken(null);
                    Intent myIntent = new Intent(MainActivity.this , Login.class);
                    startActivity(myIntent);
                    finish();
                }

                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });


    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void init(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        preferencesHelper = new PreferencesHelper(MainActivity.this);
        event_title = findViewById(R.id.txt_event_title_global);
//        activity_title = findViewById(R.id.txt_activity_title_global);
//        header_nav_phone = findViewById(R.id.header_navigation_drawer.header_nav_phone);
    }

}