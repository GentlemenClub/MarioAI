package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class Neuron implements Serializable {
    protected double bias;
    private final String id;
    protected ActivationFunction activationFunction;

    public abstract void forwardPass();
    public abstract void addPrev(Neuron prev);
    public abstract void addNext(Neuron next);
    public abstract void linkToNextLayer(Set<? extends Neuron> layer);
    public abstract void linkToPrevLayer(Set<? extends Neuron> layer);
    public abstract Set<Link> getPrevNeurons();
    public abstract Set<Link> getNextNeurons();

    public Neuron(double bias, ActivationFunction activationFunction) {
        this.bias = bias;
        this.activationFunction = activationFunction;
        this.id = UUID.randomUUID().toString();
    }
    
    public String getId()
    {
        return id;
    }
    
    public Neuron(double bias, ActivationFunction activationFunction, String id) {
        this.bias = bias;
        this.activationFunction = activationFunction;
        this.id = id;
    }
    
    //public void addNet(double singleNet ) { currentNet+=singleNet; }
    //public void resetNet(){ currentNet=0; }
    //protected double getCurrentNet(){ return currentNet; }

    protected double computeOutput(double currentNet) {
        double biasedNet = currentNet + bias;
        return activationFunction.getFunction(biasedNet);
        //return (Math.sqrt(biasedNet * biasedNet + 1) - 1) * 0.5 + biasedNet;//Bent Identity
        //OLD SIGMOID output = 1/(1+Math.exp(-biasedNet)); sigmoid function to output
    }

    protected void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

    public boolean equals(Object o) {
        if (o instanceof Neuron) {
            Neuron n = (Neuron) o;
            return id.equals(n.id);
        }
        return false;
    }

    public int hashCode() {
        return id.hashCode();
    }
}
