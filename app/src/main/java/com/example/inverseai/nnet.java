package com.example.inverseai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

public class nnet extends Activity {
    public static String choice = "";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void main(String[] args) throws Exception {

        char choice_dis;
        boolean Stop;
        float MinTrainError;
        int n, i;

        Sda sda = new Sda();
        sda.GenerateNetwork();

        sda.fpt2 = new File("./EFile");
        sda.fpt3 = new File("./WFile");
        sda.fpt5 = new File("./WtFile");
        sda.fpt7 = new File("./DWFile");
        sda.fpt2o = new FileWriter(sda.fpt2);
        sda.fpt3o = new FileWriter(sda.fpt3);
        sda.fpt5o = new FileWriter(sda.fpt5);

        if (choice.equalsIgnoreCase("t")) {
            sda.fpt1 = new File("./results.txt");
            sda.fpt3 = new File("./WFile");
            sda.fpt1o = new FileWriter(sda.fpt1);
            sda.fpt3buffer = new BufferedReader(new FileReader(sda.fpt3));
            sda.e = sda.fpt3buffer.readLine();
            sda.ss = new StringTokenizer(sda.e);
            System.out.println("\nEnter the testing set file name:testdata.txt ");
            // @ read inputfile

            System.out.println("\nEnter the results file name:results.txt ");
            // @ read inputfile

            sda.ReadWeights();
            sda.RestoreWeights();
            System.out.println("\nDo you want to disturb weights? (Y/N) :n");
            // @ read choice_dis
            choice_dis = 'n'; // remove this line when reading 'choice_dis'
            sda.EvaluateNet();

            if (choice_dis == 'y')
                sda.EvaluateNet();

            sda.fpt1o.close();
        } else if (choice.equalsIgnoreCase("i")) {
            sda.fpt8 = new File("./Inv_Output");
            sda.fpt8o = new FileWriter(sda.fpt8);
            sda.fpt3 = new File("./WFile");
            sda.fpt3buffer = new BufferedReader(new FileReader(sda.fpt3));
            sda.fpt4 = new File("./Op_Error");
            sda.fpt4o = new FileWriter(sda.fpt4);
            sda.e = sda.fpt3buffer.readLine();
            sda.ss = new StringTokenizer(sda.e);
            sda.Optimize_Options();
            sda.InverseMap(sda.n_runs);
            for (i = 1; i<= Sda.FLAGS; i++) {
				System.out.println("\n Desired Output [" + i + "] = " + sda.OptiVector[Sda.FEATURES + i]);
				sda.fpt8o.write("\nDesired Output [" + i + "] = " + sda.OptiVector[Sda.FEATURES + i]);
			}
            for (i = 0; i < sda.opt.length; i++) {
                if (i == 0) {
                    System.out.println("\n Desired Input [" + i + "] = 1.9");
                    sda.fpt8o.write("\nDesired Input [" + i + "] = 1.9");
                }
                if (i == 1) {
                    System.out.println("\n Desired Input [" + i + "] = 15.0");
                    sda.fpt8o.write("\nDesired Input [" + i + "] = 15.0");
                }
                if (i == 2 || i == 3) {
                    System.out.println("\n Desired Input [" + i + "] = 1.0");
                    sda.fpt8o.write("\nDesired Input [" + i + "] = 1.0");
                }
                if (i == 4) {
                    System.out.println("\n Desired Input [" + i + "] = 60.0");
                    sda.fpt8o.write("\nDesired Input [" + i + "] = 60.0");
                }
            }
            sda.fpt8o.close();
        }
    }

    static class LAYER {
        int Units;
        float[] Output;
        float[] Error;
        float[][] Weight;
        float[][] WeightSave;
        float[][] dWeight;
    }

    static class NET {
        LAYER[] Layer;
        float Alpha;
        float Eta;
        float Gain;
        float Error;
    }

    static class Sda {
        float Disturbance;

        File traindata, testdata, fpt1, fpt2, fpt3, fpt4, fpt5, fpt7, fpt8;
        FileWriter fpt1o, fpt2o, fpt3o, fpt4o, fpt5o, fpt8o;
        FileReader traindatai, testdatai;
        BufferedReader trainbuffer, fpt3buffer, testbuffer;

        int VECTDIM, n_runs, m, BIAS;
        int[] opt;
        int[] Units;
        NET Net;
        float[][] TrainMatrix;
        float[][] TestMatrix;

        float[] dWeight;
        float[] vector;
        float[][] dOutput;
        float[][] dSum;
        float[][] dOut;
        float[] OptiVector;
        float[] MinVector;
        float[] MaxVector;
        float Max_Err, TrainError, MIN_REAL, MAX_REAL;
        long RAND_MAX;

        //data in para.h	start
        static int FEATURES, FLAGS, NUM_LAYERS, MID_NODES1, MID_NODES2;
        int M, N, N_Traindata, N_Testdata;
        float N_Epochs, ALPHA, ETA, GAIN, MinErr;
        //data in para.h	end

        Random random;

        String d, e, f;
        StringTokenizer st, ss, su;

        @SuppressLint("SdCardPath")
        public Sda() throws Exception {

            // values in para.h start
            N = FEATURES;
            M = FLAGS;
            N_Epochs = 105770;

            ALPHA = (float) 0.7506; //Momentum Factor
            ETA = (float) 0.57861; //Learning Rate

            GAIN = 1; //Gain of the Network
            N_Traindata = 36; // Number of data being provided in the Training Set // change made by bhavana
            // earlier it was 300

            N_Testdata = 300;
            MinErr = (float) 0.001; // Minimum Mean Square Error Desired before stopping training
            // values in para.h end

            Disturbance = (float) 0.3; // Disturbance 30%

            traindata = new File("/data/user/0/com.example.inverseai/files/traindata.txt");
            testdata = new File("/data/user/0/com.example.inverseai/files/testdata.txt");
            traindatai = new FileReader(traindata);
            trainbuffer = new BufferedReader(traindatai);
            d = trainbuffer.readLine();
            st = new StringTokenizer(d);

            testdatai = new FileReader(testdata);
            testbuffer = new BufferedReader(testdatai);
            f = testbuffer.readLine();

            VECTDIM = FEATURES + FLAGS;
            opt = new int[FEATURES + 2];
            dWeight = new float[FEATURES];
            TrainMatrix = new float[N_Traindata][VECTDIM];
            TestMatrix = new float[N_Testdata][FEATURES];
            vector = new float[FEATURES];

            dOutput = new float[FLAGS][FEATURES];
            dSum = new float[FLAGS][FEATURES];
            dOut = new float[FLAGS][FEATURES];

            OptiVector = new float[VECTDIM + 2]; // Input Vector for optimizing
            MinVector = new float[VECTDIM + 1];
            MaxVector = new float[VECTDIM + 1];
            Units = new int[3];
            Units[0] = N;
            Units[1] = MID_NODES1;
            Units[2] = M;

            MIN_REAL = Float.NEGATIVE_INFINITY;
            MAX_REAL = Float.POSITIVE_INFINITY;

            BIAS = 1;

            RAND_MAX = 2147483647; // defined in C
            Net = new NET();
            random = new Random(8049);
        }

        public void GenerateNetwork() {
            int l, i;
            Net.Layer = new LAYER[NUM_LAYERS];
            for (l = 0; l < NUM_LAYERS; l++) {
                Net.Layer[l] = new LAYER();
                Net.Layer[l].Units = Units[l];
                Net.Layer[l].Output = new float[Units[l] + 1];
                Net.Layer[l].Error = new float[Units[l] + 1];
                Net.Layer[l].Weight = new float[Units[l] + 1][Units[l] + 2];
                Net.Layer[l].WeightSave = new float[Units[l] + 1][Units[l] + 2];
                Net.Layer[l].dWeight = new float[Units[l] + 1][Units[l] + 2];
                Net.Layer[l].Output[0] = BIAS;
                if (l != 0) {
                    for (i = 1; i <= Units[l]; i++) {
                        Net.Layer[l].Weight = new float[Units[l] + 1][Units[l - 1] + 1];
                        Net.Layer[l].WeightSave = new float[Units[l] + 1][Units[l - 1] + 1];
                        Net.Layer[l].dWeight = new float[Units[l] + 1][Units[l - 1] + 1];
                    }
                }
            }
            Net.Alpha = ALPHA;
            Net.Eta = ETA;
            Net.Gain = GAIN;
            Net.Error = 0;
        }

        public void RandomWeights() {
            int l, i, j;
            //change made by bhavana the random weights array size changes from 105 to 45
            double[] temp = {0.013871, -0.324274, -0.191366, 0.034532, 0.44763, -0.328272, 0.202231, -0.273583, -0.005234,
                    -0.375301, -0.416105, -0.11037, -0.22277, -0.131947, 0.483459, 0.035386, 0.265679, 0.146474, 0.267144,
                    0.280236, 0.322962, -0.348079, 0.125477, -0.185324, -0.153096, 0.417203, 0.019761, -0.098834, 0.106769,
                    0.285424, 0.431547, 0.36993, 0.366543, 0.17452, 0.258415, 0.081896, -0.110767, -0.144368, -0.299768,
                    0.32693, -0.084094, -0.036485, 0.479186, -0.373562, -0.287378};
            int a = 0;
            for (l = 1; l < NUM_LAYERS; l++) {
                for (i = 1; i <= Net.Layer[l].Units; i++) {
                    for (j = 1; j <= Net.Layer[l - 1].Units; j++) {
                        Net.Layer[l].Weight[i][j] = (float) temp[a++];
                    }
                }
            }
        }

        public void TrainNet(int n) throws Exception {

            int i, a;
            // float MinTrainError; // float Prev_Err
            float[] Output = new float[M];
            TrainError = 0;
            Max_Err = 0;
            // MinTrainError = Float.POSITIVE_INFINITY;
            // Prev_Err = 0;
            FillTrainMatrix();

            float[] temp1;
            float[] temp2;
            temp1 = new float[VECTDIM];
            temp2 = new float[FLAGS];

            for (i = 0; i < N_Traindata; i++) {
                for (a = 0; a < VECTDIM; a++) {
                    temp1[a] = TrainMatrix[i][a];
                }
                for (a = FEATURES; a < VECTDIM; a++) {
                    temp2[a - FEATURES] = TrainMatrix[i][a];
                }
                SimulateNet(temp1, Output, temp2, true, false); // %%%%%%%%

                Max_Err = Math.max(Net.Error, Max_Err);
                TrainError = Max_Err;
            }
            // Prev_Err = Max_Err;

            fpt2o.write(n + "\t" + Max_Err + "\n"); // writing Into EFile the values
           // if (TrainError < MinTrainError)
                //MinTrainError = TrainError;
        }

        public void FillTrainMatrix() throws Exception {
            // change made by bhavana
            int i, j;
            float[] doub = new float[N_Traindata * VECTDIM]; // no of row * columns traindatafile earlier it was 2100 and
            // train data 300 * (3+4)
            for (i = 0; i < doub.length; i++) {
                // System.out.println("doub[i]:"+doub[i]);
                doub[i] = Float.parseFloat(st.nextToken());
                if (!st.hasMoreTokens()) {
                    d = trainbuffer.readLine();
                    try {
                        st = new StringTokenizer(d);
                    } catch (Exception e) {
                        System.out.println("error");
                    }
                }
            }
            int c = 0;

            for (i = 0; i < N_Traindata; i++)
                for (j = 0; j < VECTDIM; j++) {
                    TrainMatrix[i][j] = doub[c];
                    c++;
                }
            traindatai = new FileReader(traindata);
            trainbuffer = new BufferedReader(traindatai);
            d = trainbuffer.readLine();
            st = new StringTokenizer(d);
        }

        public void SimulateNet(float[] Input, float[] Output, float[] Target, boolean Training, boolean Optimize) {
            TestSimNet(Input, Output);
            if (Training) {
                // System.out.println("If training");
                ComputeOutputError(Target);
                BackPropagateNet();
                AdjustWeights();
            } else {
                if (Optimize) {
                    // System.out.println("If optimizing");
                    ComputeOutputError(Target);
                    BackPropagateNet();
                    AdjustInputs();
                } else {
                    ComputeOutputError(Target);
                    BackPropagateNet();
                }
            }
        }

        public void TestSimNet(float[] Input, float[] Output) {
            SetInput(Input);
            PropagateNet();
            GetOutput(Output);
        }

        public void SetInput(float[] Input) {
            int i;
            for (i = 1; i <= Net.Layer[0].Units; i++) {
                Net.Layer[0].Output[i] = Input[i - 1];
                // System.out.println("Net.Layer[0].Output[i]:" +Net.Layer[0].Output[i]);
            }
        }

        public void PropagateNet() {
            int l;
            for (l = 0; l < NUM_LAYERS - 1; l++)
                PropagateLayer(l, l + 1);
        }

        public void PropagateLayer(int l, int u) {
            int i, j;
            float Sum;

            for (i = 1; i <= Net.Layer[u].Units; i++) {
                Sum = 0;
                for (j = 1; j <= Net.Layer[l].Units; j++) {
                    Sum += Net.Layer[u].Weight[i][j] * Net.Layer[l].Output[j];
                }
                Net.Layer[u].Output[i] = (float) (1 / (1 + Math.exp(-Net.Gain * Sum)));
                // System.out.println("Net.Layer[u].Output[i]::"+Net.Layer[u].Output[i]);
            }
        }

        public void GetOutput(float[] Output) {
            int i;
            for (i = 1; i <= Net.Layer[NUM_LAYERS - 1].Units; i++) {
                Output[i - 1] = Net.Layer[NUM_LAYERS - 1].Output[i];
                // System.out.println(Output[i-1]);
            }
        }

        public void ComputeOutputError(float[] Target) {
            int i;
            float Out, Err;
            Net.Error = 0;
            for (i = 1; i <= Net.Layer[NUM_LAYERS - 1].Units; i++) {
                Out = Net.Layer[NUM_LAYERS - 1].Output[i];
                Err = Target[i - 1] - Out;
                Net.Layer[NUM_LAYERS - 1].Error[i] = Net.Gain * Out * (1 - Out) * Err;
                Net.Error += 0.5 * Err * Err;
            }
        }

        public void BackPropagateNet() {
            int l;
            for (l = NUM_LAYERS - 1; l > 0; l--)
                BackPropagateLayer(l, l - 1);
        }

        public void BackPropagateLayer(int u, int l) {
            int i, j;
            float Out, Err;
            for (i = 1; i <= Net.Layer[l].Units; i++) {
                Err = 0;
                Out = Net.Layer[l].Output[i];
                for (j = 1; j <= Net.Layer[u].Units; j++)
                    Err += Net.Layer[u].Weight[j][i] * Net.Layer[u].Error[j];
                Net.Layer[l].Error[i] = Net.Gain * Out * (1 - Out) * Err;
            }
        }

        public void AdjustWeights() {
            int l, i, j;
            float Out, Err, dWeight;
            for (l = 1; l < NUM_LAYERS; l++)
                for (i = 1; i <= Net.Layer[l].Units; i++)
                    for (j = 1; j <= Net.Layer[l - 1].Units; j++) {
                        Out = Net.Layer[l - 1].Output[j];
                        Err = Net.Layer[l].Error[i];
                        dWeight = Net.Layer[l].dWeight[i][j];
                        Net.Layer[l].Weight[i][j] += Net.Eta * Err * Out + Net.Alpha * dWeight;
                        Net.Layer[l].dWeight[i][j] = Net.Eta * Err * Out;
                    }
        }

        public void AdjustInputs() {
            int j;
            float Out, Err;
            for (j = 1; j <= FEATURES; j++) {
                if (opt[j] == 1) {
                    Out = OptiVector[j];
                    Err = Net.Layer[0].Error[j];
                    if (OptiVector[j] <= MinVector[j] && OptiVector[j] <= MaxVector[j]) {
                        Net.Layer[0].Output[j] += Net.Eta * Err * Out + Net.Alpha * dWeight[j];
                        OptiVector[j] += Net.Eta * Err * Out + Net.Alpha * dWeight[j];
                        if ((j == 10) || (j == 11)) {
                            OptiVector[j] += Math.round(400 * Net.Eta * Err * Out)
                                    + Math.round(400 * Net.Alpha * dWeight[j]);
                            OptiVector[j] = Math.round(OptiVector[j]);
                        }
                        dWeight[j] = Net.Eta * Err * Out;
                    }
                }
            }
        }

        public void SaveWeights() {
            int l, i, j;
            for (l = 1; l < NUM_LAYERS; l++) {
                for (i = 1; i <= Net.Layer[l].Units; i++) {
                    for (j = 1; j <= Net.Layer[l - 1].Units; j++) {
                        Net.Layer[l].WeightSave[i][j] = Net.Layer[l].Weight[i][j];
                    }
                }
            }
        }

        public void RestoreWeights() {
            int l, i, j;
            for (l = 1; l < NUM_LAYERS; l++) {
                for (i = 1; i <= Net.Layer[l].Units; i++) {
                    for (j = 1; j <= Net.Layer[l - 1].Units; j++) {
                        Net.Layer[l].Weight[i][j] = Net.Layer[l].WeightSave[i][j];
                    }
                }
            }
        }

        public void WriteWeights() throws Exception {

            int l, i, j;
            for (l = 1; l < NUM_LAYERS; l++) {
                fpt5o.write("Weights from [" + l + "] layer to [" + (l + 1) + "] layer\n"); // Writing to WtFile
                for (i = 1; i <= Net.Layer[l].Units; i++) {
                    for (j = 1; j <= Net.Layer[l - 1].Units; j++) {
                        fpt3o.write(Net.Layer[l].WeightSave[i][j] + "\n");
                        fpt5o.write(Net.Layer[l].WeightSave[i][j] + "\t");
                    }
                    fpt5o.write("\n");
                }
            }
        }

        public void TestNet() throws Exception {
            int a;
            float[] Output = new float[M];
            Max_Err = 0;
            FillTrainMatrix();
            float[] temp1;
            float[] temp2;
            temp1 = new float[VECTDIM];
            temp2 = new float[FLAGS];
            for (int i = 0; i < N_Traindata; i++) {
                for (a = 0; a < VECTDIM; a++) {
                    temp1[a] = TrainMatrix[i][a];
                    // System.out.println("temp1[a]:"+temp1[a]);
                }
                for (a = FEATURES; a < VECTDIM; a++) {
                    temp2[a - FEATURES] = TrainMatrix[i][a];
                    // System.out.println("temp2[a-features]:"+temp2[a-FEATURES]);
                }
                SimulateNet(temp1, Output, temp2, false, false);
                Max_Err = Math.max(Max_Err, Net.Error);
            }
            // System.out.println("Maximum error for testing set = "+Max_Err);
        }

        public void ReadWeights() throws Exception {
            //	int l,i,j;
            //	String s;
            //	for(l=1;l<NUM_LAYERS;l++) {
            //		for(i=1;i<=Net.Layer[l].Units;i++) {
            //			for(j=1;j<=Net.Layer[l-1].Units;j++) {
            //			s=buf.readLine();
            //				Net.Layer[l].WeightSave[i][j]=Float.valueOf(s).floatValue();
            //			}
            //		}
            //	}
            // fpt3i=new FileReader(fpt3);

            // e=fpt3buffer.readLine();
            // ss=new StringTokenizer(e);
            int i;
            double[] doub = new double[MID_NODES1 * VECTDIM]; // change made by bhavana - the array size changed from 105 to
            // 45
            // System.out.print("In read:"+doub.length);
            for (i = 0; i < doub.length; i++) {
                // changed from ss to st as it gave error
                doub[i] = Float.parseFloat(ss.nextToken());
                // System.out.print("\ndoub[i]:"+doub[i]);
                if (!ss.hasMoreTokens()) {
                    // d=trainbuffer.readLine();
                    e = fpt3buffer.readLine();
                    // System.out.println("d in readweights:"+e);
                    try {
                        ss = new StringTokenizer(e);
                    } catch (Exception e) {
                        System.out.println("error");
                    }
                }
            }
            int l, j, c = 0;

            for (l = 1; l < NUM_LAYERS; l++) {
                for (i = 1; i <= Net.Layer[l].Units; i++) {
                    for (j = 1; j <= Net.Layer[l - 1].Units; j++) {
                        Net.Layer[l].WeightSave[i][j] = (float) doub[c];
                        // System.out.print("\nNet.Layer[l].WeightSave[i][j]:"+Net.Layer[l].WeightSave[i][j]);
                        c++;
                    }
                }
            }
        }

        public void EvaluateNet() throws Exception {
            // System.out.println("In Evaulate net");
            float[] Output = new float[M];
            float[] temp1;
            int i, c = 0;
            int j;
            su = new StringTokenizer(f);

            // Why 195 ? check and change accordingly
            FillTestVector();
            temp1 = new float[FEATURES];

            for (i = 0; i < N_Testdata; i++) {
                // System.out.println("temp1[a]:"+temp1[a]);
                if (FEATURES >= 0) System.arraycopy(TestMatrix[i], 0, temp1, 0, FEATURES);
                TestSimNet(temp1, Output);

                for (j = 0; j < FLAGS; j++) {
                    fpt1o.write(Output[j] + "\t"); // writing to WFile
                }
                fpt1o.write("\n");

            }

            for (i = 0; i < N_Testdata; i++) {
                FillTestVector();
                for (j = 0; j < FEATURES; j++) { // output is 1
                    vector[j] = Float.parseFloat(su.nextToken());
                    System.out.println("vector[j]:" + vector[j]);
                }

                TestSimNet(vector, Output);
            }
            while (c < 37) {
                c++;
                FillTestVector();
                if (c == 36)
                    System.out.println("END\n");
                TestSimNet(vector, Output);
                fpt1o.write("\n");

                for (i = 0; i < M; i++) {
                    fpt1o.write(Output[i] + "\t"); // writing to WFile

                }
            }
            fpt1o.close();
        }

        public void FillTestVector() throws Exception {
            float[] Output = new float[M];
            int j;
            su = new StringTokenizer(f);
            for (j = 0; j < FEATURES; j++) { // output is 1
                vector[j] = Float.parseFloat(su.nextToken());
                System.out.println("vector[j]:" + vector[j]);
            }
            int i;
            float[] doub = new float[N_Testdata * FEATURES]; // no of row * columns traindatafile earlier it was 2100 and
            // train data 300 * (3+4)
            for (i = 0; i < doub.length; i++) {
                // System.out.println("doub[i]:"+doub[i]);
                try {
                    doub[i] = Float.parseFloat(su.nextToken());
                    System.out.println("doub[i]:" + doub[i]);
                } catch (Exception e) {
                    System.out.println("error");
                }
                if (!su.hasMoreTokens()) {
                    f = testbuffer.readLine();
                    try {
                        su = new StringTokenizer(f);
                    } catch (Exception e) {
                        System.out.println("error");
                    }
                }
                TestSimNet(doub, Output);
            }
            int c = 0;

            for (i = 0; i < N_Testdata; i++)
                for (j = 0; j < FEATURES; j++) {
                    TestMatrix[i][j] = doub[c];
                    c++;
                }
            testdatai = new FileReader(testdata);
            testbuffer = new BufferedReader(testdatai);
            f = testbuffer.readLine();
            su = new StringTokenizer(f);

        }

        public void Optimize_Options() throws Exception {
            int i, j, k;
            ReadWeights();
            Scanner LineInput1 = new Scanner(System.in);
            for (j = 1; j <= FLAGS; j++) {
                System.out.println("\nEnter # of outputs: ");
                OptiVector = new float[LineInput1.nextInt()];
                opt = new int[OptiVector.length];
                for (k = 0; k < OptiVector.length; k++) {
                    System.out.println("\nDo you want to optimize output # " + (k + 1) + " (1 for YES; 0 for NO) :");
                    opt[k] = LineInput1.nextInt();
                    if (opt[k] == 0) {
                        System.out.println("\nEnter the value of the output # " + (k + 1) + ":");
                        OptiVector[k] = LineInput1.nextFloat();
                    } else if (opt[k] == 1) {
                        OptiVector[k] = (float) 0.5;
                        if (k == 0)
                            OptiVector[k] = 46;
                        if (k == 1)
                            OptiVector[k] = 31;
                        if (k == 2 || k == 3)
                            OptiVector[k] = 94;
                        if (k == 4)
                            OptiVector[k] = 160;
                    }
                }

                // System.out.println("\nEnter value of the output desired[" + m + "] :");
                // @ read OptiVector[FEATURES+j]
                // OptiVector[FEATURES + j] = LineInput1.nextFloat();
                // System.out.println("Read OptiVector[FEATURES+j]::"+OptiVector[FEATURES+j]);
            }
            for (i = 1; i <= FEATURES; i++) {
                System.out.println("\nEnter # of inputs: ");
                String[] range;
                String l;
                opt = new int[LineInput1.nextInt()];
                MinVector = new float[opt.length];
                MaxVector = new float[opt.length];
                for (k = 0; k < opt.length; k++) {
                    float min, max;
                    System.out.println("\nEnter the lower and upper bound of the input # " + (k + 1) + " (LOWER,UPPER) :");
                    l = LineInput1.next();
                    range = l.split(",");
                    min = Float.parseFloat(range[0]);
                    max = Float.parseFloat(range[1]);
                    MinVector[k] = min;
                    MaxVector[k] = max;
                }

                // System.out.println("\nDo you want to optimize input # " + i + " (1 for YES; 0 for NO) :");
                // opt[i] = LineInput1.nextInt();
                // @			read opt[i]
			/* if (opt[i] == 0) {
				System.out.println("\nEnter the value of the input # " + i + ":");
				// System.out.println("Read OptiVector[i]::"+OptiVector[i]);
				OptiVector[i] = LineInput1.nextFloat();
				//@				read OptiVector[i]
			} else if (opt[i] == 1) {
				OptiVector[i] = (float) 0.5;
				if (i == 9)
					OptiVector[i] = (float) 0.99;
				if (i == 10 || i == 11)
					OptiVector[i] = 1;
				}
			}
			for (i = 1; i<FEATURES; i++) {
				MaxVector[i] = (float) 0.9;
				MinVector[i] = (float) 0.1;
				dWeight[i] = 0;
			}
			MinVector[9] = (float) 0.3;
     		MaxVector[9] = (float) 1.0;
     		MaxVector[10] = 4;
     		MinVector[10] = 0;
     		MaxVector[11] = 5;
     		MinVector[11] = 0;
     		MaxVector[12] = 1;
     		MaxVector[13] = 1; */
                System.out.println("\nHow many runs :");
                // @ read n_runs
                n_runs = LineInput1.nextInt();
                System.out.println("**********************************");
                RestoreWeights();
                // for (i = 1; i <= FEATURES; i++)
                for (i = 0; i < OptiVector.length; i++) {
                    System.out.println("\nOutput # " + i + " : " + OptiVector[i]);
                    fpt8o.write("\nOptimized Output " + i + " : " + OptiVector[i]);
                }
            }
            for (i = 0; i < opt.length; i++) {
                float[] Output = new float[OptiVector.length];
                PropagateNet();
                GetOutput(Output);
                // printf("\nBest value of Output[%d] after %d runs =
                // %f\n",i,n,Net->Layer[NUM_LAYERS-1]->Output[i]);
                System.out.println("\nBest value of Input[" + i + "] after " + n_runs + " runs = " + OptiVector[i]);
                fpt8o.write("\nBest value of Input[" + i + "] after " + n_runs + " runs = " + OptiVector[i]);
            }
        }

        public void InverseMap(int n_runs) throws Exception {
            int n, i, a;
            float[] Output = new float[M + 1];
            Max_Err = MAX_REAL;
            n = 0;

            do {
                float[] temp1;
                float[] temp2;
                temp1 = new float[VECTDIM + 1];
                temp2 = new float[VECTDIM - FEATURES + 1];
                for (a = 1; a < VECTDIM + 1; a++) {
                    temp1[a - 1] = OptiVector[a];
                }
                for (a = FEATURES + 1; a < VECTDIM + 1; a++) {
                    temp2[FEATURES + 1 - a] = OptiVector[a];
                }

                SimulateNet(temp1, Output, temp2, false, true);
                Max_Err = Math.min(Net.Error, Max_Err);
                if (n % 100 == 0) {
                    fpt4o.write("\n" + n + "\t" + Max_Err);
                    for (i = 1; i <= FEATURES; i++) {
                        fpt4o.write("\t" + Net.Layer[0].Output[i]);
                    }
                    for (i = 1; i < FLAGS; i++) {
                        fpt4o.write("\t" + Net.Layer[NUM_LAYERS - 1].Output[i]);
                    }
                }
                n++;
            } while (Max_Err > 0.0000000005 && n < n_runs);

		/* for (i = 1; i<= FEATURES; i++) {
			// printf("Optimized Input %d = %f\n",(i),Net.Layer[0].Output[i]);
			fpt8o.write("\nOptimized Input: " + i + "\t" + Net.Layer[0].Output[i]);
		}

		PropagateNet();
		GetOutput(Output);

		for (i = 1; i<= FLAGS; i++) {
			//   printf("\nBest value of Output[%d] after %d runs = %f\n",i,n,Net->Layer[NUM_LAYERS-1]->Output[i]);
			System.out.println("\nBest value of Output[" + i + "] after " + n + " runs = " + Net.Layer[NUM_LAYERS - 1].Output[i]);
			fpt8o.write("\nBest value of Output[" + i + "] after " + n + " runs = " + Net.Layer[NUM_LAYERS - 1].Output[i]);
		}*/
        }
    }
}