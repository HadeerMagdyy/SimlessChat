package com.example.simlesschat.Activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.simlesschat.BluetoothchatActivity;
import com.example.simlesschat.R;
import com.example.simlesschat.Fragments.bluetoothchats_fragment;
import com.example.simlesschat.Fragments.wifichats_fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AllchatsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView profilename;
    TextView profileimage;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allchats);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Latest Contacts ");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        profilename = (TextView) headerView.findViewById(R.id.profilename_allchatsactivity_id);
        profileimage=(TextView)headerView.findViewById(R.id.profileimage_allchatsactivity_id);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String name=sharedPreferences.getString(getString(R.string.username),"No name");

        profilename.setText(name);
        profileimage.setText(String.valueOf(name.charAt(0)).toUpperCase());


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container,new bluetoothchats_fragment()).commit();

        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i;
        if (id == R.id.editprofile_nav_id) {
             i= new Intent(AllchatsActivity.this,EditprofileActivity.class);
            startActivity(i);
        } else if (id == R.id.Connectwifi_nav_id) {

            i=new Intent(AllchatsActivity.this,wifi_connection_activity.class);
            startActivity(i);

        } else if (id == R.id.Logout_nav_id) {
            i=new Intent(AllchatsActivity.this,LoginActivity.class);
            startActivity(i);
        }
        else if(id == R.id.Connectbluetooh_nav_id)
        {

            i=new Intent(AllchatsActivity.this, BluetoothchatActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
            Fragment selected=null;

            switch (menuItem.getItemId())
            {
                case R.id.bluetooth_nav_id:
                    selected=new bluetoothchats_fragment();
                    break;

                case R.id.wifi_nav_id:
                    selected=new wifichats_fragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container,selected).commit();
            return true;
        }
    };

}
