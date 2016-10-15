package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class Neuron implements Serializable
{
    protected double bias;
    private final String id;
    
    public abstract void forwardPass();
    public abstract void addPrev(Neuron prev);
    public abstract void addNext(Neuron next);
    public abstract void linkToNextLayer(Set<? extends Neuron> layer);
    public abstract void linkToPrevLayer(Set<? extends Neuron> layer);
    public abstract Set<Link> getPrevNeurons();
    public abstract Set<Link> getNextNeurons();
    
    public Neuron(double bias)
    {
        this.bias = bias;
        this.id = UUID.randomUUID().toString();
    }
    
    //public void addNet(double singleNet ) { currentNet+=singleNet; }
    //public void resetNet(){ currentNet=0; }
    //protected double getCurrentNet(){ return currentNet; }
    
    protected double computeOutput(double currentNet)
    {
        double biasedNet = currentNet + bias;
        return (Math.sqrt(biasedNet*biasedNet +1)-1)*0.5 + biasedNet;//Bent Identity
        //OLD SIGMOID output = 1/(1+Math.exp(-biasedNet)); sigmoid function to output
    }
    
    public boolean equals(Object o)
    {
        if(o instanceof Neuron )
        {
            Neuron n = (Neuron) o;
            return id.equals(n.id);
        }
        return false;
    }
    
    public int hashCode() { return id.hashCode(); }
}
