package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.Map;

public class NeuralNetworkOutput
{
  private final Map<OutputNeuron,Double> finalOutputs;
  private final Map<Neuron,Double> nets;
  
  public NeuralNetworkOutput(Map<OutputNeuron,Double> outputs, Map<Neuron,Double> nets)
  {
    this.finalOutputs =outputs;
    this.nets=nets;
  }
  
  protected Map<Neuron,Double> getNets()//Returns net for each node in the network
  {return nets;}
  
  protected Map<OutputNeuron,Double> getFinalOutputs()///Returns output for each output neuron
  {return finalOutputs;}
}
