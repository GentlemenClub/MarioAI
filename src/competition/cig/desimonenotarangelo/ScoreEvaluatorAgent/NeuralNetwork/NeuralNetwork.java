
package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

//Main Neural Network class: it takes the byte[][] environment of the game as input
//and outputs the predicted delta score for one action

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NeuralNetwork {
    public static final double hiddenBias = 0.35,
            outputBias = 0.60,
            eta = 0.20;

    Set<InputNeuron> inputLayer;
    Set<HiddenNeuron> hiddenLayer;
    Set<OutputNeuron> outputLayer;

    public NeuralNetwork(int inputLayerDim, int hiddenLayerDim) {
        //forcing only one output
        this(inputLayerDim, hiddenLayerDim, 1);
    }

    public NeuralNetwork(File file) {
        loadNeuralNetwork(file);
    }

    private NeuralNetwork(int inputLayerDim, int hiddenLayerDim, int outputLayerDim) {
        //this constructor is private because we can't handle now multiple outputs
        createInputLayer(inputLayerDim);
        createHiddenLayer(hiddenLayerDim);
        createOutputLayer(outputLayerDim);
    }

    private void createInputLayer(int inputLayerDim) {
        inputLayer = new HashSet<InputNeuron>(inputLayerDim);
        for (int i = 0; i < inputLayerDim; i++)
            inputLayer.add(new InputNeuron());
    }

    private void createHiddenLayer(int hiddenLayerDim) {
        hiddenLayer = new HashSet<HiddenNeuron>(hiddenLayerDim);
        for (int i = 0; i < hiddenLayerDim; i++) {
            HiddenNeuron hiddenNeuron = new HiddenNeuron();
            hiddenNeuron.linkToPrevLayer(inputLayer);
            hiddenLayer.add(hiddenNeuron);
        }

    }

    private void createOutputLayer(int outputLayerDim) {
        outputLayer = new HashSet<OutputNeuron>(outputLayerDim);
        for (int i = 0; i < outputLayerDim; i++) {
            OutputNeuron outputNeuron = new OutputNeuron();
            outputNeuron.linkToPrevLayer(hiddenLayer);
            outputLayer.add(new OutputNeuron());
        }
        //need to link previous layer with next layer?
        for (HiddenNeuron hiddenNeuron : hiddenLayer)
            hiddenNeuron.linkToNextLayer(outputLayer);
    }

    private void saveNeuralNetwork(File file) {
    }

    private void loadNeuralNetwork(File file) {

    }

    public void saveDeltaWeights(Map<Link, Double> deltaWeights, Set<? extends Neuron> layer) {
        //Calculates deltaWeigths for each node in the layer
        for (Neuron n : layer) {
            double deltaWeight = (-eta) * n.delta() * n.getCurrentNet();
            for (Link l : n.getPrevNeurons())
                deltaWeights.put(l, deltaWeight);
        }
    }

    public void backPropagation(double targetOutput) {
        //TODO: Bias update to be implemented

        Map<Link, Double> deltaWeights = new HashMap<Link, Double>();

        saveDeltaWeights(deltaWeights, outputLayer);
        saveDeltaWeights(deltaWeights, hiddenLayer);

        for (Link l : deltaWeights.keySet())
            l.updateWeight(deltaWeights.get(l));

    }

    public static void main (String... args) {
        NeuralNetwork neuralNetwork = new NeuralNetwork(3, 3);

    }

}