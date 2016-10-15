package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.*;

public class NeuralNetworkBuilder
{
    private Set<InputNeuron> inputLayer = new LinkedHashSet<InputNeuron>();
    private List<Set<HiddenNeuron>> hiddenLayers = new ArrayList<Set<HiddenNeuron>>();
    private Set<OutputNeuron> outputLayer = new LinkedHashSet<OutputNeuron>();;
  
    public double hiddenBias = 0.35,
          outputBias = 0.60,
          eta = 0.0002;
  
    public NeuralNetworkBuilder addInputLayer(int inputLayerDim)
    {
      if(!inputLayer.isEmpty())
        throw new IllegalStateException("Input Layer already added");
      
      for (int i = 0; i < inputLayerDim; i++)
        inputLayer.add(new InputNeuron(1));
      
      return this;
    }
  
  public NeuralNetworkBuilder setHiddenLayerBias()
  {
    
  }
  }
  
  public NeuralNetworkBuilder setOutputLayerBias()
  {
  }
  
  public NeuralNetworkBuilder addHiddenLayer(int hiddenLayerDim)
  {
    if(inputLayer.isEmpty())
      throw new IllegalStateException("Missing Input Layer");
  
    for (int i = 0; i < hiddenLayerDim; i++) {
      HiddenNeuron hiddenNeuron = new HiddenNeuron(hiddenBias);
      hiddenNeuron.linkToPrevLayer(inputLayer);
      hiddenLayer.add(hiddenNeuron);
    
    
    return this;
  }
    
   
}
