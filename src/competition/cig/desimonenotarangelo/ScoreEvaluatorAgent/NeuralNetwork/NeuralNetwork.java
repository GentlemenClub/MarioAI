
package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

//Main Neural Network class: it takes the byte[][] environment of the game as input
//and outputs the predicted delta score for one action

import java.io.File;
import java.util.*;

public class NeuralNetwork {
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

    public NeuralNetwork(File file) {
        loadNeuralNetwork(file);
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

    private void saveNeuralNetwork(File file) {
    }

    private void loadNeuralNetwork(File file) {

    }
    
    public void saveOutputLayerDeltaWeights(Map<Link, Double> deltaWeights, Map<OutputNeuron,Double> targetOutput ) {
        
        //Calculates deltaWeigths for each node in the output layer
        for (OutputNeuron n : outputLayer) {
            double singleTargetOutput = targetOutput.get(n);
            n.computeDelta(singleTargetOutput);
            
            for (Link l : n.getPrevNeurons())
            {
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
            
            for (Link l : n.getPrevNeurons())
            {
                Neuron prev = l.getPrev();
                double deltaWeight = (-eta) * n.getDelta() * prev.getOutput();
                deltaWeights.put(l, deltaWeight);
            }
        }
    }
    
    private void forwardLayerPass(Set<? extends Neuron> layer)
    {
        for (Neuron n : layer)
            n.forwardPass();
    }
    
    private void resetLayer(Set<? extends Neuron> layer)
    {
        for (Neuron n : layer)
            n.resetNet();
    }
    
    public void backPropagation(Map<OutputNeuron,Double> targetOutput) {
        
        //TODO: Bias update to be implemented
        
        Map<Link, Double> deltaWeights = new HashMap<Link, Double>();
    
        saveOutputLayerDeltaWeights(deltaWeights, targetOutput);
        saveHiddenLayerDeltaWeights(deltaWeights, hiddenLayer);

        for (Link l : deltaWeights.keySet())
            l.updateWeight(deltaWeights.get(l));
    }

    public Map<OutputNeuron,Double> forwardPropagation(NeuralNetworkInput input) {
    
        Iterator inputLayerIterator = inputLayer.iterator();
        Map<OutputNeuron,Double> outputs = new HashMap<OutputNeuron,Double>(outputLayer.size());
        
        if(inputLayer.size()!= input.size())
          throw new IllegalArgumentException("Input layer's size does not match given input");
        
        List<Byte> inputList = input.getInputList();
        for(Byte d: inputList)
        {
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
            outputs.put(outputNeuron,outputNeuron.getOutput());
        
        return outputs;
    }
    
    public void resetNetwork()
    {
        //Sets all currentNets to 0
        resetLayer(inputLayer);
        resetLayer(hiddenLayer);
        resetLayer(outputLayer);
    }
    
    public static class SimpleNNInput implements NeuralNetworkInput
    {
        private final List<Byte> l;
                
        public SimpleNNInput(List<Byte> l)
        {
            this.l=l;
        }
        
        public List<Byte> getInputList()
        {
            return l;
        }
    
        public int size()
        {
            return l.size();
        }
    }
    
    public static int AND (byte a, byte b)
    {
        if(a==1 && b ==1)
            return 1;
        else
            return 0;
    }
    
    public static int XOR (byte a, byte b)
    {
        if(a==b)
            return 0;
        else
            return 1;
    }
    
    public static byte getOneOrZero()
    {
        double seed =Math.random();
        
        if(seed<=0.5)
            return 1;
        else
            return 0;
    }
    
    
    public static void main (String... args) {
        
        NeuralNetwork neuralNetwork = new NeuralNetwork(2, 10);
        OutputNeuron outputNeuron=null;
        
        for(OutputNeuron n:neuralNetwork.outputLayer)
            outputNeuron=n;
        
        for(int i=0; i<10000000; i++)
        {
            //byte a = getOneOrZero();
            //byte b = getOneOrZero();
            
           byte a=(byte)(Math.random()*10),b =(byte)(Math.random()*10);
            //byte a=100,b=10;
            
            List<Byte> l= Arrays.asList(a,b);
            SimpleNNInput in = new SimpleNNInput(l);
            Map <OutputNeuron, Double> out = neuralNetwork.forwardPropagation(in);
            
            Map<OutputNeuron,Double> targetOutputs = new HashMap <OutputNeuron, Double>();
            
            targetOutputs.put(outputNeuron,(double)a+b);
            //targetOutputs.put(outputNeuron,(double)(AND(a,b)));
            //targetOutputs.put(outputNeuron,(double)(XOR(a,b)));
    
            neuralNetwork.backPropagation(targetOutputs);
    
            /*for(Neuron n: out.keySet())
              System.out.println(a+" AND "+ b+ " = "+out.get(n));
            */
            //for(Neuron n: out.keySet())
            //    System.out.println(a+" XOR "+ b+ " = "+out.get(n));
            
            for(Neuron n: out.keySet())
                System.out.println(a+" + "+ b+ " = "+out.get(n));
            neuralNetwork.resetNetwork();
            int ba=4;
        }
    }
}