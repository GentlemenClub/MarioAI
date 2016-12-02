package competition.cig.desimonenotarangelo.scoreevaluatoragent;

import ch.idsia.mario.environments.Environment;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.*;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.ActivationFunction;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.BentIdentity;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.SigmoidFunction;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.ReLU;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators.ValueGenerator;

import java.io.IOException;
import java.util.*;

public class Learner {
    private NeuralNetwork network;
    private double epsilon;
    private NeuralNetworkOutput lastNNOutput = null;
    private double gamma = 0.9;
    private String nnFileName = "MarioAI.ai";
    public final static int nActions = 32;
    public final static int nButtons = 5;
    private OutputNeuron lastChosenActionNeuron;
    private boolean qValuePrint=true,
                    rewardPrint=false;
    
    private LinkedList<double[]> inputHistory;
    private final int historySize = 4;
    
    public Learner(double epsilon) {
        try {
            network = new NeuralNetwork(nnFileName);
        } catch (IOException | ClassNotFoundException e) {
            ActivationFunction bentIdentity = new BentIdentity();
            ActivationFunction sigmoidFunction = new SigmoidFunction();
            ActivationFunction reLU = new ReLU();


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
                    .setInputLayerActivationFunction(reLU)
                    .setHiddenLayersActivationFunction(bentIdentity)
                    .setOutputLayerActivationFunction(bentIdentity)
                    .addInputLayer(nButtons * historySize//  Action History
                                + (22*22+1))//22*22 Current Environment + Current Mario Mode
                    .addHiddenLayer(ValueGenerator.Type.XAVIER, ValueGenerator.Type.XAVIER, 250)
                    .addHiddenLayer(ValueGenerator.Type.XAVIER, ValueGenerator.Type.XAVIER, 125)
                    .addOutputLayer(ValueGenerator.Type.XAVIER, ValueGenerator.Type.XAVIER, ids)
                    .setDropoutPercentage(0.5)
                    .setEta(0.00002)
                    .build();
            System.out.println("Creating new neural network");
        }
        this.epsilon = epsilon;
        this.inputHistory = new LinkedList<double[]>();
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

        NNInput(List<double[]> environmentHistory, Environment observation) {
            //inputAsList contains all neural network inputs mapped into a single list
            inputAsList = new ArrayList<Double>();

            for (double[] action : environmentHistory) {
                //Action took at that time
                for (int i = 0; i < action.length; i++)
                    inputAsList.add(action[i]);
            }
            
            //Full observation in current Environment
            double [][] normalizedObservation = getNormalizedObservation(observation);
            for (int i = 0; i < normalizedObservation.length; i++)
                for (int j = 0; j < normalizedObservation.length; j++)
                    inputAsList.add(normalizedObservation[i][j]);
            
            //Mario mode in current Environment
            double normalizedMarioMode = normalizeValue(observation.getMarioStatus(),
                    0.0, 2.0,
                    0.0, 1.0);
            inputAsList.add(normalizedMarioMode);
        }

        public List<Double> getInputList() {
            return inputAsList;
        }

        public int size() {
            return inputAsList.size();
        }
    }
    
    private static double normalizeValue(double x,
                                        double dataLow, double dataHigh,
                                        double normalizedLow, double normalizedHigh)
    {
        return ((x - dataLow)
                / (dataHigh - dataLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }
    
    public static double[][] getNormalizedObservation(Environment Observation) {
        
        byte[][] completeObservationInByte = Observation.getCompleteObservation();
        double[][] completeObservationInDouble = new double[22][22];
        
        for (int i = 0; i < 22; i++)
        {
            for (int j = 0; j < 22; j++)
            {
                completeObservationInDouble[i][j] = normalizeValue(completeObservationInByte[i][j],
                        Byte.MIN_VALUE, Byte.MAX_VALUE,
                        0, 1);
            }
        }
        return completeObservationInDouble;
    }
    
    public static double[][] getNormalizedSubObservation(Environment Observation) {
        byte[][] completeObservation = Observation.getCompleteObservation();
        double[][] subObservation = new double[11][11];
        
        int h=0,k=0;
        for (int i = 5; i < 16; i++)
        {
            for (int j = 5; j < 16; j++)
            {
                subObservation[h][k] = normalizeValue(completeObservation[i][j],
                        Byte.MIN_VALUE, Byte.MAX_VALUE,
                        0, 1);
                h++;
                if(h==11)
                {
                    h=0;
                    k++;
                }
            }
        }
        return subObservation;
    }
    
    private double[] getDoubleActionFromBoolean(boolean[] action)
    {
        double[] doubleAction = new double[action.length];
        for(int i = 0; i < action.length ; i++ )
        {
            if(action[i])
                doubleAction[i] = 1;
            else
                doubleAction[i] = 0;
        }
        return doubleAction;
    }
    
    private void initHistory()
    {
        if(inputHistory.isEmpty())
        {
            for(int i=0; i<historySize; i++)
                inputHistory.addFirst(getDoubleActionFromBoolean(new boolean[Learner.nButtons]));
        }
        else
            throw new UnsupportedOperationException("History cannot be initialized twice.");
    }
    
    private void updateHistory(boolean[] action)
    {
        double[] doubleAction = getDoubleActionFromBoolean(action);
        
        if(inputHistory.size()==historySize)
        {
            inputHistory.removeLast();
            inputHistory.addFirst(doubleAction);
        }
        else
            throw new UnsupportedOperationException("History needs to be initialized");
    }
    
    public void reset() { inputHistory.clear(); }
    
    public boolean[] getAction(Environment observation)
    {
        double rand = Math.random();
        boolean[] chosenAction;
        
        //First time initialization only
        if(inputHistory.isEmpty())
          initHistory();
        
        if(qValuePrint)
          System.out.println("----------------------------");
        //Exploration
        if (rand < epsilon) {
            chosenAction = getRandomAction(observation);
            if(qValuePrint)
              System.out.println("Exploration");
        }
        //Exploitation
        else {
            chosenAction = getBestAction(observation);
            if(qValuePrint)
              System.out.println("Exploitation");
        }

        for (OutputNeuron n : lastNNOutput.getFinalOutputs().keySet()) {
            if(qValuePrint)
            {
              printActionArray(parseActionString(n.getId()));
              System.out.print("QValue = " + lastNNOutput.getValue(n));
            }
            if (Arrays.equals(parseActionString(n.getId()), chosenAction)) {
                lastChosenActionNeuron = n;
                if(qValuePrint)
                {
                  System.out.print("  <-------------Chosen Action");
                  System.out.print("");
                }
            }
            if(qValuePrint)
              System.out.println();
        }
        if(qValuePrint)
          System.out.println("----------------------------");
        
        //Records in history the fact that agent took this action in this environment
        updateHistory(chosenAction);
        
        return chosenAction;
    }

    private boolean[] getRandomAction(Environment observation) {
        boolean[] randomAction = new boolean[nButtons];
        Random random = new Random();

        for (int i = 0; i < nButtons; i++)
            randomAction[i] = random.nextBoolean();

        NNInput nnInput = new NNInput(inputHistory, observation);
        lastNNOutput = network.forwardPropagation(nnInput);
        
        return randomAction;
    }

    public boolean[] parseActionString(String actionId) {
        String[] parts = actionId.split(" ");
        boolean[] action = new boolean[nButtons];
        for (int i = 0; i < nButtons; i++)
            action[i] = Boolean.parseBoolean(parts[i]);
        return action;
    }

    public boolean[] getBestAction(Environment observation) {
        boolean[] chosenAction = null;

        NNInput nnInput = new NNInput(inputHistory,observation);
        NeuralNetworkOutput nnOutput = network.forwardPropagation(nnInput);
        OutputNeuron maxQValueNeuron = nnOutput.getMaxValueNeuron();
        
        chosenAction = parseActionString(maxQValueNeuron.getId());
        lastNNOutput = nnOutput;
        
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

    public void learn(Environment observation, double nextStateReward)
    {
      if(rewardPrint)
        System.out.println("Reward = "+ nextStateReward);
      //qLearn(observation,nextStateReward);
      sarsaLearn(observation,nextStateReward);
    }

    private void qLearn(Environment observation, double nextStateReward)
    {
        //double qValueStAt = lastNNOutput.getValue(lastChosenActionNeuron);
        double qValueSt_nextAt_next;
    
        Map<OutputNeuron, Double> targetOutputs = new HashMap<OutputNeuron, Double>(lastNNOutput.getFinalOutputs());
        NNInput nextStateNNInput = new NNInput(inputHistory,observation);
        NeuralNetworkOutput nnOutput = network.forwardPropagation(nextStateNNInput);
        OutputNeuron nextStateMaxValueNeuron = nnOutput.getMaxValueNeuron();
        qValueSt_nextAt_next = nextStateReward + gamma * nnOutput.getValue(nextStateMaxValueNeuron);//Q-LEARNING
        
        targetOutputs.put(lastChosenActionNeuron, qValueSt_nextAt_next);
    
        network.backPropagation(lastNNOutput, targetOutputs);
    }
    
    private void sarsaLearn(Environment observation, double nextStateReward)
    {
        double qValueSt_nextAt_next;
    
        Map<OutputNeuron, Double> targetOutputs = new HashMap<OutputNeuron, Double>(lastNNOutput.getFinalOutputs());
    
        NNInput nextStateNNInput = new NNInput(inputHistory,observation);
        NeuralNetworkOutput nnOutput = network.forwardPropagation(nextStateNNInput);
        
        //SARSA, choosing next action using policy
       double value;
       double rand = Math.random();
       if(rand<epsilon)
           value = nnOutput.getRandomValue();
       else
       {
           OutputNeuron nextStateMaxValueNeuron = nnOutput.getMaxValueNeuron();
           value = nnOutput.getValue(nextStateMaxValueNeuron);
       }
       
        qValueSt_nextAt_next = nextStateReward + gamma * value;
        targetOutputs.put(lastChosenActionNeuron, qValueSt_nextAt_next);
        network.backPropagation(lastNNOutput, targetOutputs);
    }
}