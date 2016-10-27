package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class ScoreEvaluatorAgent implements Agent {
    
    private String name;
    private boolean[] action;
    private double lastScore = 0;
    private final int actionTurns=5;
    private int passedTurns=actionTurns;
    private boolean scoreInitialized = false;
    private Learner myLearner;
    
    public ScoreEvaluatorAgent()
    {
        this.name = getClass().getName();
        myLearner = new Learner(0.2);
    }

    public void saveAI() { myLearner.saveStatus(); }
    
    private double getDeltaScore(Environment Observation) {
        double totalScore = getTotalScore(Observation);
        double deltaScore = totalScore - lastScore;
        lastScore = totalScore;
        return deltaScore;
    }
    
    public double getTotalScore(Environment observation)
    {
      return  normalizeValue(getLevelPosition(observation),
                             0,320,
                             0,1);//
              //Mario.coins*5 +
              //Learner.getRewardFromMarioMode(observation.getMarioMode()) +
              //observation.getKillsByStomp()*5;
    }
    
    public void reset() {}
    
    public int getTimeLeft() {return LevelScene.timeLeft/15;}
    
    public double getLevelPosition(Environment observation)
    {
        return normalizeValue(observation.getMarioFloatPos()[0]/16,
                              0,320,
                              0,1);
    }
    
    //gets submatrix 9x9
    public static double[][] getSubObservation(Environment Observation) {
        byte[][] completeObservation = Observation.getCompleteObservation();
        double[][] subObservation = new double[9][9];
        
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
                k++;
                if (k == 9) {
                    k = 0;
                    z++;
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
        
        double normalizedMarioMode = normalizeValue(observation.getMarioMode(),
                                                    0,2,
                                                    0,1);
        
        QState qState = new QState(getSubObservation(observation),
                                   getLevelPosition(observation),
                                   normalizedMarioMode);
        if(!scoreInitialized)//First time only
        {
            lastScore = getTotalScore(observation);
            scoreInitialized = true;
            action = myLearner.getAction(qState);
            passedTurns++;
        }
        else if(passedTurns<actionTurns)//Same action must be done other times
            passedTurns++;
        else//New action need to be decided
        {
            passedTurns = 0;
            double reward = getDeltaScore(observation);
            myLearner.learn(qState,reward);
            action = myLearner.getAction(qState);
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