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

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

public class TrainingActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_training);
        initListeners(); //enables button functions
    }

    @SuppressLint({
            "SdCardPath",
            "SetJavaScriptEnabled"
    })
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initListeners() {
        ImageView runButton, backButton, uploadButton, retrainButton;
        backButton = findViewById(R.id.back);
        uploadButton = findViewById(R.id.upload);
        runButton = findViewById(R.id.run);
        retrainButton = findViewById(R.id.retrain_button);
        retrainButton.setOnClickListener(v -> {
            Log.i("FORM", "retrain button pressed.");
            Intent intent = new Intent(TrainingActivity.this,
                    TestingActivity.class); //accessing ann screen
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Continue?");
            builder.setMessage("Are you sure you want to continue training?\nPress 'No' to start testing\nPress 'Yes' to keep training");
            builder.setPositiveButton("No",
                    (dialog, which) -> startActivity(intent));
            Intent retrain = new Intent(TrainingActivity.this,
                    TrainingActivity.class);
            builder.setNegativeButton("Yes", (dialog, which) -> startActivity(retrain));
            AlertDialog dialog = builder.create();

            dialog.show();
        });
        backButton.setOnClickListener(v -> {
            Log.i("FORM", "back button pressed.");
            Intent intent = new Intent(TrainingActivity.this,
                    AnnActivity.class); //accessing testing screen
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
        boolean Stop;
        float MinTrainError;
        int n;

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

        Log.i("FORM", "run training button pressed.");
        sda.fpt2 = new File("file:///android_asset/EFile");
        sda.fpt3 = new File("file:///android_asset/WFile");
        sda.fpt5 = new File("file:///android_asset/WtFile");
        sda.fpt7 = new File("file:///android_asset/DWFile");
        sda.fpt2o = new FileWriter(sda.fpt2);
        sda.fpt3o = new FileWriter(sda.fpt3);
        sda.fpt5o = new FileWriter(sda.fpt5);
        // System.out.println("Choice 1");
        Stop = false;
        MinTrainError = sda.MAX_REAL;
        sda.RandomWeights();
        System.out.println("\n************Running Back Propagation Network****************\n");
        System.out.println("\nEnter the training set file name : traindata.txt");
        // @ read 'inputfile'
        n = 1;

        do {
            sda.TrainNet(n);
            System.out.println(sda.TrainError + "\t" + MinTrainError + "\t" + sda.MinErr);

            if (sda.TrainError < MinTrainError) {
                sda.SaveWeights();
                MinTrainError = sda.TrainError;
                if (sda.TrainError < sda.MinErr) {
                    sda.RestoreWeights();
                    Stop = true;
                }
            } else if ((sda.TrainError > (1.5 * MinTrainError)) || (sda.TrainError < sda.MinErr)) {
                sda.RestoreWeights();
                Stop = true;
            }
            n++;
            if (n == 400) // no of iterations for efile ?? why 30 check and change accordingly
                Stop = true;
        } while (!Stop);
        sda.WriteWeights();
        sda.TestNet();

        sda.fpt2o.close();
        sda.fpt3o.close();
        sda.fpt5o.close();
    }

}