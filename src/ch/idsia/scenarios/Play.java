package ch.idsia.scenarios;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.MarIA;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 5, 2009
 * Time: 12:46:43 PM
 */
public class Play {

    public static void main(String[] args) {
        Agent controller = new MarIA();
        //Agent controller = new HumanKeyboardAgent();
        Random rand = new Random();
        for (int i = 0; i < 100000000; i++) {
            if (args.length > 0) {
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
            options.setLevelRandSeed((int) (Math.random() * Integer.MAX_VALUE));
            //options.setLevelRandSeed(1859719211);
            options.setLevelDifficulty(1);//(rand.nextInt(3));
            task.setOptions(options);
            System.out.println("Score: " + task.evaluate(controller)[0]);

            ((MarIA) controller).saveAI();
            controller.reset();
        }
    }
}
