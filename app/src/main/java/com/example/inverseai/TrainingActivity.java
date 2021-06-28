package com.example.inverseai;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileWriter;

public class TrainingActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;

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
            builder.setTitle("Notification");
            builder.setMessage("Continue training?");
            builder.setPositiveButton("Neural Network Testing",
                    (dialog, which) -> startActivity(intent));
            builder.setNegativeButton("Yes", (dialog, which) -> Log.i("TEST", "retraining."));
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
        if (requestCode == PICK_FILE)
        {
            if (resultCode == RESULT_OK)
            {
                // Uri uri = data.getData();
                // String fileContent = readTextFile(uri);
                Log.i("TEST", "file read.");
            }
        }
    }

    @SuppressLint("SdCardPath")
    public void onClick(View v) throws Exception {
        boolean Stop;
        float MinTrainError;
        int n;

        nnet.Sda sda = new nnet.Sda(); //initialize sda for training with user input

        try {
            sda.GenerateNetwork();
        } catch (Exception e) {
            Log.i("ERROR", "error");
        }

        Log.i("FORM", "run training button pressed.");
        sda.fpt2 = new File("/data/user/0/com.example.inverseai/files/EFile");
        sda.fpt3 = new File("/data/user/0/com.example.inverseai/files/WFile");
        sda.fpt5 = new File("/data/user/0/com.example.inverseai/files/WtFile");
        sda.fpt7 = new File("/data/user/0/com.example.inverseai/files/DWFile");
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