package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.*;

public class NeuralNetworkOutput
{
  private final Map<OutputNeuron,Double> finalOutputs;
  private final Map<Neuron,Double> nets;
  
  public NeuralNetworkOutput(Map<OutputNeuron,Double> outputs, Map<Neuron,Double> nets)
  {
    this.finalOutputs =outputs;
    this.nets=nets;
  }
  
  public Map<Neuron,Double> getNets()//Returns net for each node in the network
  {return nets;}
  
  public Map<OutputNeuron,Double> getFinalOutputs()///Returns output for each output neuron
  {return finalOutputs;}
  
  public OutputNeuron getMaxValueNeuron()
  {
    double currMax = Double.NEGATIVE_INFINITY;
    OutputNeuron maxValueNeuron = null;
    
    for(OutputNeuron n: finalOutputs.keySet())
    {
      double currValue = finalOutputs.get(n);
      if(currMax<currValue)
      {
        maxValueNeuron = n;
        currMax = currValue;
      }
    }
    return maxValueNeuron;
  }
  
  public double getRandomValue()
  {
    List<Double> valuesList = new ArrayList<Double>(finalOutputs.values());
    int randomIndex = new Random().nextInt(valuesList.size());
    return valuesList.get(randomIndex);
  }
  
  public Map<OutputNeuron,Double> getFinalOutputs(OutputNeuron n)
  {
    return finalOutputs;
  }
  
  public double getValue(OutputNeuron n)
  {
    return finalOutputs.get(n);
  }
}
