package competition.cig.desimonenotarangelo.scoreevaluatoragent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

import java.util.LinkedList;

import static competition.cig.desimonenotarangelo.scoreevaluatoragent.PatternHoleRecognition.*;

public class ScoreEvaluatorAgent implements Agent {
    
    private String name;
    private boolean[] action;
    private double lastCoins = 0;
    private int lastMarioMode = 2;
    private final int actionTurns=2;
    private int passedTurns=actionTurns;
    private boolean scoreInitialized = false;
    private Learner myLearner;
    private double lastScore;
    private int nJumpedHoles;
    private double lastHolePosX;
    private PatternHoleRecognition.MarioHoleStatus lastMarioHoleStatus;
    //private boolean gameFinished;
    
    private LinkedList<QState> stateHistory;
    
    private final int historySize = 1;
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
    
    private void updateHistory(Environment observation)
    {
        double normalizedMarioMode = normalizeValue(observation.getMarioStatus(),
                0, 2,
                0, 1);
        
        if(stateHistory.size()==historySize)
        {
          stateHistory.removeFirst();
          stateHistory.addLast(new QState(getSubObservation(observation),normalizedMarioMode));
        }
        else//First time only
        {
            for(int i=0; i<historySize; i++)
              stateHistory.addLast(new QState(getSubObservation(observation),normalizedMarioMode));
        }
    }
    
    private double getMarioModeValue(int mode)
    {
        switch(mode)
        {
            case 0:
                return 10;
            case 1:
                return 70;
            case 2:
                return 100;
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
        //gameFinished = false;
    }
    
    public double getTotalScore(Environment observation)
    {
        return  getLevelPosition(observation)+
                getRewardFromMarioStatus(observation.getMarioStatus())+
                getMarioModeValue(observation.getMarioMode())+
                observation.getKillsTotal()*10+
                Mario.coins+
                nJumpedHoles*10;
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
                  return -100;
                else
                  return 0;
            case Mario.STATUS_WIN :
                //gameFinished = true;
                return 10000;
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
    
    //gets submatrix 9x9
    public static double[][] getSubObservation(Environment Observation) {
        byte[][] completeObservation = Observation.getCompleteObservation();
        double[][] subObservation = new double[22][22];
        //byte [][] notNormalizedObservation = new byte[9][9];
        //int k = 0, z = 0;
  
      for (int i = 0; i < 22; i++)
      {
          for (int j = 0; j < 22; j++)
          {
              //CompleteObservation[i][j] = 1;
              subObservation[i][j] = normalizeValue(completeObservation[i][j],
                      Byte.MIN_VALUE, Byte.MAX_VALUE,
                      0, 1);
          }
      }//for (int i = 7; i < 16; i++)
         //   for (int j = 7; j < 16; j++) {
                //subObservation[i][j] = normalizeValue(completeObservation[i][j],
                 //                      Byte.MIN_VALUE,Byte.MAX_VALUE,
                  //                     0,1);
                //notNormalizedObservation[z][k] = completeObservation[i][j];
                //k++;
                //if (k == 9) {
                 //   k = 0;
                  //  z++;
               // }
            //}
    /*   System.out.println("---------------------------------------");
    
        for (int i = 0; i < 9; i++)
      {
          for (int j = 0; j < 9; j++)
              System.out.printf("[ %3f]", subObservation[i][j]);
          System.out.println();
      }
        System.out.println("---------------------------------------");
    
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
                System.out.printf("[ %3d]", notNormalizedObservation[i][j]);
            System.out.println();
        }
      
        System.out.println("---------------------------------------");
    */
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
        
        double normalizedMarioMode = normalizeValue(observation.getMarioMode(),
                                                    0,2,
                                                    0,1);
        
        boolean isMarioDead = (observation.getMarioStatus()==Mario.STATUS_DEAD);
        
        if(!scoreInitialized)//First time only
        {
            updateHistory(observation);
            action = myLearner.getAction(stateHistory);
            passedTurns++;
            lastScore = getTotalScore(observation);
            scoreInitialized = true;
        }
        else if(passedTurns<actionTurns && !isMarioDead)//Same action must be done other times
            passedTurns++;
        else//New action need to be decided
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
            double reward = getReward(observation);
            updateHistory(observation);
            myLearner.learn(stateHistory,reward);
            action = myLearner.getAction(stateHistory);
            lastScore = getTotalScore(observation);
        }
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