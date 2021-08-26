package com.example.inverseai;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class AnnActivity extends AppCompatActivity {

    //nn values
    static nnet.Sda sda;
    static String FEATURES = null;
    static String FLAGS = null;
    static String MID_NODES1 = null;
    static String MID_NODES2 = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //removes application title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //removes notification bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ann);
        initListeners(); //enables button functions
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initListeners() {
        ImageView training, backButton;
        EditText inputNode, outputNode, hl1Node, hl2Node;
        inputNode = findViewById(R.id.input_node_field);
        outputNode = findViewById(R.id.output_node_field);
        hl1Node = findViewById(R.id.h_l1_field);
        hl2Node = findViewById(R.id.h_l2_field);
        training = findViewById(R.id.training);
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            Log.i("FORM", "back button pressed.");
            Intent intent = new Intent(AnnActivity.this,
                    HomeActivity.class); //accessing home screen
            startActivity(intent);
        });
        training.setOnClickListener(v -> {
            Log.i("FORM", "save button pressed.");
            FEATURES = inputNode.getText().toString();
            FLAGS = outputNode.getText().toString();
            MID_NODES1 = hl1Node.getText().toString();
            MID_NODES2 = hl2Node.getText().toString();
            if (!FEATURES.isEmpty()) {
                nnet.Sda.FEATURES = Integer.parseInt(FEATURES);
            }
            if (!FLAGS.isEmpty()) {
                nnet.Sda.FLAGS = Integer.parseInt(FLAGS);
            }
            if (!MID_NODES1.isEmpty()) {
                nnet.Sda.MID_NODES1 = Integer.parseInt(MID_NODES1);
            }
            if (!MID_NODES2.isEmpty()) {
                nnet.Sda.MID_NODES2 = Integer.parseInt(MID_NODES2);
            }
            if (!FEATURES.isEmpty() && !FLAGS.isEmpty()) {
                nnet.Sda.NUM_LAYERS = nnet.Sda.FEATURES + nnet.Sda.FLAGS;
            }
            try {
                if (AnnActivity.FLAGS.isEmpty()) {
                    nnet.Sda.FEATURES = 2;
                    Log.i("FEATURES", "DEFAULT");
                }
                if (AnnActivity.FLAGS.isEmpty()) {
                    nnet.Sda.FLAGS = 1;
                    Log.i("FLAGS", "DEFAULT");
                }
                if (AnnActivity.MID_NODES1.isEmpty()) {
                    nnet.Sda.MID_NODES1 = 15;
                    Log.i("MID_NODES1", "DEFAULT");
                }
                if (AnnActivity.MID_NODES2.isEmpty()) {
                    nnet.Sda.MID_NODES2 = 4;
                    Log.i("MID_NODES2", "DEFAULT");
                }
                if (AnnActivity.FLAGS.isEmpty() && AnnActivity.FEATURES.isEmpty()) {
                    nnet.Sda.NUM_LAYERS = 3;
                    Log.i("NUM_LAYERS", "DEFAULT");
                }
                sda = new nnet.Sda();
                sda.GenerateNetwork();
                Log.i("FEATURES", FEATURES);
                Log.i("FLAGS", FLAGS);
                Log.i("MID_NODES1", MID_NODES1);
                Log.i("MID_NODES2", MID_NODES2);
                Log.i("TEST", "NN generated successfully.");

                Intent intent = new Intent(AnnActivity.this,
                        TrainingActivity.class); //accessing training screen

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Warning!");
                builder.setMessage("Some values are not specified. Are you sure you want to use " +
                        "default values?");
                builder.setPositiveButton("Confirm",
                        (dialog, which) -> startActivity(intent));
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> Log.i("TEST", "canceled."));
                AlertDialog dialog = builder.create();

                if (AnnActivity.FEATURES.isEmpty() || AnnActivity.FLAGS.isEmpty() ||
                        AnnActivity.MID_NODES1.isEmpty() || AnnActivity.MID_NODES2.isEmpty()) {
                    dialog.show();
                }
            } catch (Exception e) {
                Log.i("ERROR", "failed.");
            }
            if (!AnnActivity.FEATURES.isEmpty() && !AnnActivity.FLAGS.isEmpty() &&
                    !AnnActivity.MID_NODES1.isEmpty() && !AnnActivity.MID_NODES2.isEmpty()) {
                {
                    Intent intent = new Intent(AnnActivity.this,
                            TrainingActivity.class); //accessing training screen
                    startActivity(intent);
                }
            }
        });
    }
}