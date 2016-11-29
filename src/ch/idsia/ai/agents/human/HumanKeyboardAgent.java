package ch.idsia.ai.agents.human;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static competition.cig.desimonenotarangelo.scoreevaluatoragent.PatternHoleRecognition.getFormattedMatrix;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 29, 2009
 * Time: 12:19:49 AM
 * Package: ch.idsia.ai.agents.ai;
 */
public class HumanKeyboardAgent extends KeyAdapter implements Agent {
    List<boolean[]> history = new ArrayList<boolean[]>();
    private boolean[] Action = null;
    private String Name = "HumanKeyboardAgent";
    private double lastScore=0;
    
    public HumanKeyboardAgent() {
        this.reset();
//        RegisterableAgent.registerAgent(this);
    }

    public void reset() {
        // Just check you keyboard. Especially arrow buttons and 'A' and 'S'!
        Action = new boolean[Environment.numberOfButtons];
    }

    public boolean[] getAction(Environment observation) {
        byte[][] completeObservation = observation.getCompleteObservation();
        String[] formattedObservation = getFormattedMatrix(completeObservation);

        //System.out.println(arrayToString(formattedObservation));
    
        //MarioHoleStatus currentHoleStatus = getMarioHoleStatus(observation);
        //if(currentHoleStatus.equals(PatternHoleRecognition.MarioHoleStatus.AFTER))
        //System.out.println(currentHoleStatus.toString());
        System.out.println(getReward(observation));
        lastScore = getTotalScore(observation);
        return Action;
    }
    
    public double getLevelPosition(Environment observation)
    {
        return observation.getMarioFloatPos()[0];
        //return normalizeValue(observation.getMarioFloatPos()[0]/16,
        //                      0,320,
        //                      0,1);
    }
    
    public int getTimeLeft() {return LevelScene.timeLeft/15;}
    
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
    
    public double getTotalScore(Environment observation)
    {
        return  ((int)getLevelPosition(observation))+
                getRewardFromMarioStatus(observation.getMarioStatus())+
                getMarioModeValue(observation.getMarioMode())+
                observation.getKillsTotal()*100+
                Mario.coins*10;
    }
    
    private double getMarioModeValue(int mode)
    {
        switch(mode)
        {
            case 0:
                return 0;
            case 1:
                return 400;
            case 2:
                return 600;
            default:
                return 0;
        }
    }
    
    public void resetMarioValues()
    {
        lastScore = 0.0;
    }
    
    private double getReward(Environment observation)
    {
        double reward = getTotalScore(observation) - lastScore;
        return reward;
    }
    
    
    private String arrayToString(String[] a) {
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        //b.append('[');
        for (int i = 0; ; i++) {
            b.append(String.valueOf(a[i]));
            if (i == iMax)
                return b/*.append(']')*/.toString();
            b.append(",\n");
        }
    }

    public AGENT_TYPE getType() {
        return AGENT_TYPE.HUMAN;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public void keyPressed(KeyEvent e) {
        toggleKey(e.getKeyCode(), true);
        //System.out.println("sdf");
    }

    public void keyReleased(KeyEvent e) {
        toggleKey(e.getKeyCode(), false);
    }


    private void toggleKey(int keyCode, boolean isPressed) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                Action[Mario.KEY_LEFT] = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                Action[Mario.KEY_RIGHT] = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                Action[Mario.KEY_DOWN] = isPressed;
                break;

            case KeyEvent.VK_S:
                Action[Mario.KEY_JUMP] = isPressed;
                break;
            case KeyEvent.VK_A:
                Action[Mario.KEY_SPEED] = isPressed;
                break;
        }
    }

    public List<boolean[]> getHistory() {
        return history;
    }
}
