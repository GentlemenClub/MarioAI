package competition.cig.desimonenotarangelo.scoreevaluatoragent;

import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.*;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.ActivationFunction;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.BentIdentity;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators.ValueGenerator;

import java.io.IOException;
import java.util.*;

public class Learner {
    private NeuralNetwork network;
    private double epsilon;
    private NeuralNetworkOutput lastNNOutput = null;
    private double gamma = 0.2;
    private String nnFileName = "MarioAI.ai";
    public final static int nActions = 32;
    public final static int nButtons = 5;
    private OutputNeuron lastChosenActionNeuron;
    
    public Learner(double epsilon) {
        try {
            network = new NeuralNetwork(nnFileName);
        } catch (IOException | ClassNotFoundException e) {
            ActivationFunction activationFunction = new BentIdentity();
            //ActivationFunction activationFunction = new SigmoidFunction();
            //ActivationFunction activationFunction = new ReLU();
            //ActivationFunction activationFunction = new Identity();

            ActionIterator iterator = new ActionIterator();
            String[] ids = new String[nActions];
            int i = 0;

            while (iterator.hasNext()) {
                boolean currAction[] = iterator.next();
                String currActionString = "";
                for (int j = 0; j < currAction.length; j++) {
                    if (j == 0)
                        currActionString = Boolean.toString(currAction[j]);
                    else
                        currActionString += " " + Boolean.toString(currAction[j]);
                }
                ids[i++] = currActionString;
            }

            network = new NeuralNetworkBuilder()
                    .setInputLayerActivationFunction(activationFunction)
                    .setHiddenLayersActivationFunction(activationFunction)
                    .setOutputLayerActivationFunction(activationFunction)
                    .addInputLayer(22 * 22 + nButtons + 1)// Environment
                    .addHiddenLayer(ValueGenerator.Type.XAVIER, ValueGenerator.Type.RANDOM, 200)
                    .addHiddenLayer(ValueGenerator.Type.XAVIER, ValueGenerator.Type.RANDOM, 100)
                    .addOutputLayer(ValueGenerator.Type.XAVIER, ValueGenerator.Type.RANDOM, ids)
                    .setDropoutPercentage(0.5)
                    .setEta(0.000002)
                    .build();
            System.out.println("Creating new neural network");
        }
        this.epsilon = epsilon;
    }

    public void saveStatus() {
        network.saveNeuralNetwork(nnFileName);
    }

    private static class ActionIterator implements Iterator {
        private final int length = 5;
        private int count = 0;

        @Override
        public boolean hasNext() {
            return count < Math.pow(2, length);
        }

        @Override
        public boolean[] next() {
            String bin = Integer.toBinaryString(count);
            while (bin.length() < length)
                bin = "0" + bin;
            char[] chars = bin.toCharArray();
            boolean[] boolArray = new boolean[length];

            for (int j = 0; j < chars.length; j++)
                boolArray[j] = chars[j] == '0';

            count++;
            return boolArray;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class NNInput implements NeuralNetworkInput {
        List<Double> inputAsList;

        NNInput(List<QState> environmentHistory) {
            //inputAsList contains all neural network inputs mapped into a single list
            inputAsList = new ArrayList<Double>();

            for (QState s : environmentHistory) {
                //Environment input
                double[][] observation = s.getObservation();
                for (int i = 0; i < observation.length; i++)
                    for (int j = 0; j < observation.length; j++)
                        inputAsList.add(observation[i][j]);
                
                //Last Action input
                double[] lastAction = s.getLastAction();
                for (int i = 0; i < lastAction.length; i++)
                    inputAsList.add(lastAction[i]);
    
                inputAsList.add(s.getMarioMode());
            }
        }

        public List<Double> getInputList() {
            return inputAsList;
        }

        public int size() {
            return inputAsList.size();
        }
    }

    public boolean[] getAction(List<QState> environmentHistory) {
        double rand = Math.random();
        boolean[] chosenAction;
        System.out.println("----------------------------");
        //Exploration
        if (rand < epsilon) {
            chosenAction = getRandomAction(environmentHistory);
            System.out.println("Exploration");
        }
        //Exploitation
        else {
            chosenAction = getBestAction(environmentHistory);
            System.out.println("Exploitation");
        }

        for (OutputNeuron n : lastNNOutput.getFinalOutputs().keySet()) {
            printActionArray(parseActionString(n.getId()));
            System.out.print("QValue = " + lastNNOutput.getValue(n));
            if (Arrays.equals(parseActionString(n.getId()), chosenAction)) {
                lastChosenActionNeuron = n;
                System.out.print("  <-------------Chosen Action");
                System.out.print("");
            }
            System.out.println();
        }

        System.out.println("----------------------------");
        return chosenAction;
    }

    private boolean[] getRandomAction(List<QState> environmentHistory) {
        boolean[] randomAction = new boolean[nButtons];
        Random random = new Random();

        for (int i = 0; i < nButtons; i++)
            randomAction[i] = random.nextBoolean();

        NNInput nnInput = new NNInput(environmentHistory);
        lastNNOutput = network.forwardPropagation(nnInput);//Needed for backpropagation

        return randomAction;
    }

    public boolean[] parseActionString(String actionId) {
        String[] parts = actionId.split(" ");
        boolean[] action = new boolean[nButtons];
        for (int i = 0; i < nButtons; i++)
            action[i] = Boolean.parseBoolean(parts[i]);
        return action;
    }

    public boolean[] getBestAction(List<QState> environmentHistory) {
        ActionIterator iterator = new ActionIterator();
        boolean[] currAction;
        double maxQvalue;
        boolean[] chosenAction = null;//As long as maxQValue is negative infinity, this variable is always assigned

        NNInput nnInput = new NNInput(environmentHistory);
        NeuralNetworkOutput nnOutput = network.forwardPropagation(nnInput);
        OutputNeuron maxQValueNeuron = nnOutput.getMaxValueNeuron();
        
        chosenAction = parseActionString(maxQValueNeuron.getId());
        lastNNOutput = nnOutput;

        //if (chosenAction == null)
        //    System.out.println("OMG");

        return chosenAction;
    }

    private static void printActionArray(boolean[] action) {
        for (int i = 0; i < action.length; i++) {
            if (action[i]) {
                switch (i) {
                    case 0:
                        System.out.print("LEFT |");
                        break;
                    case 1:
                        System.out.print("RIGHT |");
                        break;
                    case 2:
                        System.out.print("DOWN |");
                        break;
                    case 3:
                        System.out.print("JUMP |");
                        break;
                    case 4:
                        System.out.print("SPEED |");
                        break;
                }
            }
        }
        System.out.println();
    }

    public void learn(List<QState> environmentHistory, double nextStateReward) {
        //double qValueStAt = lastNNOutput.getValue(lastChosenActionNeuron);
        double qValueSt_nextAt_next;

        Map<OutputNeuron, Double> targetOutputs = new HashMap<OutputNeuron, Double>(lastNNOutput.getFinalOutputs());

        NNInput nextStateNNInput = new NNInput(environmentHistory);
        NeuralNetworkOutput nnOutput = network.forwardPropagation(nextStateNNInput);
        OutputNeuron nextStateMaxValueNeuron = nnOutput.getMaxValueNeuron();
        qValueSt_nextAt_next = nextStateReward + gamma * nnOutput.getValue(nextStateMaxValueNeuron);//Q-LEARNING

        //SARSA, choosing next action using policy
    /*double value;
    double rand = Math.random();
    if(rand<epsilon)
      value = nnOutput.getRandomValue();
    else
      value = nnOutput.getValue(nextStateMaxValueNeuron);
  
    qValueSt_nextAt_next = nextStateReward + gamma * value;
  */
        targetOutputs.put(lastChosenActionNeuron, qValueSt_nextAt_next);

        network.backPropagation(lastNNOutput, targetOutputs);
    }

    public static void main(String argv[]) {
    }
}