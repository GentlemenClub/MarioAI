package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;


import java.util.HashMap;
import java.util.Map;

public class Link {
    private final Neuron in, out;
    public static final Map<Link, Double> weights = new HashMap<Link, Double>();

    public Link(Neuron in, Neuron out) {
        this.in = in;
        this.out = out;
        if (!weights.containsKey(this))//Not really necessary
            weights.put(this, Math.random());
    }

    public Neuron getIn() {
        return in;
    }

    public Neuron getOut() {
        return out;
    }

    public double getWeight() {
        return weights.get(this);
    }

    public void setWeight(double weight) {
        weights.put(this, weight);
    }

    public double getOutput() {
        if (in instanceof InputNeuron)
            return in.getCurrentNet() * getWeight();
        else if (in instanceof HiddenNeuron) {
            double currentNet = in.getCurrentNet() + NeuralNetwork.hiddenBias;
            return 1 / (1 + Math.exp(-currentNet));//sigmoid function to output
        } else {
            //OutputNeuron case
            double currentNet = in.getCurrentNet() + NeuralNetwork.outputBias;
            return 1 / (1 + Math.exp(-currentNet));//sigmoid function to output
        }
    }

    public void updateWeight(double deltaWeight) {
        double oldWeight = weights.get(this);
        weights.put(this, oldWeight + deltaWeight);
    }

    public boolean equals(Object o) {
        if (o instanceof Link) {
            Link l = (Link) o;
            return (in == l.in && out == l.out);
        } else
            return false;
    }
}
