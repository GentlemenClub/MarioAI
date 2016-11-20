package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;


//BEST LINK EVER: https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/

import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.ActivationFunction;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.weightinitializers.WeightInitializer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HiddenNeuron extends Neuron {
    protected final Set<Link> nextNeurons;
    protected final Set<Link> prevNeurons;

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

    public void addPrev(Neuron prev, WeightInitializer weightInitializer) {
        prevNeurons.add(new Link(prev, this, weightInitializer));
    }

    public void addNext(Neuron next, WeightInitializer weightInitializer) {
        nextNeurons.add(new Link(this, next, weightInitializer));
    }

    public void linkToNextLayer(Set<? extends Neuron> layer, WeightInitializer weightInitializer) {
        for (Neuron neuron : layer)
            addNext(neuron, weightInitializer);
    }

    public void linkToPrevLayer(Set<? extends Neuron> layer, WeightInitializer weightInitializer) {
        for (Neuron neuron : layer)
            addPrev(neuron, weightInitializer);
    }

    protected double computeDelta() {
        double sum = 0;

        //for is for future implementations: now only one output node is supported
        for (Link link : nextNeurons) {
            Neuron currNext = link.getNext();
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


}