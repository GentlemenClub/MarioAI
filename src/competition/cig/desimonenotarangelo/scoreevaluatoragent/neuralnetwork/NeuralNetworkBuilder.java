package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;

import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.ActivationFunction;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.BentIdentity;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators.RandomGenerator;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators.ValueGenerator;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators.XavierGenerator;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators.ZeroGenerator;

import java.util.*;

public class NeuralNetworkBuilder {
    private final Set<InputNeuron> inputLayer;
    private final List<Set<HiddenNeuron>> hiddenLayers;
    private final Set<OutputNeuron> outputLayer;
    private ActivationFunction inputLayerActivationFunction;
    private ActivationFunction hiddenLayersActivationFunction;
    private ActivationFunction outputLayerActivationFunction;
    
    private double dropoutPercentage = 0.0;

    private double eta = 0.0002;

    public NeuralNetworkBuilder() {
        inputLayer = new LinkedHashSet<InputNeuron>();
        hiddenLayers = new ArrayList<Set<HiddenNeuron>>();
        outputLayer = new LinkedHashSet<OutputNeuron>();
        inputLayerActivationFunction = new BentIdentity();
        hiddenLayersActivationFunction = new BentIdentity();
        outputLayerActivationFunction = new BentIdentity();
    }
    
    public NeuralNetworkBuilder setDropoutPercentage(double percentage) {
        if (percentage < 0 || percentage > 1)
            throw new IllegalArgumentException("Input must be between 0 and 1");
        dropoutPercentage = percentage;

        return this;
    }

    public NeuralNetworkBuilder addInputLayer(int inputLayerDim) {
        if (!inputLayer.isEmpty())
            throw new IllegalStateException("Input Layer already added");

        for (int i = 0; i < inputLayerDim; i++)
            inputLayer.add(new InputNeuron(1, inputLayerActivationFunction));

        return this;
    }

    public NeuralNetworkBuilder addHiddenLayer(int hiddenLayerDim) {
        return addHiddenLayer(ValueGenerator.Type.XAVIER, ValueGenerator.Type.XAVIER, hiddenLayerDim);
    }

    public NeuralNetworkBuilder addHiddenLayer(ValueGenerator.Type weightInitializerType,
                                               ValueGenerator.Type biasInitializerType,
                                               int hiddenLayerDim) {
        if (inputLayer.isEmpty())
            throw new IllegalStateException("Missing Input Layer");

        Set<? extends Neuron> previousLayer = hiddenLayers.size() > 0 ? hiddenLayers.get(hiddenLayers.size() - 1) : inputLayer;
        int inputNeurons = previousLayer.size();
        int outputNeurons = hiddenLayerDim;
        ValueGenerator weightInitializer = getValueInitializerFromType(weightInitializerType, inputNeurons, outputNeurons);
        ValueGenerator biasInitializer = getValueInitializerFromType(biasInitializerType, inputNeurons, outputNeurons);
        
        Set<HiddenNeuron> hiddenLayer = new LinkedHashSet<HiddenNeuron>(hiddenLayerDim);
        for (int i = 0; i < hiddenLayerDim; i++) {
            HiddenNeuron hiddenNeuron = new HiddenNeuron(biasInitializer.getValue(), hiddenLayersActivationFunction);
            hiddenNeuron.linkToPrevLayer(previousLayer, weightInitializer);
            hiddenLayer.add(hiddenNeuron);
        }

        //need to link previous layer with next layer
        for (Neuron neuron : previousLayer)
            neuron.linkToNextLayer(hiddenLayer, weightInitializer);

        hiddenLayers.add(hiddenLayer);

        return this;
    }

    public NeuralNetworkBuilder addOutputLayer(int outputLayerDim) {
        return addOutputLayer(ValueGenerator.Type.XAVIER, ValueGenerator.Type.XAVIER, outputLayerDim);
    }

    public NeuralNetworkBuilder addOutputLayer(ValueGenerator.Type weightInitializerType,
                                               ValueGenerator.Type biasInitializerType,
                                               int outputLayerDim) {
        if (hiddenLayers.isEmpty())
            throw new IllegalStateException("Missing at least one Hidden Layer");
        if (!outputLayer.isEmpty())
            throw new IllegalStateException("Output Layer already added");

        //get last hidden layer
        Set<HiddenNeuron> lastHiddenLayer = hiddenLayers.get(hiddenLayers.size() - 1);
        int inputNeurons = lastHiddenLayer.size();
        int outputNeurons = outputLayerDim;
        ValueGenerator weightInitializer = getValueInitializerFromType(weightInitializerType, inputNeurons, outputNeurons);
        ValueGenerator biasInitializer = getValueInitializerFromType(biasInitializerType, inputNeurons, outputNeurons);

        for (int i = 0; i < outputLayerDim; i++) {
            OutputNeuron outputNeuron = new OutputNeuron(biasInitializer.getValue(), outputLayerActivationFunction);
            outputNeuron.linkToPrevLayer(lastHiddenLayer, weightInitializer);
            outputLayer.add(outputNeuron);
        }
        //need to link output layer with the last hidden layer
        for (HiddenNeuron hiddenNeuron : lastHiddenLayer)
            hiddenNeuron.linkToNextLayer(outputLayer, weightInitializer);

        return this;
    }

    public NeuralNetworkBuilder addOutputLayer(String... outputIDs) {
        return addOutputLayer(ValueGenerator.Type.XAVIER, ValueGenerator.Type.XAVIER, outputIDs);
    }

    public NeuralNetworkBuilder addOutputLayer(ValueGenerator.Type weightInitializerType,
                                               ValueGenerator.Type biasInitializerType,
                                               String... outputIDs) {
        if (hiddenLayers.isEmpty())
            throw new IllegalStateException("Missing at least one Hidden Layer");
        if (!outputLayer.isEmpty())
            throw new IllegalStateException("Output Layer already added");

        //get last hidden layer
        Set<HiddenNeuron> lastHiddenLayer = hiddenLayers.get(hiddenLayers.size() - 1);
        int inputNeurons = lastHiddenLayer.size();
        int outputNeurons = outputIDs.length;
        ValueGenerator weightInitializer = getValueInitializerFromType(weightInitializerType, inputNeurons, outputNeurons);
        ValueGenerator biasInitializer = getValueInitializerFromType(biasInitializerType, inputNeurons, outputNeurons);

        for (String outputID : outputIDs) {
            OutputNeuron outputNeuron = new OutputNeuron(biasInitializer.getValue(), outputLayerActivationFunction, outputID);
            outputNeuron.linkToPrevLayer(lastHiddenLayer, weightInitializer);
            outputLayer.add(outputNeuron);
        }
        //need to link output layer with the last hidden layer
        for (HiddenNeuron hiddenNeuron : lastHiddenLayer)
            hiddenNeuron.linkToNextLayer(outputLayer, weightInitializer);

        return this;
    }

    private ValueGenerator getValueInitializerFromType(ValueGenerator.Type valueInitializerType, int... parameters) {
        switch (valueInitializerType) {
            case RANDOM:
                return new RandomGenerator();
            case ZERO:
                return new ZeroGenerator();
            case XAVIER:
                return new XavierGenerator(parameters[0], parameters[1]);
        }
        return new RandomGenerator();
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

    public double getDropoutPercentage() { return dropoutPercentage; }

    public ActivationFunction getInputLayerActivationFunction() {
        return inputLayerActivationFunction;
    }

    public ActivationFunction getHiddenLayersActivationFunction() {
        return hiddenLayersActivationFunction;
    }

    public ActivationFunction getOutputLayerActivationFunction() {
        return outputLayerActivationFunction;
    }
    
    public double getEta() {
        return eta;
    }

    public NeuralNetwork build() {
        return new NeuralNetwork(this);
    }
}
