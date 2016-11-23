package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;

import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.ActivationFunction;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators.ValueGenerator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InputNeuron extends Neuron {
    protected transient Set<Link> nextNeurons;

    public InputNeuron(double bias, ActivationFunction activationFunction) {
        super(bias, activationFunction);
        nextNeurons = new HashSet<Link>();
    }

    public void forwardPass() {
        Map<Neuron,Double> nets = NeuralNetwork.netsCache;
        for(Link link: nextNeurons) {
            Neuron currNext = link.getNext();
            double singleNet = nets.get(this);
            Double nextNet = nets.get(currNext);
            //If it is the first time you add net to the neuron, the net must be initialized to 0
            if(nextNet==null)
                nextNet=0.0;

            nets.put(currNext, nextNet + singleNet * link.getWeight());
        }
    }

    public void addPrev(Neuron prev, ValueGenerator weightInitializer){ throw new UnsupportedOperationException();}
    public void addNext(Neuron next, ValueGenerator weightInitializer) {
        nextNeurons.add(new Link(this, next, weightInitializer));
    }

    public void linkToNextLayer(Set<? extends Neuron> layer, ValueGenerator weightInitializer) {
        for (Neuron neuron : layer)
            addNext(neuron, weightInitializer);
    }

    public void linkToPrevLayer(Set<? extends Neuron> layer, ValueGenerator weightInitializer){
        throw new UnsupportedOperationException();
    }

    public double getDelta(){ throw new UnsupportedOperationException();}

    public Set<Link> getPrevNeurons(){ throw new UnsupportedOperationException();}
    public Set<Link> getNextNeurons() {return nextNeurons;}

    @Override
    public void setPrevNeurons(Set<Link> prevNeurons) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNextNeurons(Set<Link> nextNeurons) {
        this.nextNeurons = nextNeurons;
    }
    
}
