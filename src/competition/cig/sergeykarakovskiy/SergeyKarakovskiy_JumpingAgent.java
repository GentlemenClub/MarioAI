package competition.cig.sergeykarakovskiy;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.environments.Environment;
import ch.idsia.mario.engine.sprites.Mario;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: Sep 1, 2009
 * Time: 3:12:07 PM
 * Package: competition.cig.sergeykarakovskiy
 */

public class SergeyKarakovskiy_JumpingAgent implements Agent
{
    private String name;
    private boolean[] action;

    public SergeyKarakovskiy_JumpingAgent()
    {
        setName("SergeyKarakovskiy_JumpingAgent");
        reset();
    }

    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_SPEED] = true;
    }

    public boolean[] getAction(Environment observation)
    {
        //try {Thread.sleep (1000);}
        //catch (Exception e){}
        action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] =  observation.mayMarioJump() || !observation.isMarioOnGround();

        byte[][] Env= observation.getCompleteObservation();
        for(int i=0;i<Env.length;i++) {
            for (int j = 0; j < Env.length; j++)
            {
                System.out.print("[");
                System.out.printf("%3d",(int)Env[i][j]);
                System.out.print("]");
            }
            System.out.println("");
        }
        System.out.println("_____________________________________");

        return action;
    }


    public Agent.AGENT_TYPE getType()
    {
        return Agent.AGENT_TYPE.AI;
    }

    public String getName() {        return name;    }

    public void setName(String Name) { this.name = Name;    }

}
