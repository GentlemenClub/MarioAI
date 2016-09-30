
package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

//Main Neural Network class: it takes the byte[][] environment of the game as input
//and outputs the predicted delta score for one action

import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;

import java.io.File;
import java.util.*;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.iterator;

public class NeuralNetwork {
    public static final double hiddenBias = 0.35,
            outputBias = 0.60,
            eta = 0.20;

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
            inputLayer.add(new InputNeuron());
    }

    private void createHiddenLayer(int hiddenLayerDim) {
        hiddenLayer = new LinkedHashSet<HiddenNeuron>(hiddenLayerDim);
        for (int i = 0; i < hiddenLayerDim; i++) {
            HiddenNeuron hiddenNeuron = new HiddenNeuron();
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
            OutputNeuron outputNeuron = new OutputNeuron();
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

    public void saveHiddenLayerDeltaWeights(Map<Link, Double> deltaWeights,
                                 Set<? extends Neuron> layer,
                                 Map<Neuron,Double> targetOutput ) {
        
        //Calculates deltaWeigths for each node in the layer
        for (Neuron n : layer) {
            double singleTargetOutput = targetOutput.get(n);
            double deltaWeight = (-eta) * n.delta(singleTargetOutput) * n.getCurrentNet();
            for (Link l : n.getPrevNeurons())
                deltaWeights.put(l, deltaWeight);
        }
    }
    
    public void saveOutputLayerDeltaWeights(Map<Link, Double> deltaWeights,
                                 Set<? extends Neuron> layer,
                                 Map<Neuron,Double> targetOutput ) {
        
        //Calculates deltaWeigths for each node in the layer
        for (Neuron n : layer) {
            double singleTargetOutput = targetOutput.get(n);
            double deltaWeight = (-eta) * n.delta(singleTargetOutput) * n.getCurrentNet();
            for (Link l : n.getPrevNeurons())
                deltaWeights.put(l, deltaWeight);
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
    
    public void backPropagation(Map<Neuron,Double> targetOutput) {
        
        //TODO: Bias update to be implemented
        
        Map<Link, Double> deltaWeights = new HashMap<Link, Double>();

        saveDeltaWeights(deltaWeights, outputLayer, targetOutput);
        saveDeltaWeights(deltaWeights, hiddenLayer, targetOutput);

        for (Link l : deltaWeights.keySet())
            l.updateWeight(deltaWeights.get(l));
    }

    public Map<Neuron,Double> forwardPropagation(NeuralNetworkInput input) {
    
        Iterator inputLayerIterator = inputLayer.iterator();
        Map<Neuron,Double> outputs = new HashMap<Neuron,Double>(outputLayer.size());
        
        if(inputLayer.size()!= input.size())
          throw new IllegalArgumentException("Input layer's size does not match given input");
        
        //Sets input environment into the input layer
        //for(int i=0;i <input.size(); i++)
        //    for(int j=0;j <input.size(); j++) {
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
        
        for (OutputNeuron outputNeuron : outputLayer)
            outputs.put(outputNeuron,outputNeuron.getFinalOutput());
        
        return outputs;
    }
    
    public void resetNetwork()
    {
        //Sets all currentNets to 0
        resetLayer(inputLayer);
        resetLayer(hiddenLayer);
        resetLayer(outputLayer);
    }
    
    public static class SumNNInput implements NeuralNetworkInput
    {
        private final List<Byte> l;
                
        public SumNNInput(List<Byte> l)
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
    
    public static void main (String... args) {
        
        NeuralNetwork neuralNetwork = new NeuralNetwork(2, 4);
        Neuron outputNeuron=null;
        
        for(OutputNeuron n:neuralNetwork.outputLayer)
            outputNeuron=n;
        
        for(int i=0; i<10000; i++)
        {
            byte a=(byte)(Math.random()*10),b =(byte)(Math.random()*10);
            
            List<Byte> l= Arrays.asList(a,b);
            SumNNInput in = new SumNNInput(l);
            Map <Neuron, Double> out = neuralNetwork.forwardPropagation(in);
            
            Map<Neuron,Double> targetOutputs = new HashMap <Neuron, Double>();
            
            targetOutputs.put(outputNeuron,(double)a+b);
            neuralNetwork.backPropagation(targetOutputs);
            
            for(Neuron n: out.keySet())
                System.out.println(a+" + "+ b+ " = "+out.get(n));
        }
    }

}