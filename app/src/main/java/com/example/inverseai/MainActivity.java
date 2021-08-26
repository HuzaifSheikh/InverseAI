package com.example.inverseai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Huzaif
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //removes application title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //removes notification bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initListeners(); //enables button functions
    }

    protected void initListeners() {
        ImageView loginButton, registerButton;
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        loginButton.setOnClickListener(v -> {
            Log.i("FORM", "login button pressed.");
            Intent intent = new Intent(MainActivity.this,
                    LoginActivity.class); //accessing login screen
            startActivity(intent);
        });
        registerButton.setOnClickListener(v -> {
            Log.i("FORM", "register button pressed.");
            Intent intent = new Intent(MainActivity.this,
                    RegisterActivity.class); //accessing register screen
            startActivity(intent);
        });
    }
}