package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import ch.idsia.mario.environments.Environment;
import competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.ScoreEvaluatorAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Learner
{
  /*
  public byte[] getBestAction(byte[][] State)
  {
    ScoreEvaluatorAgent.ScoreEvaluatorAgentNNInput agentNNInput = new ScoreEvaluatorAgent.ScoreEvaluatorAgentNNInput(subObservation);
    Map<OutputNeuron, Double> out;
    double currMax = Double.NEGATIVE_INFINITY;
    ScoreEvaluatorAgent.ACTION currMaxAction = ScoreEvaluatorAgent.ACTION.RIGHT;
    
    for (String actionName : actionEvaluators.keySet())
    {
      NeuralNetwork network = actionEvaluators.get(actionName);
      out = network.forwardPropagation(agentNNInput);
  
      //Only one output value
      for (Neuron n : out.keySet())
      {
        if (out.get(n) > currMax)
        {
          currMax = out.get(n);
          currMaxAction = ScoreEvaluatorAgent.ACTION.valueOf(actionName);
        }
      }
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
  }*/
}