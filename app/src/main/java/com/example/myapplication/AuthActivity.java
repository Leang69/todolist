package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.fragment.LoginFragment;
import com.example.myapplication.fragment.SkipLoginFragment;

public class AuthActivity extends AppCompatActivity {
    String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        token = prefs.getString("token", "No name defined");

        if (!token.equals("No name defined")){
            showFragment(new SkipLoginFragment());
        }else {
            showFragment(new LoginFragment());
        }
        setContentView(R.layout.activity_auth);

    }

    private void showFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_auth_container,fragment);
        fragmentTransaction.commit();
    };
}
