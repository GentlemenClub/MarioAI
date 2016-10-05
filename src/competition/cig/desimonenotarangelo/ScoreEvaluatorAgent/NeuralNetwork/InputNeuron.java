package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.HashSet;
import java.util.Set;

public class InputNeuron extends Neuron {
    protected final Set<Link> nextNeurons;

    public InputNeuron(double bias)
    {
        super(bias);
        nextNeurons = new HashSet<Link>();
    }

    public void setInput(double singleNet) { currentNet=singleNet; }

    public void forwardPass() {
        for(Link link: nextNeurons) {
            Neuron currNext = link.getNext();
            currNext.addNet(currentNet * link.getWeight());
            int i =0;
        }
    }

    public void addPrev(Neuron prev){ throw new UnsupportedOperationException();}
    public void addNext(Neuron next) { nextNeurons.add(new Link(this,next)); }

    public void linkToNextLayer(Set<? extends Neuron> layer) {
        for (Neuron neuron : layer)
            addNext(neuron);
    }
    public void linkToPrevLayer(Set<? extends Neuron> layer){ throw new UnsupportedOperationException();}
    
    public double getDelta(){ throw new UnsupportedOperationException();}
    
    public Set<Link> getPrevNeurons(){ throw new UnsupportedOperationException();}
    public Set<Link> getNextNeurons() {return nextNeurons;}
}
