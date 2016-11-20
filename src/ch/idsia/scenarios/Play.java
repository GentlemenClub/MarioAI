package ch.idsia.scenarios;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.ScoreEvaluatorAgent;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 5, 2009
 * Time: 12:46:43 PM
 */
public class Play {

    public static void main(String[] args) {
        Agent controller = new ScoreEvaluatorAgent();
        //Agent controller = new HumanKeyboardAgent();

        for (int i = 0; i < 100000000; i++) {
            if (args.length > 0) {
                controller = AgentsPool.load(args[0]);
                AgentsPool.addAgent(controller);
            }
            EvaluationOptions options = new CmdLineOptions(new String[0]);
            options.setAgent(controller);
            Task task = new ProgressTask(options);
            options.setMaxFPS(false);
            options.setVisualization(true);
            options.setNumberOfTrials(1);
            options.setMatlabFileName("");
            //options.setLevelRandSeed((int) 0);//(Math.random() * Integer.MAX_VALUE));
            //options.setLevelDifficulty(0);
            options.setLevelRandSeed((int) 1);//(Math.random() * Integer.MAX_VALUE));
            options.setLevelDifficulty(0);
            task.setOptions(options);
            System.out.println("Score: " + task.evaluate(controller)[0]);
            ((ScoreEvaluatorAgent) controller).saveAI();
            ((ScoreEvaluatorAgent) controller).resetMarioValues();
        }
    }
}
