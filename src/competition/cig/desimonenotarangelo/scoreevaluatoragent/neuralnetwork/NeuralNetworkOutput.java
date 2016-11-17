package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;


import java.util.*;

public class NeuralNetworkOutput {
    private final Map<OutputNeuron, Double> finalOutputs;
    private final Map<Neuron, Double> nets;

    public NeuralNetworkOutput(Map<OutputNeuron, Double> outputs, Map<Neuron, Double> nets) {
        this.finalOutputs = outputs;
        this.nets = nets;
    }

    //Returns net for each node in the network
    public Map<Neuron, Double> getNets() {
        return nets;
    }

    //Returns output for each output neuron
    public Map<OutputNeuron, Double> getFinalOutputs() {
        return finalOutputs;
    }

    public OutputNeuron getMaxValueNeuron() {
        double currMax = Double.NEGATIVE_INFINITY;
        OutputNeuron maxValueNeuron = null;

        for (OutputNeuron n : finalOutputs.keySet()) {
            double currValue = finalOutputs.get(n);
            if (currMax < currValue) {
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

    public Map<OutputNeuron, Double> getFinalOutputs(OutputNeuron n) {
        return finalOutputs;
    }

    public double getValue(OutputNeuron n) {
        return finalOutputs.get(n);
    }
}
