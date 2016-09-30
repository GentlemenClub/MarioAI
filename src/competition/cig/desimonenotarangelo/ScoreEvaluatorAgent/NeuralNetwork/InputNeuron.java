package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.HashSet;
import java.util.Set;

public class InputNeuron extends Neuron {
    protected final Set<Link> nextNeurons;

    public InputNeuron() { nextNeurons = new HashSet<Link>(); }

    public void setInput(double singleNet) { currentNet=singleNet; }

    public void forwardPass() {
        for(Link link: nextNeurons) {
            Neuron currNext = link.getOut();
            currNext.addNet(currentNet * link.getWeight());
        }
    }

    public void addPrev(Neuron prev){ throw new UnsupportedOperationException();}
    public void addNext(Neuron next) { nextNeurons.add(new Link(this,next)); }

    public void linkToNextLayer(Set<? extends Neuron> layer) {
        for (Neuron neuron : layer)
            addNext(neuron);
    }
    public void linkToPrevLayer(Set<? extends Neuron> layer){ throw new UnsupportedOperationException();}
    
    public Set<Link> getPrevNeurons(){ throw new UnsupportedOperationException();}
    public Set<Link> getNextNeurons() {return nextNeurons;}
}
