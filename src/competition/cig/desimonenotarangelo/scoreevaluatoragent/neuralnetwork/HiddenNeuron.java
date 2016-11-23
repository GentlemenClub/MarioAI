package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;


import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.ActivationFunction;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators.ValueGenerator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HiddenNeuron extends Neuron {
    protected transient Set<Link> nextNeurons;
    protected transient Set<Link> prevNeurons;

    public HiddenNeuron(double bias, ActivationFunction activationFunction) {
        super(bias, activationFunction);
        nextNeurons = new HashSet<Link>();
        prevNeurons = new HashSet<Link>();
    }

    public void forwardPass() {
        Map<Neuron, Double> nets = NeuralNetwork.netsCache;
        double singleOutput = computeOutput(nets.get(this));//Calculates activation function from neuron's net sum
        for (Link link : nextNeurons) {
            Neuron currNext = link.getNext();
            Double nextNet = nets.get(currNext);
            
            //If it is the first time you add net to the neuron, the net must be initialized to 0
            if (nextNet == null)
                nextNet = 0.0;

            nets.put(currNext, nextNet + singleOutput * link.getWeight());
        }
    }

    public void addPrev(Neuron prev, ValueGenerator weightInitializer) {
        prevNeurons.add(new Link(prev, this, weightInitializer));
    }

    public void addNext(Neuron next, ValueGenerator weightInitializer) {
        nextNeurons.add(new Link(this, next, weightInitializer));
    }

    public void linkToNextLayer(Set<? extends Neuron> layer, ValueGenerator weightInitializer) {
        for (Neuron neuron : layer)
            addNext(neuron, weightInitializer);
    }

    public void linkToPrevLayer(Set<? extends Neuron> layer, ValueGenerator weightInitializer) {
        for (Neuron neuron : layer)
            addPrev(neuron, weightInitializer);
    }

    protected double computeDelta() {
        double sum = 0;

        //for is for future implementations: now only one output node is supported
        for (Link link : nextNeurons) {
            Neuron currNext = link.getNext();
            if(NeuralNetwork.dropoutMaskCache.contains(currNext))
                continue;
            sum += NeuralNetwork.deltasCache.get(currNext) * link.getWeight();
        }
        double currentNet = NeuralNetwork.netsCache.get(this);
        return activationFunction.getDerivative(currentNet) * sum;
        //return ((currentNet/ (2*Math.sqrt(currentNet*currentNet+1)))+1) * sum;//Bent Identity
        /*OLD SIGMOID
        delta = output*(1-output)*sum;*/
    }

    public Set<Link> getPrevNeurons() {
        return prevNeurons;
    }

    public Set<Link> getNextNeurons() {
        return nextNeurons;
    }

    @Override
    public void setPrevNeurons(Set<Link> prevNeurons) {
        this.prevNeurons = prevNeurons;
    }

    @Override
    public void setNextNeurons(Set<Link> nextNeurons) {
        this.nextNeurons = nextNeurons;
    }


}