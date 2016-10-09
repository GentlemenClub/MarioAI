package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Link implements Serializable {
    private final Neuron prev, next;
    public static final Map<Link, Double> weights = new HashMap<Link, Double>();

    public Link(Neuron in, Neuron out) {
        this.prev = in;
        this.next = out;
        if (!weights.containsKey(this))//if the key exists, don't overwrite the value
            weights.put(this, Math.random());
    }

    public Neuron getPrev() { return prev; }

    public Neuron getNext() {
        return next;
    }

    public double getWeight() {
        return weights.get(this);
    }

    public void setWeight(double weight) {
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
