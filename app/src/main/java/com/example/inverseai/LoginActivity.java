package com.example.inverseai;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

public class LoginActivity extends AppCompatActivity {
    private User user;
    private final ServiceConnection connection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("LOGIN", "UserService discovered.");
            UserService.UserServiceBinder binder = (UserService.UserServiceBinder) service;
            UserService userService = binder.getService(); //register this user to the UserService
            userService.registerUser(user);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //removes application title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //removes notification bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initListeners(); //enables button functions
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initListeners() {
        EditText usernameField, passwordField;
        ImageView backButton, nextButton;
        usernameField = findViewById(R.id.username_field);
        passwordField = findViewById(R.id.password_field);
        backButton = findViewById(R.id.back);
        nextButton = findViewById(R.id.next_button);
        usernameField.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(usernameField, InputMethodManager.SHOW_IMPLICIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        backButton.setOnClickListener(v -> {
            Log.i("FORM", "back button pressed.");
            Intent intent = new Intent(LoginActivity.this,
                    MainActivity.class); //accessing main screen
            startActivity(intent);
        });
        nextButton.setOnClickListener(view -> {
            String usernameValue = usernameField.getText().toString();
            usernameField.getText().clear();
            String passwordValue = passwordField.getText().toString();
            passwordField.getText().clear();
            try {
                String UserString = User.retrieveUser(getApplicationContext(), usernameValue);
                Log.i("LOGIN", "user found.");
                user = new User(UserString);
                if (passwordValue.equals(user.getPassword())) {
                    Intent userServiceIntent = new Intent(LoginActivity.this,
                            UserService.class);
                    bindService(userServiceIntent, connection, Context.BIND_AUTO_CREATE);
                    Intent intent = new Intent(LoginActivity.this,
                            HomeActivity.class); //accessing home screen
                    startActivity(intent);
                } else {
                    Log.i("LOGIN", "wrong password.");
                }
            } catch (FileNotFoundException e) {
                Log.i("LOGIN", "user not found.");
            }
        });
    }
}