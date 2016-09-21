package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.jblas.NDArray;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import java.util.Arrays;


public class ScoreEvaluatorAgent implements Agent{

    private final boolean[] Action;
    private final MultiLayerNetwork NeuralNetwork;
    private float lastScore=0;

    public ScoreEvaluatorAgent() {

        Action= new boolean[Environment.numberOfButtons];
        NeuralNetwork= createNeuralNetwork();
    }

    private MultiLayerNetwork createNeuralNetwork(){

        int nChannels = 1;
        int outputNum = 64;
        int batchSize = 64;
        int nEpochs = 10;
        int iterations = 1;
        int seed = 123;//Random number?

        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .regularization(true).l2(0.0005)
                .learningRate(0.01)//.biasLearningRate(0.02)
                //.learningRateDecayPolicy(LearningRatePolicy.Inverse).lrPolicyDecayRate(0.001).lrPolicyPower(0.75)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.NESTEROVS).momentum(0.9)
                .list()
                .layer(0, new ConvolutionLayer.Builder(5, 5)
                        //nIn and nOut specify depth. nIn here is the nChannels and nOut is the number of filters to be applied
                        .nIn(nChannels)
                        .stride(1, 1)
                        .nOut(20)
                        .activation("identity")
                        .build())
                .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2,2)
                        .stride(2,2)
                        .build())
                .layer(2, new ConvolutionLayer.Builder(5, 5)
                        //Note that nIn need not be specified in later layers
                        .stride(1, 1)
                        .nOut(50)
                        .activation("identity")
                        .build())
                .layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2,2)
                        .stride(2,2)
                        .build())
                .layer(4, new DenseLayer.Builder().activation("relu")
                        .nOut(500).build())
                .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(outputNum)
                        .activation("softmax")
                        .build())
                .backprop(true).pretrain(false);

        MultiLayerConfiguration Configuration = builder.build();
        MultiLayerNetwork Model = new MultiLayerNetwork(Configuration);
        Model.init();
        return Model;
    }

    //Total score on which NeuralNetwork configuration will tuned
    private float getTotalScore(Environment Observation) {

        //Change the values in order to give more importance
        //to certain actions
        int killScore= Observation.getKillsTotal()*50;
        int marioModeScore= Observation.getMarioMode()*200;
        int coinScore= Mario.coins*50;
        int flowerScore= Mario.gainedFlowers*150;
        int mushroomScore= Mario.gainedMushrooms*100;
        float marioProgress = Observation.getMarioFloatPos()[0];
        return (float) (marioProgress  +
                        killScore      +
                        coinScore      +
                        flowerScore    +
                        marioModeScore +
                        mushroomScore);
    }

    private float getDeltaScore(Environment Observation){
        float totalScore=getTotalScore(Observation);
        float deltaScore=totalScore-lastScore;
        lastScore=totalScore;
        return deltaScore;
    }

    public void reset() {

    }

    //Gets environment in float[][] form
    private float[][] getFloatCompleteObservation(Environment Observation)
    {
        byte[][] Env= Observation.getCompleteObservation();
        float[][] FloatEnv= new float[Env.length][Env.length];

        for(int i=0;i<Env.length;i++)
            for(int j=0;j<Env.length;j++)
                FloatEnv[i][j]=Env[i][j];

        return FloatEnv;
    }


    public boolean[] getAction(Environment Observation) {

        INDArray Environment= new NDArray(getFloatCompleteObservation(Observation));
        INDArray Out=NeuralNetwork.output(Environment);

        //Evaluation Eval = new Evaluation();

        //NeuralNetwork.fit(Environment);
        /*for(int i=0;i<Env.length;i++) {
            for (int j = 0; j < Env.length; j++) {
                System.out.print("[");
                System.out.printf("%3d",(int)Env[i][j]);
                System.out.print("]");
            }
            System.out.println("");
        }
        System.out.println("_____________________________________");
        */

        Action[Mario.KEY_RIGHT] = true;
        Action[Mario.KEY_JUMP] = true;
        System.out.println(Arrays.toString(Observation.getMarioFloatPos()));
        return Action;
    }

    public AGENT_TYPE getType() {
        return AGENT_TYPE.AI;
    }

    public String getName() {
        return "ScoreEvaluatorAgent";
    }

    public void setName(String name) {

    }
}
