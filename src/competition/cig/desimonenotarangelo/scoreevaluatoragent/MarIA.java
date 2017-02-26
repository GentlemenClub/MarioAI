package competition.cig.desimonenotarangelo.scoreevaluatoragent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class MarIA implements Agent {
    private String name;
    private boolean[] action = new boolean[Learner.nButtons];
    private final int actionTurns = 2;
    private int passedTurns = actionTurns;
    private boolean scoreInitialized;
    private Learner myLearner;

    private double lastScore;
    private boolean wins;

    private final double epsilon = 0.2;

    public MarIA(String fileName) {
        this.name = getClass().getName();
        myLearner = new Learner(epsilon, fileName);
        reset();
    }

    public double getLastScore() {
        return lastScore;
    }

    public boolean hasWon() {
        return wins;
    }

    public void saveAI(String fileName) {
        myLearner.saveStatus(fileName);
    }

    private double getMarioModeValue(int mode) {
        switch (mode) {
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

    public double getTotalScore(Environment observation) {
        return ((int) getLevelPosition(observation)) +
                getRewardFromMarioStatus(observation.getMarioStatus()) +
                getMarioModeValue(observation.getMarioMode()) +
                observation.getKillsTotal() * 100 +
                Mario.coins;
    }

    private double getReward(Environment observation) {
        return getTotalScore(observation) - lastScore;
    }

    private double getRewardFromMarioStatus(int marioStatus) {
        switch (marioStatus) {
            case Mario.STATUS_DEAD:
                if (getTimeLeft() > 0)//Gives penalty only if mario is dead for a mistake and not for timeout
                    return -300;
            default:
                return 0;
        }
    }

    public void reset() {
        lastScore = 0.0;
        action = new boolean[Learner.nButtons];
        scoreInitialized = false;
        myLearner.reset();
        wins = false;
    }

    public int getTimeLeft() {
        return LevelScene.timeLeft / 15;
    }

    public double getLevelPosition(Environment observation) {
        double position = observation.getMarioFloatPos()[0];
        //System.out.println("Position: " + position);
        return position;
    }

    public boolean[] getAction(Environment observation) {
        boolean isMarioDead = (observation.getMarioStatus() == Mario.STATUS_DEAD);
        boolean gameFinished = (observation.getMarioStatus() == Mario.STATUS_WIN);

        //First time only
        if (!scoreInitialized) {
            action = myLearner.getAction(observation);
            lastScore = getTotalScore(observation);
            scoreInitialized = true;
            passedTurns++;
        } else if (passedTurns < actionTurns && !isMarioDead)//Same action must be done other times
            passedTurns++;
        //New action need to be decided
        else if (!gameFinished) {
            passedTurns = 0;
            //First, reward is given based on current world state
            double reward = getReward(observation);
            //Then backpropagate properly this reward on my history
            myLearner.learn(observation, reward);

            action = myLearner.getAction(observation);
            lastScore = getTotalScore(observation);
        } else {
            wins = true;
            System.out.println("Game Finished! ^.^");
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

    public static void main(String args[]) {
    }
}