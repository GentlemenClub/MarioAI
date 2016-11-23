
package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;

//Main Neural Network class: it takes the byte[][] environment of the game as input
//and outputs the predicted delta score for one action

import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.ActivationFunction;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.BentIdentity;

import java.io.*;
import java.util.*;

public class NeuralNetwork implements Serializable {
    private double eta = 0.0002,
            dropoutPercentage = 0;

    private Set<InputNeuron> inputLayer;
    private List<Set<HiddenNeuron>> hiddenLayers;
    private Set<OutputNeuron> outputLayer;

    //Structures used for caching values during forward and backward propagation
    protected static final Map<Neuron, Double> deltasCache = new HashMap<Neuron, Double>();
    protected static final Map<Neuron, Double> netsCache = new HashMap<Neuron, Double>();
    protected static final Map<Neuron, Double> finalOutputsCache = new HashMap<Neuron, Double>();
    protected static final Map<Neuron, Double> deltaBiasesCache = new HashMap<Neuron, Double>();
    protected static final Map<Link, Double> deltaWeightsCache = new HashMap<Link, Double>();
    protected static final Set<Neuron> dropoutMaskCache = new HashSet<Neuron>();

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
        this.eta = neuralNetworkBuilder.getEta();
        this.dropoutPercentage = neuralNetworkBuilder.getDropoutPercentage();
    }

    public void saveNeuralNetwork(String fileName) {
        try {
            // Get current time
            long start = System.currentTimeMillis();
            FileOutputStream fileOut = new FileOutputStream(fileName);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOut);
            ObjectOutputStream outStream = new ObjectOutputStream(bufferedOutputStream);

            //serialize double fields
            outStream.writeDouble(eta);
            outStream.writeDouble(dropoutPercentage);

            //serialize weights map
            outStream.writeInt(Link.weights.size());
            for (Map.Entry<Link, Double> entryCouple : Link.weights.entrySet()) {
                outStream.writeObject(entryCouple.getKey());
                outStream.writeDouble(entryCouple.getValue());
            }

            //serialize input layer
            outStream.writeInt(inputLayer.size());
            for (InputNeuron inputNeuron : inputLayer) {
                outStream.writeObject(inputNeuron);
                Set<Link> nextNeurons = inputNeuron.getNextNeurons();
                outStream.writeInt(nextNeurons.size());
                for (Link link : nextNeurons)
                    outStream.writeObject(link);
            }
            //serialize hidden layers
            outStream.writeInt(hiddenLayers.size());
            for (Set<HiddenNeuron> hiddenLayer : hiddenLayers) {
                //serialize every hidden layer
                outStream.writeInt(hiddenLayer.size());
                for (HiddenNeuron hiddenNeuron : hiddenLayer) {
                    outStream.writeObject(hiddenNeuron);
                    Set<Link> prevNeurons = hiddenNeuron.getPrevNeurons();
                    Set<Link> nextNeurons = hiddenNeuron.getNextNeurons();
                    outStream.writeInt(prevNeurons.size());
                    for (Link link : prevNeurons)
                        outStream.writeObject(link);
                    outStream.writeInt(nextNeurons.size());
                    for (Link link : nextNeurons)
                        outStream.writeObject(link);
                }
            }

            //serialize output layer
            outStream.writeInt(outputLayer.size());
            for (OutputNeuron outputNeuron : outputLayer) {
                outStream.writeObject(outputNeuron);
                Set<Link> prevNeurons = outputNeuron.getPrevNeurons();
                outStream.writeInt(prevNeurons.size());
                for (Link link : prevNeurons)
                    outStream.writeObject(link);
            }

            outStream.close();
            bufferedOutputStream.close();
            fileOut.close();
            // Get elapsed time in milliseconds
            long elapsedTimeMillis = System.currentTimeMillis() - start;

            System.out.println("Neural network saved in " + fileName + " in " + elapsedTimeMillis + " ms");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private void loadNeuralNetwork(String fileName) throws IOException, ClassNotFoundException {
        // Get current time
        long start = System.currentTimeMillis();
        FileInputStream fileIn = new FileInputStream(fileName);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileIn);
        ObjectInputStream inStream = new ObjectInputStream(bufferedInputStream);

        //deserialize double fields
        eta = inStream.readDouble();
        dropoutPercentage = inStream.readDouble();

        //deserialize weights map
        int weightsMapSize = inStream.readInt();
        for (int i = 0; i < weightsMapSize; i++) {
            Link link = (Link) inStream.readObject();
            Double weight = inStream.readDouble();
            Link.weights.put(link, weight);
        }

        //deserialize input layer
        int inputLayerDim = inStream.readInt();
        inputLayer = new LinkedHashSet<InputNeuron>(inputLayerDim);
        for (int i = 0; i < inputLayerDim; i++) {
            InputNeuron inputNeuron = (InputNeuron) inStream.readObject();
            int nextNeuronsSize = inStream.readInt();
            Set<Link> nextNeurons = new HashSet<Link>(nextNeuronsSize);
            for (int j = 0; j < nextNeuronsSize; j++)
                nextNeurons.add((Link) inStream.readObject());
            inputNeuron.setNextNeurons(nextNeurons);
            inputLayer.add(inputNeuron);
        }

        //deserialize hidden layers
        int hiddenLayersDim = inStream.readInt();
        hiddenLayers = new ArrayList<Set<HiddenNeuron>>(hiddenLayersDim);
        for (int i = 0; i < hiddenLayersDim; i++) {
            //deserialize hidden layer
            int hiddenLayerDim = inStream.readInt();
            Set<HiddenNeuron> hiddenLayer = new LinkedHashSet<HiddenNeuron>(hiddenLayerDim);
            for (int j = 0; j < hiddenLayerDim; j++) {
                HiddenNeuron hiddenNeuron = (HiddenNeuron) inStream.readObject();

                int prevNeuronsSize = inStream.readInt();
                Set<Link> prevNeurons = new HashSet<Link>(prevNeuronsSize);
                for (int k = 0; k < prevNeuronsSize; k++)
                    prevNeurons.add((Link) inStream.readObject());
                hiddenNeuron.setPrevNeurons(prevNeurons);

                int nextNeuronsSize = inStream.readInt();
                Set<Link> nextNeurons = new HashSet<Link>(nextNeuronsSize);
                for (int l = 0; l < nextNeuronsSize; l++)
                    nextNeurons.add((Link) inStream.readObject());
                hiddenNeuron.setNextNeurons(nextNeurons);

                hiddenLayer.add(hiddenNeuron);
            }
            hiddenLayers.add(hiddenLayer);
        }

        //deserialize output layer
        int outputLayerDim = inStream.readInt();
        outputLayer = new LinkedHashSet<OutputNeuron>(outputLayerDim);
        for (int i = 0; i < outputLayerDim; i++) {
            OutputNeuron outputNeuron = (OutputNeuron) inStream.readObject();
            int prevNeuronsSize = inStream.readInt();
            Set<Link> prevNeurons = new HashSet<Link>(prevNeuronsSize);
            for (int j = 0; j < prevNeuronsSize; j++)
                prevNeurons.add((Link) inStream.readObject());
            outputNeuron.setPrevNeurons(prevNeurons);
            outputLayer.add(outputNeuron);
        }

        inStream.close();
        bufferedInputStream.close();
        fileIn.close();
        // Get elapsed time in milliseconds
        long elapsedTimeMillis = System.currentTimeMillis() - start;

        System.out.println("Neural network loaded from " + fileName + " in " + elapsedTimeMillis + " ms");
    }

    public void saveOutputLayerDeltaWeightsAndBiases(Map<OutputNeuron, Double> targetOutput) {

        //Calculates deltaWeigths for each node in the output layer
        for (OutputNeuron n : outputLayer) {
            double singleTargetOutput = targetOutput.get(n);
            double singleDelta = n.computeDelta(singleTargetOutput);
            deltasCache.put(n, singleDelta);

            double deltaBias = (-eta) * singleDelta;
            deltaBiasesCache.put(n, deltaBias);

            for (Link l : n.getPrevNeurons()) {
                Neuron prev = l.getPrev();
    
                //if this prev neuron was dropped out, link with prev must not be updated
                if(dropoutMaskCache.contains(prev))
                    continue;

                double deltaWeight = deltaBias * prev.computeOutput(netsCache.get(prev));
                deltaWeightsCache.put(l, deltaWeight);
            }
        }
    }

    public void saveHiddenLayerDeltaWeightsandBiases(Set<HiddenNeuron> currHiddenLayer) {
        //Calculates deltaWeigths for each node in the hidden layer
        for (HiddenNeuron n : currHiddenLayer) {
            
            //If neuron was dropped out, no update must be done
            if(dropoutMaskCache.contains(n))
                continue;
            
            deltasCache.put(n, n.computeDelta());
            double deltaBias = (-eta) * n.computeDelta();
            deltaBiasesCache.put(n, deltaBias);

            for (Link l : n.getPrevNeurons()) {
                Neuron prev = l.getPrev();
                
                //if this prev neuron was dropped out, link with prev must not be updated
                if(dropoutMaskCache.contains(prev))
                    continue;
                
                double deltaWeight = (-eta) * deltasCache.get(n) * prev.computeOutput(netsCache.get(prev));
                deltaWeightsCache.put(l, deltaWeight);
            }
        }
    }

    public void backPropagation(NeuralNetworkOutput nnOutput, Map<OutputNeuron, Double> targetOutput) {

        //Copies Values from input into the cache
        finalOutputsCache.putAll(nnOutput.getFinalOutputs());
        netsCache.putAll(nnOutput.getNets());
        dropoutMaskCache.addAll(nnOutput.getDropoutMask());
        /*for(OutputNeuron n : targetOutput.keySet())
            System.out.println("Target OutPut: " + targetOutput.get(n));
        
        for(OutputNeuron n : finalOutputs.keySet())
            System.out.println("OutPut: " + finalOutputs.get(n));
        System.out.println("-----------------------------");
        */

        saveOutputLayerDeltaWeightsAndBiases(targetOutput);
        for (int i = hiddenLayers.size() - 1; i >= 0; i--) {
            Set<HiddenNeuron> hiddenLayer = hiddenLayers.get(i);
            saveHiddenLayerDeltaWeightsandBiases(hiddenLayer);
        }

        for (Link l : deltaWeightsCache.keySet())
            l.updateWeight(deltaWeightsCache.get(l));

        for (Neuron n : deltaBiasesCache.keySet()) {
            double deltaBias = deltaBiasesCache.get(n);
            n.updateBias(deltaBias);
        }

        cleanCacheAfterBackwardPropagation();
    }

    private void cleanCacheAfterBackwardPropagation() {
        deltasCache.clear();
        deltaBiasesCache.clear();
        netsCache.clear();
        deltaWeightsCache.clear();
        dropoutMaskCache.clear();
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
        int nDroppedOutNodes = 0;
        for (Neuron n : layer)
        {
            if (n instanceof HiddenNeuron
                    && Math.random() < dropoutPercentage
                    && nDroppedOutNodes < layer.size())
            {
                nDroppedOutNodes++;
                dropoutMaskCache.add(n);
            }
            else
                n.forwardPass();
        }
    }

    public NeuralNetworkOutput getOutputAndClean() {
        Map<OutputNeuron, Double> finalOutputs = new HashMap<OutputNeuron, Double>();
        Map<Neuron, Double> finalNets = new HashMap<Neuron, Double>();
        Set<Neuron> finalDropoutMask = new HashSet<Neuron>();

        deltasCache.clear();
        deltaBiasesCache.clear();
        deltaWeightsCache.clear();

        for (Neuron n : inputLayer) {
            finalNets.put(n, netsCache.get(n));
            netsCache.remove(n);
        }

        for (Set<HiddenNeuron> hiddenLayer : hiddenLayers) {
            for (Neuron n : hiddenLayer) {
                finalNets.put(n, netsCache.get(n));
                netsCache.remove(n);
                if (dropoutMaskCache.contains(n))
                {
                    finalDropoutMask.add(n);
                    dropoutMaskCache.remove(n);
                }
            }
        }

        for (OutputNeuron n : outputLayer) {
            finalNets.put(n, netsCache.get(n));
            finalOutputs.put(n, finalOutputsCache.get(n));
            finalOutputsCache.remove(n);
            netsCache.remove(n);
        }

        return new NeuralNetworkOutput(finalOutputs, finalNets, finalDropoutMask);
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
        
        if (Double.compare(that.eta, eta) != 0) return false;
        if (Double.compare(that.dropoutPercentage, dropoutPercentage) != 0) return false;
        if (inputLayer != null ? !inputLayer.equals(that.inputLayer) : that.inputLayer != null) return false;
        if (hiddenLayers != null ? !hiddenLayers.equals(that.hiddenLayers) : that.hiddenLayers != null) return false;
        return outputLayer != null ? outputLayer.equals(that.outputLayer) : that.outputLayer == null;
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
                    .addHiddenLayer(400)
                    .addHiddenLayer(300)
                    .addOutputLayer(1)
                    .setEta(0.0000002)
                    .build();
            System.out.println("Creating new neural network");
        }
        OutputNeuron outputNeuron = null;

        for (OutputNeuron n : neuralNetwork.outputLayer)
            outputNeuron = n;

        for (int i = 0; i < 1; i++) {
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