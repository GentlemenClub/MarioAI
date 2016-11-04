package ch.idsia.scenarios;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.ScoreEvaluatorAgent;
import competition.cig.sergeykarakovskiy.SergeyKarakovskiy_JumpingAgent;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 5, 2009
 * Time: 12:46:43 PM
 */
public class Play {

    public static void main(String[] args)
    {
      Agent controller = new ScoreEvaluatorAgent();
      
      for(int i=0 ; i<1000000; i++)
      {
        if (args.length > 0)
        {
          controller = AgentsPool.load(args[0]);
          AgentsPool.addAgent(controller);
        }
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        Task task = new ProgressTask(options);
        options.setMaxFPS(true);
        options.setVisualization(true);
        options.setNumberOfTrials(1);
        options.setMatlabFileName("");
        options.setLevelRandSeed(1);//((int) (Math.random() * Integer.MAX_VALUE));
        options.setLevelDifficulty(1);
        task.setOptions(options);
        System.out.println("Score: " + task.evaluate(controller)[0]);
        ((ScoreEvaluatorAgent) controller).saveAI();
      }
    }
}
