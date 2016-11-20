package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;

import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.SigmoidFunction;

import java.util.Map;

public class InputBiasNeuron extends InputNeuron
{
  public InputBiasNeuron()
  {
    super(0, new SigmoidFunction());
  }
  
  /*public void forwardPass() {
    Map<Neuron,Double> nets = NeuralNetwork.netsCache;
    for(Link link: nextNeurons) {
      Neuron currNext = link.getNext();
      double singleNet = nets.get(this);
      Double nextNet = nets.get(currNext);
      //If it is the first time you add net to the neuron, the net must be initialized to 0
      if(nextNet==null)
        nextNet=0.0;
      
      nets.put(currNext, nextNet + singleNet * link.getWeight());
    }*/
}
