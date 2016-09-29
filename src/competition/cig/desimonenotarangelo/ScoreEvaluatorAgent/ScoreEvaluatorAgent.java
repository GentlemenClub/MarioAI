package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;


import static competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.ScoreEvaluatorAgent.ACTION.*;
import static java.lang.Thread.sleep;


public class ScoreEvaluatorAgent implements Agent
{
  public enum ACTION
  {
    LEFT, RIGHT, DOWN, JUMP, SPEED,
    LEFT_JUMP, RIGHT_JUMP, SPEED_JUMP, RIGHT_SPEED, LEFT_SPEED
  }
  
  ;
  
  private final boolean[] Action;
  private double lastScore = 0;
  
  
  public ScoreEvaluatorAgent()
  {
    Action = new boolean[Environment.numberOfButtons];
  }
  
  void setAction(ACTION ChosenAction)
  {
    //Sets all actions to false
    //No button pressed
    
    for (int i = 0; i < 5; i++)
      Action[i] = false;
    
    if (ChosenAction == LEFT)
      Action[Mario.KEY_LEFT] = true;
    else if (ChosenAction == RIGHT)
      Action[Mario.KEY_RIGHT] = true;
    else if (ChosenAction == DOWN)
      Action[Mario.KEY_DOWN] = true;
    else if (ChosenAction == JUMP)
      Action[Mario.KEY_JUMP] = true;
    else if (ChosenAction == SPEED)
      Action[Mario.KEY_SPEED] = true;
    else if (ChosenAction == LEFT_JUMP)
    {
      Action[Mario.KEY_LEFT] = true;
      Action[Mario.KEY_JUMP] = true;
    } else if (ChosenAction == RIGHT_JUMP)
    {
      Action[Mario.KEY_RIGHT] = true;
      Action[Mario.KEY_JUMP] = true;
    } else if (ChosenAction == SPEED_JUMP)
    {
      Action[Mario.KEY_SPEED] = true;
      Action[Mario.KEY_JUMP] = true;
    } else if (ChosenAction == RIGHT_SPEED)
    {
      Action[Mario.KEY_RIGHT] = true;
      Action[Mario.KEY_SPEED] = true;
    } else if (ChosenAction == LEFT_SPEED)
    {
      Action[Mario.KEY_LEFT] = true;
      Action[Mario.KEY_SPEED] = true;
    }
    
  }
  
  
  //Total score on which NeuralNetwork configuration will tuned
  private double getTotalScore(Environment Observation)
  {
    //Change the values in order to give more importance
    //to certain actions
    int killScore = Observation.getKillsTotal() * 50;
    int marioModeScore = Observation.getMarioMode() * 200;
    int coinScore = Mario.coins * 50;
    int flowerScore = Mario.gainedFlowers * 150;
    int mushroomScore = Mario.gainedMushrooms * 100;
    double marioProgress = Observation.getMarioFloatPos()[0];
    //double marioHigh = Observation.getMarioFloatPos()[1];//Needed for jumping holes?
    
    return (double) (marioProgress +
            //              marioHigh      +
            killScore +
            coinScore +
            flowerScore +
            marioModeScore +
            mushroomScore);
  }
  
  private double getDeltaScore(Environment Observation)
  {
    double totalScore = getTotalScore(Observation);
    double deltaScore = totalScore - lastScore;
    lastScore = totalScore;
    return deltaScore;
  }
  
  //gets submatrix 7x7 around Mario DOESN'T WORK YET
  public static byte[][] getSubObservation(Environment Observation)
  {
    byte[][] CompleteObservation = Observation.getCompleteObservation();
    byte[][] SubObservation = new byte[9][9];
    int k = 0, z = 0;
  
      /*for (int i = 0; i < 22; i++)
        for (int j = 0; j < 22; j++)
          CompleteObservation[i][j] = 1;
      */
    for (int i = 7; i < 16; i++)
      for (int j = 7; j < 16; j++)
      {
        SubObservation[z][k] = CompleteObservation[i][j];
        k++;
        if (k == 9)
        {
          k = 0;
          z++;
        }
      }
    return SubObservation;
  }
  
  public void reset() {}
  
  public boolean[] getAction(Environment Observation)
  {
    
    byte[][] SubObservation = getSubObservation(Observation);
  }
  
  public AGENT_TYPE getType()
  {
    return null;
  }
  
  public String getName()
  {
    return null;
  }
  
  public void setName(String name)
  {
    
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
    /*
  
      Action[Mario.KEY_RIGHT] = true;
      Action[Mario.KEY_JUMP] = true;
        
      return Action;
    }

    public AGENT_TYPE getType() {
        return AGENT_TYPE.AI;
    }

    public String getName() {
        return "ScoreEvaluatorAgent";
    }

    public void setName(String name) {}

}
