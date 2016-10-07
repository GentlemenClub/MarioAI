package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Neuron
{
    protected double currentNet;
    protected double delta;
    protected double output;
    protected double bias;
    private final String id;
    
    public abstract void forwardPass();
    public abstract void addPrev(Neuron prev);
    public abstract void addNext(Neuron next);
    public abstract void linkToNextLayer(Set<? extends Neuron> layer);
    public abstract void linkToPrevLayer(Set<? extends Neuron> layer);
    public abstract Set<Link> getPrevNeurons();
    public abstract Set<Link> getNextNeurons();
    protected abstract double getDelta();
    
    public Neuron(double bias, String id)
    {
        this.bias = bias;
        this.id=id;
    }
    
    public Neuron(double bias)
    {
        this.bias = bias;
        this.id = UUID.randomUUID().toString();
    }
    
    public void addNet(double singleNet ) { currentNet+=singleNet; }
    public void resetNet(){ currentNet=0; }
    protected double getCurrentNet(){ return currentNet; }
    protected double getOutput(){ return output; }
    
    protected void computeOutput()
    {
        double biasedNet = currentNet + bias;
        //currentNet=0;
        /*if( this instanceof OutputNeuron)
        {
            output = biasedNet; //Linear Function needed
            int a=2;
        }
        else*/
        output = (Math.sqrt(biasedNet*biasedNet +1)-1)*0.5 + biasedNet;//Bent Identity
        //output = biasedNet*biasedNet*biasedNet;//x^3 as activation function
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
