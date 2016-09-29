
package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

//Main Neural Network class: it takes the byte[][] environment of the game as input
//and outputs the predicted delta score for one action

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NeuralNetwork
{
  public static final double hiddenBias = 0.35,
                             outputBias = 0.60,
                             eta        = 0.20;
  
  Set<InputNeuron> inputLayer;
  Set<HiddenNeuron> hiddenLayer;
  Set<OutputNeuron> outputLayer;
  
  public NeuralNetwork(int inputLayerDim, int hiddenLayerDim)
  {
      
  }
  
  public void saveDeltaWeights(Map<Link,Double> deltaWeights, Set<? extends Neuron> layer)
  {
    //Calculates deltaWeigths for each node in the layer
    for (Neuron n : layer)
    {
      double deltaWeight = (-eta)*n.delta()*n.getCurrentNet();
      for (Link l : n.getPrevNeurons())
        deltaWeights.put(l,deltaWeight);
    }
  }
  
  public void backPropagation(double targetOutput)
  {
    //TODO: Bias update to be implemented
    
    Map<Link,Double> deltaWeights = new HashMap<Link,Double>();
    
    saveDeltaWeights(deltaWeights,outputLayer);
    saveDeltaWeights(deltaWeights,hiddenLayer);
  
    for( Link l : deltaWeights.keySet())
      l.updateWeight(deltaWeights.get(l));
    
  }
  
}