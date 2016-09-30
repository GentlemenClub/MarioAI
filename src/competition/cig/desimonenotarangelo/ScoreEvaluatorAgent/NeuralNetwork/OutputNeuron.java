package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.HashSet;
import java.util.Set;

public class OutputNeuron extends Neuron
{
  protected final Set<Link> prevNeurons;
  
  public OutputNeuron() { prevNeurons = new HashSet<Link>(); }
  
  public void forwardPass() { throw new UnsupportedOperationException();}
  public void addNext(Neuron prev){ throw new UnsupportedOperationException();}

  public void linkToNextLayer(Set<? extends Neuron> layer){ throw new UnsupportedOperationException();}

  public void linkToPrevLayer(Set<? extends Neuron> layer) {
    for (Neuron neuron : layer)
        addPrev(neuron);
  }

  public double getFinalOutput()
  {
    double biasedNet = currentNet + NeuralNetwork.outputBias;
    return 1/(1+Math.exp(-biasedNet));//sigmoid function to output
  }
  
  public void addPrev(Neuron prev) { prevNeurons.add(new Link(prev,this)); }
  
  protected double delta(double singleTargetOutput)
  {
    double output = getFinalOutput();
    return output*(1-output)*(output-singleTargetOutput);
  }
  
  public Set<Link> getPrevNeurons(){ return prevNeurons;}
  public Set<Link> getNextNeurons() { throw new UnsupportedOperationException();}
  
}
