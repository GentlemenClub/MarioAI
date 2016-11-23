package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;


import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators.ValueGenerator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Link implements Serializable {
    private final Neuron prev, next;
    public static final Map<Link, Double> weights = new HashMap<Link, Double>();

    public Link(Neuron in, Neuron out) {
        this.prev = in;
        this.next = out;
    }

    public Link(Neuron in, Neuron out, ValueGenerator weightInitializer) {
        this.prev = in;
        this.next = out;
        setWeight(weightInitializer.getValue());
    }

    public Neuron getPrev() { return prev; }

    public Neuron getNext() {
        return next;
    }

    public double getWeight() {
        return weights.get(this);
    }

    //if the key exists, don't overwrite the value
    public void setWeight(double weight) {
        if (!weights.containsKey(this))
            weights.put(this, weight);
    }
    
    public void updateWeight(double deltaWeight) {
        double oldWeight = weights.get(this);
        weights.put(this, oldWeight + deltaWeight);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Link) {
            Link l = (Link) o;
            return (prev.equals(l.prev) && next.equals(l.next));
        } else
            return false;
    }
    
    @Override
    public int hashCode()
    {
      return prev.hashCode() ^ next.hashCode();
    }
}
