package com.example.inverseai;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //removes application title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //removes notification bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        initListeners(); //enables button functions
    }

    protected void initListeners() {
        ImageView logout;
        logout = findViewById(R.id.logout_button);
        logout.setOnClickListener(v -> {
            Log.i("FORM", "logout button pressed.");
            Intent userServiceIntent = new Intent(this, UserService.class);
            bindService(userServiceIntent, connection, Context.BIND_AUTO_CREATE);
            Intent intent = new Intent(HomeActivity.this,
                    MainActivity.class); //accessing login screen
            startActivity(intent);
            Log.i("LOGIN", "user logged out.");
        });
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("FORM", "UserService discovered.");
            UserService.UserServiceBinder binder = (UserService.UserServiceBinder) service;
            UserService userService = binder.getService();
            userService.deregisterUser(); // de-register this user
            Intent intent = new Intent(HomeActivity.this, User.class);
            startActivity(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };
}