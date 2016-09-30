package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;


//BEST LINK EVER: https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
import java.util.HashSet;
import java.util.Set;

public class HiddenNeuron extends Neuron
{
  protected final Set<Link> nextNeurons;
  protected final Set<Link> prevNeurons;
  
  public HiddenNeuron()
  {
    nextNeurons = new HashSet<Link>();
    prevNeurons = new HashSet<Link>();
  }
  
  public void forwardPass()
  {
    double output= getOutput();
    
    for(Link link: nextNeurons)
    {
      Neuron currNext = link.getOut();
      currNext.addNet(output * link.getWeight());
    }
  }
  
  public void addPrev(Neuron prev) { prevNeurons.add(new Link(prev,this)); }
  public void addNext(Neuron next) { nextNeurons.add(new Link(this,next)); }

  public void linkToNextLayer(Set<? extends Neuron> layer) {
    for (Neuron neuron : layer)
      addNext(neuron);
  }

  public void linkToPrevLayer(Set<? extends Neuron> layer) {
      for (Neuron neuron : layer)
          addPrev(neuron);
  }

  private double getOutput()
  {
    double biasedNet = currentNet + NeuralNetwork.hiddenBias;
    return 1/(1+Math.exp(-biasedNet));//sigmoid function to output
  }
  
  protected double delta()
  {
    double output = getOutput();
    double sum = 0;
    
    //for is for future implementations: now only one output node is supported
    for(Link link: nextNeurons)
    {
      Neuron currNext = link.getOut();
      sum += currNext.delta() * link.getWeight();
    }
    
    return output*(1-output)*sum;
  }
  
  public Set<Link> getPrevNeurons(){ return prevNeurons;}
  public Set<Link> getNextNeurons(){ return nextNeurons;}
  
  
}