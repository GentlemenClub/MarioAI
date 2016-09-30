package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.HashSet;
import java.util.Set;

public abstract class Neuron
{
    protected double currentNet;
    public abstract void forwardPass();
    public abstract void addPrev(Neuron prev);
    public abstract void addNext(Neuron next);
    public abstract void linkToNextLayer(Set<? extends Neuron> layer);
    public abstract void linkToPrevLayer(Set<? extends Neuron> layer);
    public abstract Set<Link> getPrevNeurons();
    public abstract Set<Link> getNextNeurons();

    public void addNet(double singleNet ) { currentNet+=singleNet; }
    public void resetNet(){ currentNet=0; }
    protected double getCurrentNet(){ return currentNet; }
}
