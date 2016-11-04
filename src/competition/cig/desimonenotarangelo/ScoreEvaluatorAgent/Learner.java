package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork.*;

import java.io.IOException;
import java.util.*;

public class Learner
{
  private NeuralNetwork network;
  private double epsilon;
  private NeuralNetworkOutput lastNNOutput = null;
  private double alfa = 0.7, gamma = 0.6;
  private String nnFileName = "MarioAI.ai";
  private final static int nActions=32;
  private final static int nButtons=5;
  private OutputNeuron lastChosenActionNeuron;
          
  public Learner(double epsilon)
  {
    try {
      network = new NeuralNetwork(nnFileName);
    } catch (IOException | ClassNotFoundException e) {
      ActivationFunction activationFunction = new BentIdentity();//SigmoidFunction();//
      //ActivationFunction inputActivationFunction = new SigmoidFunction();//
  
      ActionIterator iterator = new ActionIterator();
      String[] ids = new String[nActions];
      int i=0;
      
      while(iterator.hasNext())
      {
        boolean currAction[] = iterator.next();
        String currActionString="";
        for(int j=0; j<currAction.length; j++)
        {
          if(j==0)
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
              .addInputLayer(81)// Environment 9x9
              .addHiddenLayer(30)
              .addHiddenLayer(15)
              .addHiddenLayer(10)
              .addOutputLayer(ids)
              .setEta(0.00000002)
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
  
  /*private static boolean[] oneHotEncodingToAction(boolean action[])
  {
    //Gets Index
    int i=0;
      while(!action[i])
        i++;
    
    String bin = Integer.toBinaryString(i);
    while (bin.length() < 5)
      bin = "0" + bin;
    char[] chars = bin.toCharArray();
    boolean[] boolArray = new boolean[5];
  
    for (int j = 0; j < chars.length; j++)
      boolArray[j] = chars[j] == '0';
    
    return boolArray;
  }*/
  /*
  public static class ActionIterator implements Iterator
  {
    private int count=0;
    private final int nActions=32;
    boolean[] boolArray = new boolean[nActions];
  
    @Override
    public boolean hasNext() { return count < nActions;}
    
    @Override
    public boolean[] next()
    {
      boolArray[count]=true;//Sets current action as true
      
      if(count>0)
        boolArray[count-1]=false;//Set previous action as false
      count++;
      return boolArray;
    }
    
    @Override
    public void remove() { throw new UnsupportedOperationException(); }
  }
  */
  private class NNInput implements NeuralNetworkInput
  {
    List<Double> inputAsList;
    
    NNInput(QState state)
    {
      double[][] observation = state.getObservation();
      int totalInputSize = observation.length*observation.length;
      
      //inputAsList contains all neural network inputs mapped into a single list
      inputAsList = new ArrayList<Double>(totalInputSize);
      
      //Environment input
      for(int i=0;i<observation.length;i++)
        for(int j=0;j<observation.length;j++)
          inputAsList.add(observation[i][j]);
    }
    
    public List<Double> getInputList() { return inputAsList; }
    public int size() { return inputAsList.size(); }
  }
    
  /*public static double getRewardFromMarioMode(int marioMode)
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
  }*/
  
  public boolean[] getAction(QState state)
  {
    double rand = Math.random();
    boolean[] chosenAction;
    System.out.println("----------------------------");
    //Exploration
    if (rand < epsilon)
    {
      chosenAction = getRandomAction(state);
      System.out.println("Exploration");
    }
    //Exploitation
    else
    {
      chosenAction = getBestAction(state);
      System.out.println("Exploitation");
    }
    
    for(OutputNeuron n: lastNNOutput.getFinalOutputs().keySet())
    {
      printActionArray(parseActionString(n.getId()));
      System.out.print("QValue = " + lastNNOutput.getValue(n));
      if(Arrays.equals(parseActionString(n.getId()),chosenAction))
      {
        lastChosenActionNeuron = n;
        System.out.print("  <-------------Chosen Action");
        System.out.print("");
      }
      System.out.println();
    }
    
    System.out.println("----------------------------");
    return chosenAction;
  }
  
  private boolean[] getRandomAction(QState state)
  {
    boolean[] randomAction = new boolean[nButtons];
    Random random = new Random();
    
    for(int i=0; i< nButtons; i++)
      randomAction[i] = random.nextBoolean();
    
    NNInput nnInput = new NNInput(state);
    lastNNOutput = network.forwardPropagation(nnInput);//Needed for backpropagation
    
    return randomAction;
  }
  
  public boolean[] parseActionString(String actionId)
  {
    String[] parts = actionId.split(" ");
    boolean[] action = new boolean[nButtons];
    for(int i =0; i<nButtons; i++)
      action[i] = Boolean.parseBoolean(parts[i]);
    return action;
  }
  
  public boolean[] getBestAction(QState state)
  {
    ActionIterator iterator = new ActionIterator();
    boolean[] currAction;
    double maxQvalue = Double.NEGATIVE_INFINITY;
    boolean[] chosenAction = null;//As long as maxQValue is negative infinity, this variable is always assigned
  
    NNInput nnInput = new NNInput(state);
    NeuralNetworkOutput nnOutput = network.forwardPropagation(nnInput);
    OutputNeuron maxQValueNeuron = nnOutput.getMaxValueNeuron();
    
    chosenAction = parseActionString(maxQValueNeuron.getId());
    lastNNOutput = nnOutput;
    
    if(chosenAction==null)
      System.out.println("OMG");
    
    return chosenAction;
  }
  
  private static void printActionArray(boolean[] action)
  {
    for(int i=0;i<action.length;i++)
    {
      if(action[i])
      {
        switch (i)
        {
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
  
  public void learn(QState nextState, double nextStateReward)
  {
    //double qValueStAt = lastNNOutput.getValue(lastChosenActionNeuron);
    double qValueSt_nextAt_next;

    Map<OutputNeuron,Double> targetOutputs = new HashMap<OutputNeuron,Double>(lastNNOutput.getFinalOutputs());
  
    NNInput nextStateNNInput = new NNInput(nextState);
    NeuralNetworkOutput nnOutput = network.forwardPropagation(nextStateNNInput);
    OutputNeuron nextStateMaxValueNeuron = nnOutput.getMaxValueNeuron();
    qValueSt_nextAt_next = nextStateReward + gamma * nnOutput.getValue(nextStateMaxValueNeuron);
    
    //qValueStAt = qValueStAt + alfa *(nextStateReward + gamma * qValueSt_nextAt_next - qValueStAt);
    
    targetOutputs.put(lastChosenActionNeuron,qValueSt_nextAt_next);
    
    network.backPropagation(lastNNOutput,targetOutputs);
  }
  
  public static void main(String argv[])
  {
    ActionIterator iterator = new ActionIterator();
    boolean[] currAction;
    
    /*while(iterator.hasNext())
    {
      currAction = iterator.next();
      printActionArray(oneHotEncodingToAction(currAction));
    }*/
    //for(int i =0; i<100 ; i++)
    //  printActionArray(oneHotEncodingToAction(getRandomAction()));
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