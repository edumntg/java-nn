import engine.Value;
import loss.Loss;
import loss.MSELoss;
import nn.Layer;
import nn.MLP;
import optimizer.Optimizer;
import optimizer.SGD;
import scaler.StandardScaler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Demo {
    public static void main(String[] args) throws Exception {

        // Training parameters
        float lr = 1E-2f;
        float momentum = 0.0f;
        int EPOCHS = 50;

        // Set seed
        Value.rng.setSeed(1218);

        // Read dataset
        List<List<String>> records = new ArrayList<>();
        Value[][] x = new Value[5000][5]; // 5000 samples, 5 features
        Value[] y = new Value[5000];

        // Read dataset
        try(BufferedReader br = new BufferedReader(new FileReader("housing_dataset.csv"))) {
            String line;
            int i = 0;
            while((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for(int j = 0; j < 5; j++) {
                    x[i][j] = new Value(Float.parseFloat(values[j]));
                    x[i][j].requiresGrad = false;
                }
                y[i] = new Value(Float.parseFloat(values[5]));
                y[i].requiresGrad = false;
                i++;
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }

        // Scale dataset
        StandardScaler scaler = new StandardScaler();
        scaler.fitTransform(x);
        scaler.fitTransform(y);

        // Create model
        MLP model = new MLP();
        model.add(new Layer(5, 16)); // input layer
        model.add(new Layer(16, 8)); // hidden
        model.add(new Layer(8, 1)); // output

        // Criterion and optimizer
        Optimizer optimizer = new SGD(model.parameters(), lr, momentum);
        Loss criterion = new MSELoss();

        // Train
        Value[] scores;
        Value loss = null;
        for(int epoch = 1; epoch <= EPOCHS; epoch++) {
            optimizer.zeroGrad(); // reset all gradients;

            // Compute scores
            scores = model.forward(x);

            // Compute loss
            loss = criterion.forward(y, scores);

            // Compute gradients
            loss.backward();

            // Update parameters
            optimizer.step();

            // Print loss
            System.out.println(String.format("Epoch %d, loss = %.10f", epoch, loss.item()));
            //System.out.println(loss.toString());
        }
    }
}
