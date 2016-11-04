package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.*;

public class NeuralNetworkBuilder {
    private final Set<InputNeuron> inputLayer;
    private final List<Set<HiddenNeuron>> hiddenLayers;
    private final Set<OutputNeuron> outputLayer;
    private ActivationFunction inputLayerActivationFunction;
    private ActivationFunction hiddenLayersActivationFunction;
    private ActivationFunction outputLayerActivationFunction;

    private double hiddenBias = 0.35,
            outputBias = 0.60,
            eta = 0.0002;

    public NeuralNetworkBuilder() {
        inputLayer = new LinkedHashSet<InputNeuron>();
        hiddenLayers = new ArrayList<Set<HiddenNeuron>>();
        outputLayer = new LinkedHashSet<OutputNeuron>();
        inputLayerActivationFunction = new BentIdentity();
        hiddenLayersActivationFunction = new BentIdentity();
        outputLayerActivationFunction = new BentIdentity();
    }

    public NeuralNetworkBuilder addInputLayer(int inputLayerDim) {
        if (!inputLayer.isEmpty())
            throw new IllegalStateException("Input Layer already added");

        for (int i = 0; i < inputLayerDim; i++)
            inputLayer.add(new InputNeuron(1, inputLayerActivationFunction));

        return this;
    }

    public NeuralNetworkBuilder addHiddenLayer(int hiddenLayerDim) {
        if (inputLayer.isEmpty())
            throw new IllegalStateException("Missing Input Layer");

        Set<? extends Neuron> previousLayer = inputLayer;
        if (hiddenLayers.size() > 0)
            previousLayer = hiddenLayers.get(hiddenLayers.size() - 1);

        Set<HiddenNeuron> hiddenLayer = new LinkedHashSet<HiddenNeuron>(hiddenLayerDim);
        for (int i = 0; i < hiddenLayerDim; i++) {
            HiddenNeuron hiddenNeuron = new HiddenNeuron(hiddenBias, hiddenLayersActivationFunction);
            hiddenNeuron.linkToPrevLayer(previousLayer);
            hiddenLayer.add(hiddenNeuron);
        }

        //need to link previous layer with next layer
        for (Neuron neuron : previousLayer)
            neuron.linkToNextLayer(hiddenLayer);

        hiddenLayers.add(hiddenLayer);

        return this;
    }

    public NeuralNetworkBuilder addOutputLayer(int outputLayerDim) {
        if (!outputLayer.isEmpty())
            throw new IllegalStateException("Output Layer already added");

        //get last hidden layer
        Set<HiddenNeuron> lastHiddenLayer = hiddenLayers.get(hiddenLayers.size() - 1);
        for (int i = 0; i < outputLayerDim; i++) {
            OutputNeuron outputNeuron = new OutputNeuron(outputBias, outputLayerActivationFunction);
            outputNeuron.linkToPrevLayer(lastHiddenLayer);
            outputLayer.add(outputNeuron);
        }
        //need to link output layer with the last hidden layer
        for (HiddenNeuron hiddenNeuron : lastHiddenLayer)
            hiddenNeuron.linkToNextLayer(outputLayer);

        return this;
    }
    
    public NeuralNetworkBuilder addOutputLayer(String... outputIDs) {
        
        if (!outputLayer.isEmpty())
            throw new IllegalStateException("Output Layer already added");
        
        //get last hidden layer
        Set<HiddenNeuron> lastHiddenLayer = hiddenLayers.get(hiddenLayers.size() - 1);
        for (int i = 0; i < outputIDs.length; i++) {
            OutputNeuron outputNeuron = new OutputNeuron(outputBias, outputLayerActivationFunction, outputIDs[i]);
            outputNeuron.linkToPrevLayer(lastHiddenLayer);
            outputLayer.add(outputNeuron);
        }
        //need to link output layer with the last hidden layer
        for (HiddenNeuron hiddenNeuron : lastHiddenLayer)
            hiddenNeuron.linkToNextLayer(outputLayer);
        
        return this;
    }
    
    
    public NeuralNetworkBuilder setInputLayerActivationFunction(ActivationFunction inputLayerActivationFunction) {
        this.inputLayerActivationFunction = inputLayerActivationFunction;
        for (InputNeuron inputNeuron : inputLayer)
            inputNeuron.setActivationFunction(inputLayerActivationFunction);

        return this;
    }

    public NeuralNetworkBuilder setHiddenLayersActivationFunction(ActivationFunction hiddenLayersActivationFunction) {
        this.hiddenLayersActivationFunction = hiddenLayersActivationFunction;
        for (Set<HiddenNeuron> hiddenLayer : hiddenLayers)
            for (HiddenNeuron hiddenNeuron : hiddenLayer)
                hiddenNeuron.setActivationFunction(hiddenLayersActivationFunction);

        return this;
    }

    public NeuralNetworkBuilder setOutputLayerActivationFunction(ActivationFunction outputLayerActivationFunction) {
        this.outputLayerActivationFunction = outputLayerActivationFunction;
        for (OutputNeuron outputNeuron : outputLayer)
            outputNeuron.setActivationFunction(outputLayerActivationFunction);

        return this;
    }

    public NeuralNetworkBuilder setHiddenLayerBias(double hiddenLayerBias) {
        hiddenBias = hiddenLayerBias;

        return this;
    }

    public NeuralNetworkBuilder setOutputLayerBias(double outputLayerBias) {
        outputBias = outputLayerBias;

        return this;
    }

    public NeuralNetworkBuilder setEta(double eta) {
        this.eta = eta;

        return this;
    }

    public Set<InputNeuron> getInputLayer() {
        return inputLayer;
    }

    public List<Set<HiddenNeuron>> getHiddenLayers() {
        return hiddenLayers;
    }

    public Set<OutputNeuron> getOutputLayer() {
        return outputLayer;
    }

    public ActivationFunction getInputLayerActivationFunction() {
        return inputLayerActivationFunction;
    }

    public ActivationFunction getHiddenLayersActivationFunction() {
        return hiddenLayersActivationFunction;
    }

    public ActivationFunction getOutputLayerActivationFunction() {
        return outputLayerActivationFunction;
    }

    public double getHiddenBias() {
        return hiddenBias;
    }

    public double getOutputBias() {
        return outputBias;
    }

    public double getEta() {
        return eta;
    }

    public NeuralNetwork build() {
        return new NeuralNetwork(this);
    }
}
