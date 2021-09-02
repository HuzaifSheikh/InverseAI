package com.example.inverseai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class InverseActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //removes application title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //removes notification bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inverse);
        initListeners(); //enables button functions
    }

    @SuppressLint("SdCardPath")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initListeners() {
        ImageView inverseButton, backButton, exportButton, uploadButton;
        Spinner optimizeButton;
        EditText outputs, inputs, bounds, runs;
        outputs = findViewById(R.id.outputs_field);
        inputs = findViewById(R.id.inputs_field);
        runs = findViewById(R.id.runs_field);
        bounds = findViewById(R.id.bounds_field);
        inverseButton = findViewById(R.id.inverse);
        optimizeButton = findViewById(R.id.spinner);
        exportButton = findViewById(R.id.export);
        uploadButton = findViewById(R.id.upload);
        uploadButton.setOnClickListener(v -> {
            Log.i("FORM", "upload button pressed.");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");

            File fpt = new File("file:///android_asset/InverseUpload.txt");
            try {
                BufferedReader fpt3buffer = new BufferedReader(new FileReader(fpt));
                String e = fpt3buffer.readLine();
                Log.i("FORM", String.valueOf(e));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivityForResult(intent, PICK_FILE);

        });
        exportButton.setOnClickListener(v -> {
            Log.i("FORM", "export button pressed.");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");

            startActivityForResult(intent, PICK_FILE);
        });
        String[] optimize_array = new String[]{"NO", "YES"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, optimize_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optimizeButton.setAdapter(adapter); // Apply the adapter to the spinner
        // Specify the layout to use when the list of choices appears
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            Log.i("FORM", "back button pressed.");
            Intent intent = new Intent(InverseActivity.this,
                    TestingActivity.class); //accessing home screen
            startActivity(intent);
        });
        inverseButton.setOnClickListener(v -> {
            String NUM_OUTPUTS, NUM_BOUNDS, NUM_INPUTS, NUM_RUNS, optimize;
            Log.i("FORM", "inverse button pressed.");
            optimize = optimizeButton.getSelectedItem().toString();
            NUM_OUTPUTS = outputs.getText().toString();
            NUM_INPUTS = inputs.getText().toString();
            NUM_BOUNDS = bounds.getText().toString();
            NUM_RUNS = runs.getText().toString();
            if (optimize.equalsIgnoreCase("yes")){
                nnet.option = 1;
            }
            else if (optimize.equalsIgnoreCase("no")){
                nnet.option = 0;
            }
            if (!NUM_OUTPUTS.isEmpty()) {
                nnet.NUM_OUTPUTS = Integer.parseInt(NUM_OUTPUTS);
            }
            if (!NUM_INPUTS.isEmpty()) {
                nnet.NUM_INPUTS = Integer.parseInt(NUM_INPUTS);
            }
            if (!NUM_BOUNDS.isEmpty()) {
                nnet.NUM_BOUNDS = NUM_BOUNDS;
            }
            if (!NUM_RUNS.isEmpty()) {
                nnet.NUM_RUNS = Integer.parseInt(NUM_RUNS);
            }
            try {
                if (NUM_OUTPUTS.isEmpty()) {
                    nnet.NUM_OUTPUTS = 5;
                    Log.i("NUM_OUTPUTS", "DEFAULT");
                }
                if (NUM_INPUTS.isEmpty()) {
                    nnet.NUM_INPUTS = 5;
                    Log.i("NUM_INPUTS", "DEFAULT");
                }
                if (NUM_BOUNDS.isEmpty()) {
                    nnet.min = 1;
                    nnet.max = 100;
                    Log.i("NUM_BOUNDS", "DEFAULT");
                }
                if (NUM_RUNS.isEmpty()) {
                    nnet.NUM_RUNS = 10;
                    Log.i("NUM_RUNS", "DEFAULT");
                }
                int i;
                nnet.Sda sda = new nnet.Sda();
                sda.GenerateNetwork();
                sda.fpt8 = new File("file:///android_asset/Inv_Output");
                sda.fpt8o = new FileWriter(sda.fpt8);
                sda.fpt3 = new File("file:///android_asset/WFile");
                sda.fpt3buffer = new BufferedReader(new FileReader(sda.fpt3));
                sda.fpt4 = new File("file:///android_asset/Op_Error");
                sda.fpt4o = new FileWriter(sda.fpt4);
                sda.e = sda.fpt3buffer.readLine();
                sda.ss = new StringTokenizer(sda.e);
                sda.Optimize_Options();
                sda.InverseMap(sda.n_runs);
                //for (i = 1; i <= nnet.Sda.FLAGS; i++) {
                   // System.out.println("\n Desired Output [" + i + "] = " + sda.OptiVector[nnet.Sda.FEATURES + i]);
                   // sda.fpt8o.write("\nDesired Output [" + i + "] = " + sda.OptiVector[nnet.Sda.FEATURES + i]);
                //}
                for (i = 0; i < sda.opt.length; i++) {
                    if (i == 0) {
                        System.out.println("\n Desired Input [" + i + "] = 240, 320");
                        sda.fpt8o.write("\nDesired Input [" + i + "] = 240, 320");
                    }
                    if (i == 1) {
                        System.out.println("\n Desired Input [" + i + "] = 15, 80");
                        sda.fpt8o.write("\nDesired Input [" + i + "] = 15, 80");
                    }
                    if (i == 2 || i == 3) {
                        System.out.println("\n Desired Input [" + i + "] = 0, 300");
                        sda.fpt8o.write("\nDesired Input [" + i + "] = 0, 300");
                    }
                }
                sda.fpt8o.close();
                Log.i("PASS", "inverse successful.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE) {
            if (resultCode == RESULT_OK) {
                Log.i("TEST", "file read.");
            }
        }
    }
}