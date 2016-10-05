package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;


//BEST LINK EVER: https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
import java.util.HashSet;
import java.util.Set;

public class HiddenNeuron extends Neuron
{
  protected final Set<Link> nextNeurons;
  protected final Set<Link> prevNeurons;
  
  public HiddenNeuron(double bias)
  {
    super(bias);
    nextNeurons = new HashSet<Link>();
    prevNeurons = new HashSet<Link>();
  }
  
  public void forwardPass()
  {
    computeOutput();
    for(Link link: nextNeurons)
    {
      Neuron currNext = link.getNext();
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
  
  protected void computeDelta()
  {
    double output = getOutput();
    double sum = 0;
    
    //for is for future implementations: now only one output node is supported
    for(Link link: nextNeurons)
    {
      Neuron currNext = link.getNext();
      sum += currNext.getDelta() * link.getWeight();
    }
    
    delta = output*(1-output)*sum;
  }
  
  protected double getDelta() { return delta; }
  
  public Set<Link> getPrevNeurons(){ return prevNeurons;}
  public Set<Link> getNextNeurons(){ return nextNeurons;}
  
  
}