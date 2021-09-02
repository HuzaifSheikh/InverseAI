package com.example.inverseai;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

public class TestingActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;

    @SuppressLint({
            "SetJavaScriptEnabled",
            "SdCardPath"
    })
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //removes application title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //removes notification bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_testing);
        initListeners(); //enables button functions
    }

    @SuppressLint({
            "SdCardPath",
            "SetJavaScriptEnabled"
    })
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initListeners() {
        ImageView runButton, backButton, uploadButton, retestButton;
        backButton = findViewById(R.id.back);
        uploadButton = findViewById(R.id.upload);
        runButton = findViewById(R.id.run);
        retestButton = findViewById(R.id.retest_button);
        retestButton.setOnClickListener(v -> {
            Log.i("FORM", "retest button pressed.");
            Intent intent = new Intent(TestingActivity.this,
                    InverseActivity.class); //accessing ann screen
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Continue?");
            builder.setMessage("Are you sure you want to continue testing?" +
                    "\nPress 'No' to start inverse\nPress 'Yes' to keep training");
            builder.setPositiveButton("No",
                    (dialog, which) -> startActivity(intent));
            Intent retest = new Intent(TestingActivity.this,
                    TestingActivity.class);
            builder.setNegativeButton("Yes", (dialog, which) -> startActivity(retest));
            AlertDialog dialog = builder.create();

            dialog.show();
        });
        backButton.setOnClickListener(v -> {
            Log.i("FORM", "back button pressed.");
            Intent intent = new Intent(TestingActivity.this,
                    TrainingActivity.class); //accessing testing screen
            startActivity(intent);
        });
        runButton.setOnClickListener(v -> {
            Log.i("FORM", "run button pressed.");
            try {
                this.onClick(runButton);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("FORM", "use a different file.");
            }
        });
        uploadButton.setOnClickListener(v -> {
            Log.i("FORM", "upload button pressed.");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");

            startActivityForResult(intent, PICK_FILE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE) {
            if (resultCode == RESULT_OK) {
                // Uri uri = data.getData();
                // String fileContent = readTextFile(uri);
                Log.i("TEST", "file read.");
            }
        }
    }

    @SuppressLint({
            "SdCardPath",
            "SetJavaScriptEnabled"
    })
    public void onClick(View v) throws Exception {

        WebView ErrorView = findViewById(R.id.ErrorView);
        ErrorView.clearView();
        ErrorView.setWebChromeClient(new WebChromeClient());
        ErrorView.setInitialScale(200);
        ErrorView.getSettings().setJavaScriptEnabled(true);
        ErrorView.getSettings().setAllowContentAccess(true);
        ErrorView.getSettings().setAllowFileAccess(true);
        ErrorView.getSettings().setDomStorageEnabled(true);
        ErrorView.loadUrl("file:///android_asset/ErrorGraph.htm");
        ErrorView.setVisibility(View.VISIBLE);

        WebView AccuracyView = findViewById(R.id.AccuracyView);
        AccuracyView.clearView();
        AccuracyView.setWebChromeClient(new WebChromeClient());
        AccuracyView.setInitialScale(200);
        AccuracyView.getSettings().setJavaScriptEnabled(true);
        AccuracyView.getSettings().setAllowContentAccess(true);
        AccuracyView.getSettings().setAllowFileAccess(true);
        AccuracyView.getSettings().setDomStorageEnabled(true);
        AccuracyView.loadUrl("file:///android_asset/AccuracyGraph.htm");
        AccuracyView.setVisibility(View.VISIBLE);

        nnet.Sda sda = new nnet.Sda(); //initialize sda for training with user input

        try {
            sda.GenerateNetwork();
        } catch (Exception e) {
            Log.i("ERROR", "error");
        }
        sda.fpt1 = new File("file:///android_asset/results.txt");
        sda.fpt3 = new File("file:///android_asset/WFile");
        sda.fpt1o = new FileWriter(sda.fpt1);
        sda.fpt3buffer = new BufferedReader(new FileReader(sda.fpt3));
        sda.e = sda.fpt3buffer.readLine();
        sda.ss = new StringTokenizer(sda.e);
        System.out.println("\nEnter the testing set fileW name:testdata.txt ");
        // @ read inputfile

        System.out.println("\nEnter the results file name:results.txt ");
        // @ read inputfile

        sda.ReadWeights();
        sda.RestoreWeights();
        System.out.println("\nDo you want to disturb weights? (Y/N) :n");
        // @ read choice_dis

        sda.EvaluateNet();
        sda.fpt1o.close();
    }
}