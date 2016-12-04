package competition.cig.desimonenotarangelo.scoreevaluatoragent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class ScoreEvaluatorAgent implements Agent {
    
    private String name;
    private boolean[] action = new boolean[Learner.nButtons];
    private final int actionTurns=2;
    private int passedTurns=actionTurns;
    private boolean scoreInitialized;
    private Learner myLearner;
    private double lastScore;
    
    private final double epsilon = 0.2;
    
    public ScoreEvaluatorAgent()
    {
        this.name = getClass().getName();
        myLearner = new Learner(epsilon);
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
  
   
    
    private double getMarioModeValue(int mode)
    {
        switch(mode)
        {
            case 0:
                return 0;
            case 1:
                return 200;
            case 2:
                return 300;
            default:
                return 0;
        }
    }
    
    public void resetMarioValues()
    {
        lastScore = 0.0;
        action = new boolean[Learner.nButtons];
        scoreInitialized = false;
        myLearner.reset();
    }
    
    public double getTotalScore(Environment observation)
    {
        return  ((int)getLevelPosition(observation))+
                getRewardFromMarioStatus(observation.getMarioStatus())+
                getMarioModeValue(observation.getMarioMode())+
                observation.getKillsTotal()*100+
                Mario.coins;
    }
    
    private double getReward(Environment observation) { return getTotalScore(observation) - lastScore; }
    
    private double getRewardFromMarioStatus(int marioStatus)
    {
        switch(marioStatus)
        {
            case Mario.STATUS_DEAD :
                if(getTimeLeft()>0)//Gives penalty only if mario is dead for a mistake and not for timeout
                  return -300;
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
    
    public boolean[] getAction(Environment observation) {
    
        //if(gameFinished)
        //
        //#   return new boolean[5];
        
        boolean isMarioDead = (observation.getMarioStatus()==Mario.STATUS_DEAD);
        boolean gameFinished = (observation.getMarioStatus()==Mario.STATUS_WIN);
                
        if(!scoreInitialized)//First time only
        {
            action = myLearner.getAction(observation);
            lastScore = getTotalScore(observation);
            scoreInitialized = true;
            passedTurns++;
        }
        else if(passedTurns<actionTurns && !isMarioDead)//Same action must be done other times
            passedTurns++;
        else if(!gameFinished) //New action need to be decided
        {
            passedTurns = 0;
            //First, reward is given based on current world state
            double reward = getReward(observation);
            //Then backpropagate properly this reward on my history
            myLearner.learn(observation,reward);

            action = myLearner.getAction(observation);
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