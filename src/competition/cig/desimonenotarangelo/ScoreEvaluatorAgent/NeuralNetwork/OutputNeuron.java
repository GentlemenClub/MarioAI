package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.HashSet;
import java.util.Set;

public class OutputNeuron extends Neuron
{
  protected final Set<Link> prevNeurons;
  
  public OutputNeuron(double bias)
  {
    super(bias);
    prevNeurons = new HashSet<Link>();
  }
  
  public void forwardPass() { computeOutput(); }
  
  public void addNext(Neuron prev){ throw new UnsupportedOperationException();}

  public void linkToNextLayer(Set<? extends Neuron> layer){ throw new UnsupportedOperationException();}

  public void linkToPrevLayer(Set<? extends Neuron> layer) {
    for (Neuron neuron : layer)
        addPrev(neuron);
  }

  public void addPrev(Neuron prev) { prevNeurons.add(new Link(prev,this)); }
  
  protected void computeDelta(double singleTargetOutput)
  {
    delta = (output-singleTargetOutput) * ((currentNet/ (2*Math.sqrt(currentNet*currentNet+1)))+1);//Bent Identity
    //OLD SIGMOID output*(1-output)*(output-singleTargetOutput);
  }
  
  protected double getDelta() { return delta; }
  
  public Set<Link> getPrevNeurons(){ return prevNeurons;}
  public Set<Link> getNextNeurons() { throw new UnsupportedOperationException();}
  
}
