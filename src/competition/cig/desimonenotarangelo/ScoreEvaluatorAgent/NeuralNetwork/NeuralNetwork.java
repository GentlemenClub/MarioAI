
package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

//Main Neural Network class: it takes the byte[][] environment of the game as input
//and outputs the predicted delta score for one action

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class NeuralNetwork implements Serializable {
    public static final double hiddenBias = 0.35,
            outputBias = 0.60,
            eta = 0.0002;

    private Set<InputNeuron> inputLayer;
    private Set<HiddenNeuron> hiddenLayer;
    private Set<OutputNeuron> outputLayer;

    public NeuralNetwork(int inputLayerDim, int hiddenLayerDim) {
        //forcing only one output
        this(inputLayerDim, hiddenLayerDim, 1);
    }

    public NeuralNetwork(String fileName) throws IOException, ClassNotFoundException {
        loadNeuralNetwork(fileName);
    }

    public NeuralNetwork(int inputLayerDim, int hiddenLayerDim, int outputLayerDim) {
        createInputLayer(inputLayerDim);
        createHiddenLayer(hiddenLayerDim);
        createOutputLayer(outputLayerDim);
    }

    private void createInputLayer(int inputLayerDim) {
        inputLayer = new LinkedHashSet<InputNeuron>(inputLayerDim);
        for (int i = 0; i < inputLayerDim; i++)
            inputLayer.add(new InputNeuron(1));
    }

    private void createHiddenLayer(int hiddenLayerDim) {
        hiddenLayer = new LinkedHashSet<HiddenNeuron>(hiddenLayerDim);
        for (int i = 0; i < hiddenLayerDim; i++) {
            HiddenNeuron hiddenNeuron = new HiddenNeuron(hiddenBias);
            hiddenNeuron.linkToPrevLayer(inputLayer);
            hiddenLayer.add(hiddenNeuron);
        }

        //need to link previous layer with next layer
        for (InputNeuron inputNeuron : inputLayer)
            inputNeuron.linkToNextLayer(hiddenLayer);
    }

    private void createOutputLayer(int outputLayerDim) {
        outputLayer = new LinkedHashSet<OutputNeuron>(outputLayerDim);
        for (int i = 0; i < outputLayerDim; i++) {
            OutputNeuron outputNeuron = new OutputNeuron(outputBias);
            outputNeuron.linkToPrevLayer(hiddenLayer);
            outputLayer.add(outputNeuron);
        }
        //need to link previous layer with next layer
        for (HiddenNeuron hiddenNeuron : hiddenLayer)
            hiddenNeuron.linkToNextLayer(outputLayer);
    }

    public void saveNeuralNetwork(String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream outStream = new ObjectOutputStream(fileOut);

            //serialize weights map
            outStream.writeInt(Link.weights.size());
            for (Map.Entry<Link, Double> entrycouple : Link.weights.entrySet()) {
                Link link = entrycouple.getKey();
                outStream.writeObject(link.getPrev());
                outStream.writeObject(link.getNext());
                outStream.writeDouble(entrycouple.getValue());
            }

            //serialize input layer
            outStream.writeInt(inputLayer.size());
            for (InputNeuron inputNeuron : inputLayer)
                outStream.writeObject(inputNeuron);

            //serialize hidden layer
            outStream.writeInt(hiddenLayer.size());
            for (HiddenNeuron hiddenNeuron : hiddenLayer)
                outStream.writeObject(hiddenNeuron);

            //serialize output layer
            outStream.writeInt(outputLayer.size());
            for (OutputNeuron outputNeuron : outputLayer)
                outStream.writeObject(outputNeuron);

            outStream.close();
            fileOut.close();

            System.out.println("Neural network saved in " + fileName);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private void loadNeuralNetwork(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(fileIn);

        //deserialize weights map
        int weightsMapSize = in.readInt();
        for (int i = 0; i < weightsMapSize; i++) {
            Neuron prevNeuron = (Neuron) in.readObject();
            Neuron nextNeuron = (Neuron) in.readObject();
            Link link = new Link(prevNeuron, nextNeuron);
            Double weight = in.readDouble();
            Link.weights.put(link, weight);
        }

        //deserialize input layer
        int inputLayerDim = in.readInt();
        inputLayer = new LinkedHashSet<InputNeuron>(inputLayerDim);
        for (int i = 0; i < inputLayerDim; i++)
            inputLayer.add((InputNeuron) in.readObject());

        //deserialize hidden layer
        int hiddenLayerDim = in.readInt();
        hiddenLayer = new LinkedHashSet<HiddenNeuron>(hiddenLayerDim);
        for (int i = 0; i < hiddenLayerDim; i++)
            hiddenLayer.add((HiddenNeuron) in.readObject());

        //deserialize output layer
        int outputLayerDim = in.readInt();
        outputLayer = new LinkedHashSet<OutputNeuron>(outputLayerDim);
        for (int i = 0; i < outputLayerDim; i++)
            outputLayer.add((OutputNeuron) in.readObject());

        in.close();
        fileIn.close();

        System.out.println("Neural network loaded from " + fileName);
    }

    public void saveOutputLayerDeltaWeights(Map<Link, Double> deltaWeights, Map<OutputNeuron, Double> targetOutput) {

        //Calculates deltaWeigths for each node in the output layer
        for (OutputNeuron n : outputLayer) {
            double singleTargetOutput = targetOutput.get(n);
            n.computeDelta(singleTargetOutput);

            for (Link l : n.getPrevNeurons()) {
                Neuron prev = l.getPrev();
                double deltaWeight = (-eta) * n.getDelta() * prev.getOutput();
                deltaWeights.put(l, deltaWeight);
            }
        }
    }

    public void saveHiddenLayerDeltaWeights(Map<Link, Double> deltaWeights, Set<HiddenNeuron> layer) {

        //Calculates deltaWeigths for each node in the hidden layer
        for (HiddenNeuron n : hiddenLayer) {
            n.computeDelta();

            for (Link l : n.getPrevNeurons()) {
                Neuron prev = l.getPrev();
                double deltaWeight = (-eta) * n.getDelta() * prev.getOutput();
                deltaWeights.put(l, deltaWeight);
            }
        }
    }

    private void forwardLayerPass(Set<? extends Neuron> layer) {
        for (Neuron n : layer)
            n.forwardPass();
    }

    private void resetLayer(Set<? extends Neuron> layer) {
        for (Neuron n : layer)
            n.resetNet();
    }

    public void backPropagation(Map<OutputNeuron, Double> targetOutput) {

        //TODO: Bias update to be implemented

        Map<Link, Double> deltaWeights = new HashMap<Link, Double>();

        saveOutputLayerDeltaWeights(deltaWeights, targetOutput);
        saveHiddenLayerDeltaWeights(deltaWeights, hiddenLayer);

        for (Link l : deltaWeights.keySet())
            l.updateWeight(deltaWeights.get(l));
    }

    public Map<OutputNeuron, Double> forwardPropagation(NeuralNetworkInput input) {
        Iterator inputLayerIterator = inputLayer.iterator();
        Map<OutputNeuron, Double> outputs = new HashMap<OutputNeuron, Double>(outputLayer.size());

        if (inputLayer.size() != input.size())
            throw new IllegalArgumentException("Input layer's size does not match given input");

        List<Byte> inputList = input.getInputList();
        for (Byte d : inputList) {
            InputNeuron inputNeuron = (InputNeuron) inputLayerIterator.next();
            inputNeuron.setInput(d);
        }

        //Forward pass in the input layer
        forwardLayerPass(inputLayer);
        //Forward pass in the hidden layer
        forwardLayerPass(hiddenLayer);
        //Forward pass in the output layer
        forwardLayerPass(outputLayer);

        for (OutputNeuron outputNeuron : outputLayer)
            outputs.put(outputNeuron, outputNeuron.getOutput());

        return outputs;
    }

    public void resetNetwork() {
        //Sets all currentNets to 0
        resetLayer(inputLayer);
        resetLayer(hiddenLayer);
        resetLayer(outputLayer);
    }

    public Set<InputNeuron> getInputLayer() {
        return inputLayer;
    }

    public Set<HiddenNeuron> getHiddenLayer() {
        return hiddenLayer;
    }

    public Set<OutputNeuron> getOutputLayer() {
        return outputLayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NeuralNetwork that = (NeuralNetwork) o;

        if (inputLayer != null ? !inputLayer.equals(that.inputLayer) : that.inputLayer != null) return false;
        if (hiddenLayer != null ? !hiddenLayer.equals(that.hiddenLayer) : that.hiddenLayer != null) return false;
        return outputLayer != null ? outputLayer.equals(that.outputLayer) : that.outputLayer == null;
    }

    public static class SimpleNNInput implements NeuralNetworkInput {
        private final List<Byte> l;

        public SimpleNNInput(List<Byte> l) {
            this.l = l;
        }

        public List<Byte> getInputList() {
            return l;
        }

        public int size() {
            return l.size();
        }
    }

    public static int AND(byte a, byte b) {
        if (a == 1 && b == 1)
            return 1;
        else
            return 0;
    }

    public static int XOR(byte a, byte b) {
        if (a == b)
            return 0;
        else
            return 1;
    }

    public static byte getOneOrZero() {
        double seed = Math.random();

        if (seed <= 0.5)
            return 1;
        else
            return 0;
    }

    public static void main(String... args) {
        NeuralNetwork neuralNetwork = new NeuralNetwork(2, 8);
        //NeuralNetwork neuralNetwork = new NeuralNetwork("AND_NN.ser");
        OutputNeuron outputNeuron = null;

        for (OutputNeuron n : neuralNetwork.outputLayer)
            outputNeuron = n;

        for (int i = 0; i < 1000000; i++) {
            byte a = getOneOrZero();
            byte b = getOneOrZero();

            //byte a=(byte)(Math.random()*10),b =(byte)(Math.random()*10);
            //byte a=100,b=10;

            List<Byte> l = Arrays.asList(a, b);
            SimpleNNInput in = new SimpleNNInput(l);
            Map<OutputNeuron, Double> out = neuralNetwork.forwardPropagation(in);

            Map<OutputNeuron, Double> targetOutputs = new HashMap<OutputNeuron, Double>();


            //targetOutputs.put(outputNeuron,(double)a+b);
            targetOutputs.put(outputNeuron, (double) (AND(a, b)));
            //targetOutputs.put(outputNeuron,(double)(XOR(a,b)));

            neuralNetwork.backPropagation(targetOutputs);

            for (Neuron n : out.keySet())
                System.out.println(a + " AND " + b + " = " + out.get(n));

            /*for(Neuron n: out.keySet())
                System.out.println(a+" XOR "+ b+ " = "+out.get(n));*/
            
            /*for(Neuron n: out.keySet())
                System.out.println(a+" + "+ b+ " = "+out.get(n));*/
            neuralNetwork.resetNetwork();
        }

        neuralNetwork.saveNeuralNetwork("AND_NN.ser");
        //NeuralNetwork newNeuralNetwork = new NeuralNetwork("AND_NN.ser");
        //System.out.println(neuralNetwork.equals(newNeuralNetwork));
    }
}