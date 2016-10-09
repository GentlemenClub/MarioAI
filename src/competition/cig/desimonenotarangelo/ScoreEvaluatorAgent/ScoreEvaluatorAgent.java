package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork.NeuralNetwork;
import competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork.NeuralNetworkInput;
import competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork.Neuron;
import competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork.OutputNeuron;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.ScoreEvaluatorAgent.ACTION.*;
import static java.lang.Thread.sleep;


public class ScoreEvaluatorAgent implements Agent {
    public enum ACTION {
        LEFT, RIGHT, JUMP, SPEED,
        LEFT_JUMP, RIGHT_JUMP, SPEED_JUMP, RIGHT_SPEED, LEFT_SPEED
    }
    private String name;

    private final boolean[] action;
    private String lastAction = null;
    private double lastScore = 0;
    private double epsilon = 0.2;
    private final Map<String,NeuralNetwork> actionEvaluators;
    private int inputLayerDim  = 81;
    private int hiddenLayerDim = 20;
    
    public ScoreEvaluatorAgent() {
        action = new boolean[Environment.numberOfButtons];
        actionEvaluators = new HashMap<String,NeuralNetwork>();
        
        for (ACTION a: ACTION.values()) {
            String action = a.toString();
            try {
                actionEvaluators.put(action, new NeuralNetwork(action + ".ai"));
            } catch (IOException | ClassNotFoundException e) {
                actionEvaluators.put(action, new NeuralNetwork(inputLayerDim,hiddenLayerDim));
            }
        }
        this.name = getClass().getName();
    }

    public void saveAI() {
        for (String action : actionEvaluators.keySet()) {
            NeuralNetwork neuralNetwork = actionEvaluators.get(action);
            neuralNetwork.saveNeuralNetwork(action + ".ai");
        }
    }
    
    void setAction(ACTION ChosenAction) {
        //Sets all actions to false
        //No button pressed

        for (int i = 0; i < 5; i++)
            action[i] = false;

        if (ChosenAction == LEFT)
            action[Mario.KEY_LEFT] = true;
        else if (ChosenAction == RIGHT)
            action[Mario.KEY_RIGHT] = true;
        else if (ChosenAction == JUMP)
            action[Mario.KEY_JUMP] = true;
        else if (ChosenAction == SPEED)
            action[Mario.KEY_SPEED] = true;
        else if (ChosenAction == LEFT_JUMP) {
            action[Mario.KEY_LEFT] = true;
            action[Mario.KEY_JUMP] = true;
        } else if (ChosenAction == RIGHT_JUMP) {
            action[Mario.KEY_RIGHT] = true;
            action[Mario.KEY_JUMP] = true;
        } else if (ChosenAction == SPEED_JUMP) {
            action[Mario.KEY_SPEED] = true;
            action[Mario.KEY_JUMP] = true;
        } else if (ChosenAction == RIGHT_SPEED) {
            action[Mario.KEY_RIGHT] = true;
            action[Mario.KEY_SPEED] = true;
        } else if (ChosenAction == LEFT_SPEED) {
            action[Mario.KEY_LEFT] = true;
            action[Mario.KEY_SPEED] = true;
        }

    }

    //Total score on which NeuralNetwork configuration will tuned
    private double getTotalScore(Environment Observation) {
        //Change the values in order to give more importance
        //to certain actions
        int killScore = Observation.getKillsTotal() * 50;
        int marioModeScore = Observation.getMarioMode() * 200;
        int coinScore = Mario.coins * 50;
        int flowerScore = Mario.gainedFlowers * 150;
        int mushroomScore = Mario.gainedMushrooms * 100;
        double marioProgress = Observation.getMarioFloatPos()[0];
        double marioHigh = Observation.getMarioFloatPos()[1];//Needed for jumping holes?

        return (double) (marioProgress +
                marioHigh              +
                killScore              +
                coinScore              +
                flowerScore            +
                marioModeScore         +
                mushroomScore);
    }

    private double getDeltaScore(Environment Observation) {
        double totalScore = getTotalScore(Observation);
        double deltaScore = totalScore - lastScore;
        lastScore = totalScore;
        return deltaScore;
    }

    //gets submatrix 9x9
    public static byte[][] getSubObservation(Environment Observation) {
        byte[][] completeObservation = Observation.getCompleteObservation();
        byte[][] subObservation = new byte[9][9];
        int k = 0, z = 0;
  
      /*for (int i = 0; i < 22; i++)
        for (int j = 0; j < 22; j++)
          CompleteObservation[i][j] = 1;
      */
        for (int i = 7; i < 16; i++)
            for (int j = 7; j < 16; j++) {
                subObservation[z][k] = completeObservation[i][j];
                k++;
                if (k == 9) {
                    k = 0;
                    z++;
                }
            }
        return subObservation;
    }

    public void reset() {
    }

    private OutputNeuron getOutputNeuron(NeuralNetwork network)
    {
        for(OutputNeuron n : network.getOutputLayer())
          return n;
        
        return null;
    }
    
    public boolean[] getAction(Environment observation) {
        
        if(lastAction != null)
        {
            NeuralNetwork network = actionEvaluators.get(lastAction);
    
            Map<OutputNeuron,Double> targetOutputs = new HashMap <OutputNeuron, Double>();
    
            targetOutputs.put(getOutputNeuron(network),getDeltaScore(observation));
            network.backPropagation(targetOutputs);
            
            for(String actionName: actionEvaluators.keySet())
            {
                actionEvaluators.get(actionName).resetNetwork();
            }
        }
        
        double random = Math.random();
        
        if(random < epsilon)
        {
            Random rand = new Random();
            int randomAction = rand.nextInt(ACTION.values().length);
            setAction(ACTION.values()[randomAction]);
            lastAction = ACTION.values()[randomAction].toString();
        }
        else
        {
            ACTION bestAction = getBestAction(observation);
            setAction(bestAction);
            lastAction = bestAction.toString();
        }
        return action;
    }
    
    private ACTION getBestAction(Environment observation)
    {
        byte[][] subObservation = getSubObservation(observation);
        ScoreEvaluatorAgentNNInput agentNNInput = new ScoreEvaluatorAgentNNInput(subObservation);
        Map <OutputNeuron, Double> out;
        double currMax = Double.MIN_VALUE;
        ACTION currMaxAction = ACTION.RIGHT;
        
        for(String actionName: actionEvaluators.keySet())
        {
            NeuralNetwork network = actionEvaluators.get(actionName);
            out = network.forwardPropagation(agentNNInput);
        
            //Only one output value
            for(Neuron n: out.keySet())
            {
                if(out.get(n)> currMax)
                {
                    currMax = out.get(n);
                    currMaxAction = ACTION.valueOf(actionName);
                }
            }
        }
        return currMaxAction;
    }
    
    private class ScoreEvaluatorAgentNNInput implements NeuralNetworkInput
    {
        List<Byte> observationAsList = new ArrayList<Byte>();
                
        ScoreEvaluatorAgentNNInput(byte[][] observation)
        {
          for(int i=0;i<observation.length;i++)
              for(int j=0;j<observation.length;j++)
                  observationAsList.add(observation[i][j]);
        }
    
        public List<Byte> getInputList() { return observationAsList; }
        public int size() { return observationAsList.size(); }
    }
    
    public AGENT_TYPE getType() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
    /*
      
      for (int i = 0; i < SubObservation.length; i++)
      {
        for (int j = 0; j < SubObservation.length; j++)
        {
          System.out.print("[");
          System.out.printf("%3d", SubObservation[i][j]);
          System.out.print("]");
        }
        System.out.println("");
      }
      System.out.println("_____________________________________");

  
      action[Mario.KEY_RIGHT] = true;
      action[Mario.KEY_JUMP] = true;
        
      return action;
    }

    public AGENT_TYPE getType() {
        return AGENT_TYPE.AI;
    }

    public String getName() {
        return "ScoreEvaluatorAgent";
    }

    public void setName(String name) {}

}
*/