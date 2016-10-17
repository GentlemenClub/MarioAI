package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OutputNeuron extends Neuron {
    protected final Set<Link> prevNeurons;

    public OutputNeuron(double bias, ActivationFunction activationFunction) {
        super(bias, activationFunction);
        prevNeurons = new HashSet<Link>();
    }

    public void forwardPass() {
        Map<Neuron, Double> nets = NeuralNetwork.netsCache;
        Map<Neuron, Double> finalOutputs = NeuralNetwork.finalOutputsCache;
        finalOutputs.put(this, computeOutput(nets.get(this)));
    }

    public void addNext(Neuron prev) {
        throw new UnsupportedOperationException();
    }

    public void linkToNextLayer(Set<? extends Neuron> layer) {
        throw new UnsupportedOperationException();
    }

    public void linkToPrevLayer(Set<? extends Neuron> layer) {
        for (Neuron neuron : layer)
            addPrev(neuron);
    }

    public void addPrev(Neuron prev) {
        prevNeurons.add(new Link(prev, this));
    }

    protected double computeDelta(double singleTargetOutput) {
        double currentNet = NeuralNetwork.netsCache.get(this);
        double output = computeOutput(currentNet);
        return (output - singleTargetOutput) * activationFunction.getDerivative(currentNet);
        //return (output-singleTargetOutput) * ((currentNet/ (2*Math.sqrt(currentNet*currentNet+1)))+1);//Bent Identity
        //OLD SIGMOID output*(1-output)*(output-singleTargetOutput);
    }

    public Set<Link> getPrevNeurons() {
        return prevNeurons;
    }

    public Set<Link> getNextNeurons() {
        throw new UnsupportedOperationException();
    }

}
