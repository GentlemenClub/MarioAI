package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.HashSet;
import java.util.Set;

public class OutputNeuron extends Neuron
{
  protected final Set<Link> prevNeurons;
  private double targetOutput;
  
  public OutputNeuron() { prevNeurons = new HashSet<Link>(); }
  
  public void forwardPass() { throw new UnsupportedOperationException();}
  public void addNext(Neuron prev){ throw new UnsupportedOperationException();}
  
  public double getFinalOutput()
  {
    double biasedNet = currentNet + NeuralNetwork.outputBias;
    return 1/(1+Math.exp(-biasedNet));//sigmoid function to output
  }
  
  public void addPrev(Neuron prev) { prevNeurons.add(new Link(prev,this)); }
  
  public void setTargetOutput(double targetOutput) { this.targetOutput = targetOutput; }
  public double getTargetOutput () { return targetOutput; }
  
  protected double delta()
  {
    double output = getFinalOutput();
    return output*(1-output)*(output-targetOutput);
  }
  
  public Set<Link> getPrevNeurons(){ return prevNeurons;}
  public Set<Link> getNextNeurons() { throw new UnsupportedOperationException();}
  
}
