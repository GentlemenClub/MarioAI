package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class ScoreEvaluatorAgent implements Agent {
    
    private String name;
    private boolean[] action;
    private double lastCoins = 0;
    private int lastMarioMode = 2;
    private final int actionTurns=5;
    private int passedTurns=actionTurns;
    private boolean scoreInitialized = false;
    private Learner myLearner;
    private double lastScore = 0.0;
    private double epsilon = 0.2;
    
    public ScoreEvaluatorAgent()
    {
        this.name = getClass().getName();
        myLearner = new Learner(epsilon);
    }

    public void saveAI() { myLearner.saveStatus(); }
    
    private double getMarioModeValue(int mode)
    {
        switch(mode)
        {
            case 0:
                return 5;
            case 1:
                return 20;
            case 2:
                return 30;
            default:
                return 0;
        }
    }
    
    public double getTotalScore(Environment observation)
    {
        return getLevelPosition(observation)*100 +
                getRewardFromMarioStatus(observation.getMarioStatus())+
                getMarioModeValue(observation.getMarioMode())+
                Mario.coins*10;
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
                if(getTimeLeft()>0)//Gives penalty only if mario is dead for a mistake and not for timeout
                  return -100;
                else
                  return 0;
            case Mario.STATUS_WIN :
                return +100;
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
        double[][] subObservation = new double[9][9];
        byte [][] notNormalizedObservation = new byte[9][9];
        int k = 0, z = 0;
  
      /*for (int i = 0; i < 22; i++)
        for (int j = 0; j < 22; j++)
          CompleteObservation[i][j] = 1;
      */
        for (int i = 7; i < 16; i++)
            for (int j = 7; j < 16; j++) {
                subObservation[z][k] = normalizeValue(completeObservation[i][j],
                                       Byte.MIN_VALUE,Byte.MAX_VALUE,
                                       0,1);
                notNormalizedObservation[z][k] = completeObservation[i][j];
                k++;
                if (k == 9) {
                    k = 0;
                    z++;
                }
            }
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
        
        double normalizedMarioMode = normalizeValue(observation.getMarioMode(),
                                                    0,2,
                                                    0,1);
    
        boolean isMarioIsDead = (observation.getMarioStatus()==Mario.STATUS_DEAD);
        
        QState qState = new QState(getSubObservation(observation));
        if(!scoreInitialized)//First time only
        {
            action = myLearner.getAction(qState);
            passedTurns++;
            lastScore = getTotalScore(observation);
            scoreInitialized = true;
        }
        else if(passedTurns<actionTurns && !isMarioIsDead)//Same action must be done other times
            passedTurns++;
        else//New action need to be decided
        {
            passedTurns = 0;
            double reward = getReward(observation);
            myLearner.learn(qState,reward);
            action = myLearner.getAction(qState);
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