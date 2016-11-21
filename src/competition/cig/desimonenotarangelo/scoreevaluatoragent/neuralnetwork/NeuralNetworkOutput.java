package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;


import java.util.*;

public class NeuralNetworkOutput {
    private final Map<OutputNeuron, Double> finalOutputs;
    private final Map<Neuron, Double> nets;
    private final Set<Neuron> dropoutMask;
    
    public NeuralNetworkOutput(Map<OutputNeuron, Double> outputs,
                               Map<Neuron, Double> nets,
                               Set<Neuron> dropoutMask)
    {
        this.finalOutputs = outputs;
        this.nets = nets;
        this.dropoutMask = dropoutMask;
    }

    //Returns net for each node in the network
    public Map<Neuron, Double> getNets() {
        return nets;
    }

    //Returns output for each output neuron
    public Map<OutputNeuron, Double> getFinalOutputs() {
        return finalOutputs;
    }
    
    //Returns all nodes that were not used in the forward pass
    public Set<Neuron> getDropoutMask() {
        return dropoutMask;
    }
    
    public OutputNeuron getMaxValueNeuron() {
        double currMax = Double.NEGATIVE_INFINITY;
        OutputNeuron maxValueNeuron = null;

        for (OutputNeuron n : finalOutputs.keySet()) {
            double currValue = finalOutputs.get(n);
            if (maxValueNeuron == null || currMax < currValue) {
                maxValueNeuron = n;
                currMax = currValue;
            }
        }
        return maxValueNeuron;
    }

    public double getRandomValue() {
        List<Double> valuesList = new ArrayList<Double>(finalOutputs.values());
        int randomIndex = new Random().nextInt(valuesList.size());
        return valuesList.get(randomIndex);
    }
    
    public double getValue(OutputNeuron n) {
        if(finalOutputs.get(n)==null)
            System.out.print("");
        return finalOutputs.get(n);
    }
}
