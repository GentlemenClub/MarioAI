package ch.idsia.scenarios;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import competition.cig.desimonenotarangelo.scoreevaluatoragent.MarIA;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 5, 2009
 * Time: 12:46:43 PM
 */
public class Play {

    public static void main(String[] args) throws IOException{
        Agent controller = new MarIA("");
        //Agent controller = new HumanKeyboardAgent();
        Random rand = new Random();
        int epochs = 1000;
        String cvsFileName = "results.csv";
        FileWriter csvWriter = new FileWriter(cvsFileName);

        for (int i = 0; i < epochs; i++) {
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
            options.setLevelDifficulty(0);
            task.setOptions(options);
            System.out.println("Score: " + task.evaluate(controller)[0]);

            Double lastScore = ((MarIA) controller).getLastScore();
            Boolean hasWon = ((MarIA) controller).hasWon();
            writeLine(csvWriter, Arrays.asList(lastScore.toString(), hasWon.toString()), ',', ' ');

            if (i == 9) {
                ((MarIA) controller).saveAI("epoch10.ai");
            }else if (i == 99) {
                ((MarIA) controller).saveAI("epoch100.ai");
            } else if (i == 999) {
                ((MarIA) controller).saveAI("epoch1000.ai");
            }

            controller.reset();
        }

        csvWriter.flush();
        csvWriter.close();
    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = ',';
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }

    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;
    }
}
