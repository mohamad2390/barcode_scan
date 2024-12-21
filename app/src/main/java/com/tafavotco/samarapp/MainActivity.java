package com.tafavotco.samarapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.tafavotco.samarapp.ui.ActivityListFragment;
import com.tafavotco.samarapp.ui.HomeFragment;
import com.tafavotco.samarapp.ui.Login;
import com.tafavotco.samarapp.ui.ScanBarCodeFragment;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    PreferencesHelper preferencesHelper;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();

//        NavigationView navigationView = findViewById(R.id.nav_view);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar , R.string.open_nav , R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        if (savedInstanceState == null){
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new HomeFragment()).commit();
//        }

        replaceFragment(new HomeFragment());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_registration) {
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
                    Toast.makeText(MainActivity.this, "هنوز درست نشده", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_about_us) {
                    Toast.makeText(MainActivity.this, "هنوز درست نشده", Toast.LENGTH_SHORT).show();
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
    }

}