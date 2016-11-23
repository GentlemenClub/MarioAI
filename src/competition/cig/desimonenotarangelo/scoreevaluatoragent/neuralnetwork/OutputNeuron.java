package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;

import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.ActivationFunction;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators.ValueGenerator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OutputNeuron extends Neuron {
    protected transient Set<Link> prevNeurons;

    public OutputNeuron(double bias, ActivationFunction activationFunction) {
        super(bias, activationFunction);
        prevNeurons = new HashSet<Link>();
    }
    
    public OutputNeuron(double bias, ActivationFunction activationFunction, String id) {
        super(bias, activationFunction,id);
        prevNeurons = new HashSet<Link>();
    }
    
    public void forwardPass() {
        Map<Neuron, Double> nets = NeuralNetwork.netsCache;
        Map<Neuron, Double> finalOutputs = NeuralNetwork.finalOutputsCache;
        finalOutputs.put(this, computeOutput(nets.get(this)));
    }

    public void addNext(Neuron prev, ValueGenerator weightInitializer) {
        throw new UnsupportedOperationException();
    }

    public void linkToNextLayer(Set<? extends Neuron> layer, ValueGenerator weightInitializer) {
        throw new UnsupportedOperationException();
    }

    public void linkToPrevLayer(Set<? extends Neuron> layer, ValueGenerator weightInitializer) {
        for (Neuron neuron : layer)
            addPrev(neuron, weightInitializer);
    }

    public void addPrev(Neuron prev, ValueGenerator weightInitializer) {
        prevNeurons.add(new Link(prev, this, weightInitializer));
    }

    public double computeDelta(double singleTargetOutput) {
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

    @Override
    public void setPrevNeurons(Set<Link> prevNeurons) {
        this.prevNeurons = prevNeurons;
    }

    @Override
    public void setNextNeurons(Set<Link> nextNeurons) {
        throw new UnsupportedOperationException();
    }
}
