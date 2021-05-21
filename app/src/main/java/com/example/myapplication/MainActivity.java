package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.adapter.TaskAdapter;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.ProfileFragment;
import com.example.myapplication.fragment.SetingFragment;
import com.example.myapplication.model.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar myToolbar;
    String token;
    TextView headerUsername;
    TextView headerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.navOpen, R.string.navClose);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();


        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        token = prefs.getString("token", "No name defined");

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigation(item);
                return true;
            }
        });

        headerUsername = navigationView.getHeaderView(0).findViewById(R.id.drawer_header_username);
        headerEmail = navigationView.getHeaderView(0).findViewById(R.id.drawer_header_email);

        showFragment(new HomeFragment());
    }

    @Override
    protected void onStart() {
        super.onStart();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + "user", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    headerUsername.setText("Hi! " + response.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    headerEmail.setText(response.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json; charset=UTF-8");
                header.put("Authorization", "Bearer " + token);
                return header;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void handleNavigation(MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            showFragment(new HomeFragment());
            myToolbar.setTitle(R.string.toolBarHome);
        } else if (item.getItemId() == R.id.nav_logout) {
            showTask(this);
        }
        drawerLayout.closeDrawers();
    };



    private void showTask(MainActivity mainActivity) {
        AlertDialog logoutDialog = new MaterialAlertDialogBuilder(this).setCancelable(false).setView(R.layout.alert_logout_dialog).create();
        logoutDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + "logout", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intent = new Intent(mainActivity, AuthActivity.class);
                startActivity(intent);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor =
                        mainActivity.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE).edit();
                editor.putString("token", "No name defined");
                editor.apply();
                logoutDialog.dismiss();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Accept", "application/json");
                header.put("Content-Type", "application/json; charset=UTF-8");
                header.put("Authorization", "Bearer " + token);

                return header;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    ;
}