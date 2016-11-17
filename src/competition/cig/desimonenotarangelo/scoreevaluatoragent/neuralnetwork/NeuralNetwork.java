
package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;

//Main Neural Network class: it takes the byte[][] environment of the game as input
//and outputs the predicted delta score for one action

import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.ActivationFunction;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.BentIdentity;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.HiddenNeuron;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.Neuron;

import java.io.*;
import java.util.*;

public class NeuralNetwork implements Serializable {
    private double hiddenBias = 0.35,
            outputBias = 0.60,
            eta = 0.0002;

    private Set<InputNeuron> inputLayer;
    private List<Set<HiddenNeuron>> hiddenLayers;
    private Set<OutputNeuron> outputLayer;

    //Structures used for caching values during forward and backward propagation
    protected static final Map<Neuron, Double> deltasCache = new HashMap<Neuron, Double>();
    protected static final Map<Neuron, Double> netsCache = new HashMap<Neuron, Double>();
    protected static final Map<Neuron, Double> finalOutputsCache = new HashMap<Neuron, Double>();
    protected static final Map<Link, Double> deltaWeightsCache = new HashMap<Link, Double>();

    public NeuralNetwork(int inputLayerDim, int hiddenLayerDim) {
        //forcing only one output
        this(inputLayerDim, hiddenLayerDim, 1);
    }

    public NeuralNetwork(String fileName) throws IOException, ClassNotFoundException {
        loadNeuralNetwork(fileName);
    }

    public NeuralNetwork(int inputLayerDim, int hiddenLayerDim, int outputLayerDim) {
        this(new NeuralNetworkBuilder().addInputLayer(inputLayerDim)
                .addHiddenLayer(hiddenLayerDim)
                .addOutputLayer(outputLayerDim));
    }

    public NeuralNetwork(NeuralNetworkBuilder neuralNetworkBuilder) {
        this.inputLayer = neuralNetworkBuilder.getInputLayer();
        this.hiddenLayers = neuralNetworkBuilder.getHiddenLayers();
        this.outputLayer = neuralNetworkBuilder.getOutputLayer();
        this.hiddenBias = neuralNetworkBuilder.getHiddenBias();
        this.outputBias = neuralNetworkBuilder.getOutputBias();
        this.eta = neuralNetworkBuilder.getEta();
    }

    public void saveNeuralNetwork(String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream outStream = new ObjectOutputStream(fileOut);

            //serialize double fields
            outStream.writeDouble(hiddenBias);
            outStream.writeDouble(outputBias);
            outStream.writeDouble(eta);

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

            //serialize hidden layers
            outStream.writeInt(hiddenLayers.size());
            for (Set<HiddenNeuron> hiddenLayer : hiddenLayers) {
                //serialize every hidden layer
                outStream.writeInt(hiddenLayer.size());
                for (HiddenNeuron hiddenNeuron : hiddenLayer)
                    outStream.writeObject(hiddenNeuron);
            }

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

        //deserialize double fields
        hiddenBias = in.readDouble();
        outputBias = in.readDouble();
        eta = in.readDouble();

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

        //deserialize hidden layers
        int hiddenLayersDim = in.readInt();
        hiddenLayers = new ArrayList<Set<HiddenNeuron>>(hiddenLayersDim);
        for (int i = 0; i < hiddenLayersDim; i++) {
            //deserialize hidden layer
            int hiddenLayerDim = in.readInt();
            Set<HiddenNeuron> hiddenLayer = new LinkedHashSet<HiddenNeuron>(hiddenLayerDim);
            for (int j = 0; j < hiddenLayerDim; j++)
                hiddenLayer.add((HiddenNeuron) in.readObject());
            hiddenLayers.add(hiddenLayer);
        }

        //deserialize output layer
        int outputLayerDim = in.readInt();
        outputLayer = new LinkedHashSet<OutputNeuron>(outputLayerDim);
        for (int i = 0; i < outputLayerDim; i++)
            outputLayer.add((OutputNeuron) in.readObject());

        in.close();
        fileIn.close();

        System.out.println("Neural network loaded from " + fileName);
    }

    public void saveOutputLayerDeltaWeights(Map<OutputNeuron, Double> targetOutput) {

        //Calculates deltaWeigths for each node in the output layer
        for (OutputNeuron n : outputLayer) {
            double singleTargetOutput = targetOutput.get(n);
            double singleDelta = n.computeDelta(singleTargetOutput);
            deltasCache.put(n, singleDelta);

            for (Link l : n.getPrevNeurons()) {
                Neuron prev = l.getPrev();
                double deltaWeight = (-eta) * singleDelta * prev.computeOutput(netsCache.get(prev));
                deltaWeightsCache.put(l, deltaWeight);
            }
        }
    }

    public void saveHiddenLayerDeltaWeights(Set<HiddenNeuron> currHiddenLayer) {
        //Calculates deltaWeigths for each node in the hidden layer
        for (HiddenNeuron n : currHiddenLayer) {
            deltasCache.put(n, n.computeDelta());
            for (Link l : n.getPrevNeurons()) {
                Neuron prev = l.getPrev();
                double deltaWeight = (-eta) * deltasCache.get(n) * prev.computeOutput(netsCache.get(prev));
                deltaWeightsCache.put(l, deltaWeight);
            }
        }
    }

    public void backPropagation(NeuralNetworkOutput nnOutput, Map<OutputNeuron, Double> targetOutput) {

        //TODO: Bias update to be implemented

        //Copies Values from input into the cache
        finalOutputsCache.putAll(nnOutput.getFinalOutputs());
        netsCache.putAll(nnOutput.getNets());
        
        /*for(OutputNeuron n : targetOutput.keySet())
            System.out.println("Target OutPut: " + targetOutput.get(n));
        
        for(OutputNeuron n : finalOutputs.keySet())
            System.out.println("OutPut: " + finalOutputs.get(n));
        System.out.println("-----------------------------");
        */

        saveOutputLayerDeltaWeights(targetOutput);
        for (int i = hiddenLayers.size() - 1; i >= 0; i--) {
            Set<HiddenNeuron> hiddenLayer = hiddenLayers.get(i);
            saveHiddenLayerDeltaWeights(hiddenLayer);
        }

        for (Link l : deltaWeightsCache.keySet())
            l.updateWeight(deltaWeightsCache.get(l));

        cleanCacheAfterBackwardPropagation();
    }

    private void cleanCacheAfterBackwardPropagation() {
        deltasCache.clear();
        netsCache.clear();
        deltaWeightsCache.clear();
    }

    public NeuralNetworkOutput forwardPropagation(NeuralNetworkInput input) {
        Iterator inputLayerIterator = inputLayer.iterator();

        if (inputLayer.size() != input.size())
            throw new IllegalArgumentException("Input layer's size does not match given input");

        List<Double> inputList = input.getInputList();

        //For each input neuron we set input value as its net sum
        for (Double d : inputList) {
            InputNeuron inputNeuron = (InputNeuron) inputLayerIterator.next();
            netsCache.put(inputNeuron, d);
        }

        //Forward pass in the input layer
        forwardLayerPass(inputLayer);
        //Forward pass in every hidden layer
        for (Set<HiddenNeuron> hiddenLayer : hiddenLayers)
            forwardLayerPass(hiddenLayer);
        //Forward pass in the output layer
        forwardLayerPass(outputLayer);

        return getOutputAndClean();
    }

    private void forwardLayerPass(Set<? extends Neuron> layer) {
        for (Neuron n : layer)
            n.forwardPass();
    }

    public NeuralNetworkOutput getOutputAndClean() {
        Map<OutputNeuron, Double> finalOutputs = new HashMap<OutputNeuron, Double>();
        Map<Neuron, Double> nets = new HashMap<Neuron, Double>();

        for (Neuron n : inputLayer) {
            nets.put(n, netsCache.get(n));
            netsCache.remove(n);
        }

        for (Set<HiddenNeuron> hiddenLayer : hiddenLayers) {
            for (Neuron n : hiddenLayer) {
                nets.put(n, netsCache.get(n));
                netsCache.remove(n);
            }
        }

        for (OutputNeuron n : outputLayer) {
            nets.put(n, netsCache.get(n));
            finalOutputs.put(n, finalOutputsCache.get(n));
            netsCache.remove(n);
            finalOutputsCache.remove(n);
        }

        return new NeuralNetworkOutput(finalOutputs, nets);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NeuralNetwork that = (NeuralNetwork) o;

        if (Double.compare(that.hiddenBias, hiddenBias) != 0) return false;
        if (Double.compare(that.outputBias, outputBias) != 0) return false;
        if (Double.compare(that.eta, eta) != 0) return false;
        if (!inputLayer.equals(that.inputLayer)) return false;
        if (!hiddenLayers.equals(that.hiddenLayers)) return false;
        return outputLayer.equals(that.outputLayer);
    }

    public static class SimpleNNInput implements NeuralNetworkInput {
        private final List<Double> l;

        public SimpleNNInput(List<Double> l) {
            this.l = l;
        }

        public List<Double> getInputList() {
            return l;
        }

        public int size() {
            return l.size();
        }
    }

    public static double AND(double a, double b) {
        if (a == 1.0 && b == 1.0)
            return 1.0;
        else
            return 0.0;
    }

    public static int XOR(double a, double b) {
        if (a == b)
            return 0;
        else
            return 1;
    }

    public static double getOneOrZero() {
        double seed = Math.random();

        if (seed <= 0.5)
            return 1;
        else
            return 0;
    }

    public static void main(String... args) {
        NeuralNetwork neuralNetwork;
        try {
            neuralNetwork = new NeuralNetwork("SUM_NN.ai");
        } catch (IOException | ClassNotFoundException e) {
            ActivationFunction activationFunction = new BentIdentity();
            neuralNetwork = new NeuralNetworkBuilder()
                    .setInputLayerActivationFunction(activationFunction)
                    .setHiddenLayersActivationFunction(activationFunction)
                    .setOutputLayerActivationFunction(activationFunction)
                    .addInputLayer(2)
                    .addHiddenLayer(8)
                    .addHiddenLayer(8)
                    .addOutputLayer(1)
                    .setEta(0.0000002)
                    .build();
            System.out.println("Creating new neural network");
        }
        OutputNeuron outputNeuron = null;

        for (OutputNeuron n : neuralNetwork.outputLayer)
            outputNeuron = n;

        for (int i = 0; i < 10000; i++) {
            //double a = getOneOrZero();
            //double b = getOneOrZero();

            double a = (Math.random() * 10), b = (Math.random() * 10);
            //byte a=18,b=11;

            List<Double> l = Arrays.asList(a, b);
            SimpleNNInput in = new SimpleNNInput(l);
            NeuralNetworkOutput out = neuralNetwork.forwardPropagation(in);

            Map<OutputNeuron, Double> targetOutputs = new HashMap<OutputNeuron, Double>();

            targetOutputs.put(outputNeuron, a + b);
            //targetOutputs.put(outputNeuron,(AND(a, b)));
            //targetOutputs.put(outputNeuron,(double)(XOR(a,b)));

            neuralNetwork.backPropagation(out, targetOutputs);

            //for (Neuron n : out.getFinalOutputs().keySet())
            //    System.out.println(a + " AND " + b + " = " + out.getFinalOutputs().get(n));
            
            /*for(Neuron n: out.keySet())
                System.out.println(a+" XOR "+ b+ " = "+out.get(n));*/

            for (Neuron n : out.getFinalOutputs().keySet())
                System.out.println(a + " + " + b + " = " + out.getFinalOutputs().get(n));
        }

        neuralNetwork.saveNeuralNetwork("SUM_NN.ai");
        NeuralNetwork newNeuralNetwork = null;
        try {
            newNeuralNetwork = new NeuralNetwork("SUM_NN.ai");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(neuralNetwork.equals(newNeuralNetwork));
    }
}