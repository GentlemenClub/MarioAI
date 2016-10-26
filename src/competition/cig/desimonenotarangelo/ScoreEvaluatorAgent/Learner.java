package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork.*;

import java.io.IOException;
import java.util.*;

public class Learner
{
  private NeuralNetwork network;
  private double epsilon;
  private NeuralNetworkOutput lastNNOutput = null;
  private double alfa = 0.7, gamma = 0.4;
  private String nnFileName = "MarioAI.ai";
  
  public Learner(double epsilon)
  {
    try {
      network = new NeuralNetwork(nnFileName);
    } catch (IOException | ClassNotFoundException e) {
      ActivationFunction activationFunction = new BentIdentity();
      network = new NeuralNetworkBuilder()
              .setInputLayerActivationFunction(activationFunction)
              .setHiddenLayersActivationFunction(activationFunction)
              .setOutputLayerActivationFunction(activationFunction)
              .addInputLayer(88)
              .addHiddenLayer(22)
              .addHiddenLayer(22)
              .addOutputLayer(1)
              .setEta(0.0000002)
              .build();
      System.out.println("Creating new neural network");
    }
    this.epsilon = epsilon;
  }
  
  public void saveStatus()
  {
    network.saveNeuralNetwork(nnFileName);
  }
  
  private static class ActionIterator implements Iterator
  {
    private final int length=5;
    private int count=0;
    
    @Override
    public boolean hasNext() { return count < Math.pow(2,length); }
  
    @Override
    public boolean[] next()
    {
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
    public void remove() { throw new UnsupportedOperationException(); }
  }
  
  private class NNInput implements NeuralNetworkInput
  {
    List<Double> inputAsList;
    
    NNInput(QState state, boolean[] actions)
    {
      byte[][] observation = state.getObservation();
      int totalInputSize = observation.length*observation.length + actions.length + 2;
      
      //inputAsList contains all neural network inputs mapped into a single list
      inputAsList = new ArrayList<Double>(totalInputSize);
      
      //Environment input
      for(int i=0;i<observation.length;i++)
        for(int j=0;j<observation.length;j++)
          inputAsList.add((double)observation[i][j]);
  
      //Action input
      for(int i=0;i<actions.length;i++)
        inputAsList.add(actions[i] ? 1.0 : 0.0);
  
      inputAsList.add(state.getLevelPosition());
      inputAsList.add(getRewardFromMarioMode(state.getMarioMode()));
    }
    
    public List<Double> getInputList() { return inputAsList; }
    public int size() { return inputAsList.size(); }
  }
    
  public static double getRewardFromMarioMode(int marioMode)
  {
    switch(marioMode)
    {
      case 2:
        return 40;
      case 1:
        return 30;
      case 0:
        return 10;
      default:
        return 0;
    }
  }
  
  private boolean[] getRandomAction()
  {
    boolean[] randomAction = new boolean[5];
    Random random = new Random();
    
    for(int i=0; i< randomAction.length; i++)
        randomAction[i]=random.nextBoolean();
    
    return randomAction;
  }
  
  public boolean[] getAction(QState state)
  {
    double rand = Math.random();
    boolean[] chosenAction = getRandomAction();
    double maxQvalue = Double.NEGATIVE_INFINITY;
  
    //Exploration
    if (rand < epsilon)
    {
      NNInput nnInput = new NNInput(state, chosenAction);
      lastNNOutput = network.forwardPropagation(nnInput);//Needed for backpropagation
      System.out.println(getQvalue(lastNNOutput));
    }
    //Exploitation
    else
    {
      ActionIterator iterator = new ActionIterator();
      boolean[] currAction;
  
      while (iterator.hasNext())
      {
        currAction = iterator.next();
        NNInput nnInput = new NNInput(state, currAction);
        NeuralNetworkOutput nnOutput = network.forwardPropagation(nnInput);
        double currQvalue = getQvalue(nnOutput);
        if (currQvalue > maxQvalue)
        {
          maxQvalue = currQvalue;
          chosenAction = currAction;
          lastNNOutput = nnOutput;//Needed for backpropagation
        }
      }
    }
    return chosenAction;
  }
  
  public void learn(QState nextState, double nextStateReward)//nuovo stato
  {
    double qValueStAt = getQvalue(lastNNOutput);
    double qValueSt_nextAt_next;

    Map<OutputNeuron,Double> targetOutput = new HashMap<OutputNeuron,Double>(lastNNOutput.getFinalOutputs());
  
    NNInput nnInput = new NNInput(nextState, getAction(nextState));
    NeuralNetworkOutput nnOutput = network.forwardPropagation(nnInput);
    qValueSt_nextAt_next = getQvalue(nnOutput);
            
    qValueStAt = qValueStAt + alfa *(nextStateReward + gamma * qValueSt_nextAt_next - qValueStAt);
    
    setTargetQvalue(targetOutput,qValueStAt);
    
    network.backPropagation(lastNNOutput,targetOutput);
  }
  
  private void setTargetQvalue(Map<OutputNeuron,Double> targetOutput, double qValue)
  {
    if(targetOutput.size()!=1)
      throw new IllegalArgumentException();
    
    for(OutputNeuron n: targetOutput.keySet())
      targetOutput.put(n,qValue);
  }
  
  private double getQvalue(NeuralNetworkOutput nnOutput)
  {
    if(nnOutput.getFinalOutputs().size()!=1)
      throw new IllegalArgumentException();
    
    for(OutputNeuron n: nnOutput.getFinalOutputs().keySet())
      return nnOutput.getFinalOutputs().get(n);
    
    return 0.0;
  }
  
  /*
  public byte[] getBestAction(byte[][] QState)
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