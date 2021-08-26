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

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    User user;
    private final ServiceConnection connection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("FORM", "UserService discovered.");
            UserService.UserServiceBinder binder = (UserService.UserServiceBinder) service;
            UserService userService = binder.getService(); //register this user to the UserService
            userService.registerUser(user);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //removes application title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //removes notification bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        initListeners(); //enables button functions
    }

    protected void initListeners() {
        EditText usernameField, passwordField;
        ImageView backButton, nextButton;
        usernameField = findViewById(R.id.username_field);
        passwordField = findViewById(R.id.password_field);
        usernameField.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(usernameField, InputMethodManager.SHOW_IMPLICIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        backButton = findViewById(R.id.back);
        nextButton = findViewById(R.id.next_button);
        backButton.setOnClickListener(v -> {
            Log.i("FORM", "back button pressed.");
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class); //accessing main screen
            startActivity(intent);
        });
        nextButton.setOnClickListener(v -> {
            Log.i("FORM", "next button pressed.");
            String usernameValue = usernameField.getText().toString();
            String passwordValue = passwordField.getText().toString();
            User user = new User(usernameValue, passwordValue); //initializes a user
            Intent userServiceIntent = new Intent(this,
                    UserService.class);
            bindService(userServiceIntent, connection, Context.BIND_AUTO_CREATE);
            try {
                user.saveUser(getApplicationContext());
                Intent intent = new Intent(RegisterActivity.this,
                        HomeActivity.class); //accessing home screen
                startActivity(intent);
            } catch (IOException e) {
                Log.i("FORM", "failed to save this session's user."); //catches an exception if the form is not filled properly
            }
        });
    }
}