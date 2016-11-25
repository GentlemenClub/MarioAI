package competition.cig.desimonenotarangelo.scoreevaluatoragent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

import java.util.LinkedList;

import static competition.cig.desimonenotarangelo.scoreevaluatoragent.PatternHoleRecognition.*;

public class ScoreEvaluatorAgent implements Agent {
    
    private String name;
    private boolean[] action = new boolean[Learner.nButtons];
    private final int actionTurns=2;
    private int passedTurns=actionTurns;
    private boolean scoreInitialized;
    private Learner myLearner;
    private double lastScore;
    private int nJumpedHoles;
    private double lastHolePosX;
    private PatternHoleRecognition.MarioHoleStatus lastMarioHoleStatus;
    
    private LinkedList<QState> stateHistory;
    
    private final int historySize = 4;
    private final double epsilon = 0.2;
    private final double holePosEpsilon = 200;
    
    public ScoreEvaluatorAgent()
    {
        this.name = getClass().getName();
        myLearner = new Learner(epsilon);
        stateHistory = new LinkedList<QState>();
        resetMarioValues();
    }

    public void saveAI() { myLearner.saveStatus(); }
    
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
  
    private void initHistory(Environment observation)
    {
      double normalizedMarioMode = normalizeValue(observation.getMarioStatus(),
              0, 2,
              0, 1);
      
      if(stateHistory.isEmpty())
      {
        for(int i=0; i<historySize; i++)
          stateHistory.addFirst(new QState(
                  getNormalizedObservation(observation),
                  getDoubleActionFromBoolean(new boolean[Learner.nButtons]),
                  normalizedMarioMode));
      }
      else
        throw new UnsupportedOperationException("History cannot be initialized twice.");
    }
    
    private void updateHistory(Environment observation)
    {
        double normalizedMarioMode = normalizeValue(observation.getMarioStatus(),
                0, 2,
                0, 1);
  
        double[] doubleAction = getDoubleActionFromBoolean(action);
      
        if(stateHistory.size()==historySize)
        {
          stateHistory.removeLast();
          stateHistory.addFirst(new QState(getNormalizedObservation(observation), doubleAction, normalizedMarioMode));
        }
        else
          throw new UnsupportedOperationException("History needs to be initialized");
    }
    
    private double getMarioModeValue(int mode)
    {
        switch(mode)
        {
            case 0:
                return 0;
            case 1:
                return 300;
            case 2:
                return 500;
            default:
                return 0;
        }
    }
    
    public void resetMarioValues()
    {
        lastScore = 0.0;
        nJumpedHoles = 0;
        lastHolePosX = 0.0;
        lastMarioHoleStatus = PatternHoleRecognition.MarioHoleStatus.BEFORE;
        stateHistory.clear();
        action = new boolean[Learner.nButtons];
        scoreInitialized = false;
    }
    
    public double getTotalScore(Environment observation)
    {
        return  (((int)getLevelPosition(observation))+
                getRewardFromMarioStatus(observation.getMarioStatus())+
                getMarioModeValue(observation.getMarioMode())+
                observation.getKillsTotal()*100+
                Mario.coins*50+
                nJumpedHoles*100)*100;
    }
    
    private double getReward(Environment observation)
    {
        //Reward is 0 normally, ultra positive on level end, ultra negative on death
        //double statusScore = getRewardFromMarioStatus(observation.getMarioStatus());
    
        //Reward is always given depending on how far in the level mario is
        //double levelPosition = observation.getMarioFloatPos()[0]/16;//getLevelPosition(observation);

        //Delta is needed to check if mario got hit
        //double deltaMarioModeValue = getMarioModeValue(observation.getMarioMode()) - getMarioModeValue(lastMarioMode);
        //lastMarioMode=observation.getMarioMode();
        
        //Delta is needed to understand when coin is obtained
        //double deltaCoins = Mario.coins - lastCoins;
        //lastCoins = Mario.coins;
      
        //return statusScore + levelPosition*10 + deltaMarioModeValue + deltaCoins*10;
        double reward = getTotalScore(observation) - lastScore;
        return reward;
    }
    
    /*public double getTotalScore(Environment observation)
    {
      //double normalizedLevelPos = observation.getMarioFloatPos()[0];
        //double normalizedRewardFromMarioStatus = getRewardFromMarioStatus(observation.getMarioStatus());
        //double coins = normalizeValue(Mario.coins,
        //        0,100,
        //        0,1);
    
        double sum = Mario.coins*100 +
                getRewardFromMarioStatus(observation.getMarioStatus());
        
        return sum;
        //observation.getMarioMode(
        //Mario.coins*5 +
              //Learner.getRewardFromMarioMode(observation.getMarioMode()) +
              //observation.getKillsByStomp()*5;
    }*/
    
    private double getRewardFromMarioStatus(int marioStatus)
    {
        switch(marioStatus)
        {
            case Mario.STATUS_DEAD :
                //gameFinished = true;
                if(getTimeLeft()>0)//Gives penalty only if mario is dead for a mistake and not for timeout
                  return -600;
                else
                  return 0;
           // case Mario.STATUS_WIN :
                //gameFinished = true;
            //    return 10000;
            default :
                return 0;
        }
    }
    
    public void reset() {}
    
    public int getTimeLeft() {return LevelScene.timeLeft/15;}
    
    public double getLevelPosition(Environment observation)
    {
        return observation.getMarioFloatPos()[0];
        //return normalizeValue(observation.getMarioFloatPos()[0]/16,
        //                      0,320,
        //                      0,1);
    }
    
  
    public static double[][] getNormalizedObservation(Environment Observation) {
        byte[][] completeObservation = Observation.getCompleteObservation();
        double[][] subObservation = new double[11][11];
      int h=0,k=0;
      for (int i = 5; i < 16; i++)
      {
          for (int j = 5; j < 16; j++)
          {
              //CompleteObservation[i][j] = 1;
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
    
    private static double normalizeValue(double x,
                                  double dataLow, double dataHigh,
                                  double normalizedLow, double normalizedHigh)
    {
        return ((x - dataLow)
                / (dataHigh - dataLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }
    
    public boolean[] getAction(Environment observation) {
    
        //if(gameFinished)
        //
        //#   return new boolean[5];
        
        boolean isMarioDead = (observation.getMarioStatus()==Mario.STATUS_DEAD);
        boolean gameFinished = (observation.getMarioStatus()==Mario.STATUS_WIN);
                
        if(!scoreInitialized)//First time only
        {
            initHistory(observation);
            action = myLearner.getAction(stateHistory);
            updateHistory(observation);
            passedTurns++;
            lastScore = getTotalScore(observation);
            scoreInitialized = true;
        }
        else if(passedTurns<actionTurns && !isMarioDead)//Same action must be done other times
            passedTurns++;
        else if(!gameFinished) //New action need to be decided
        {
            MarioHoleStatus currentHoleStatus = getMarioHoleStatus(observation);
            if(!lastMarioHoleStatus.equals(MarioHoleStatus.AFTER) && currentHoleStatus.equals(MarioHoleStatus.AFTER) &&
                    lastHolePosX < observation.getMarioFloatPos()[0] - holePosEpsilon)
            {
                nJumpedHoles++;
                lastHolePosX = observation.getMarioFloatPos()[0];
            }
            
            lastMarioHoleStatus = currentHoleStatus;
            passedTurns = 0;
            //First, reward is given based on current world state
            double reward = getReward(observation);
            //Then backpropagate properly this reward on my history
            myLearner.learn(stateHistory,reward);
            //Now new action is elaborated by the learner
            action = myLearner.getAction(stateHistory);
            //History is updated with new decisions
            updateHistory(observation);
            //Score is updated for next delta-reward calculation
            lastScore = getTotalScore(observation);
        }
        else
          System.out.println("Game Finished! ^.^");
        
        return action;
    }
    
    public AGENT_TYPE getType() {
        return AGENT_TYPE.AI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public static void main (String args[])
    {}
}