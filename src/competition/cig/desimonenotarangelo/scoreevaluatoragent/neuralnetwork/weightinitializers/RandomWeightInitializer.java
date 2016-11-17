package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.weightinitializers;


public class RandomWeightInitializer implements WeightInitializer {

    @Override
    public double getWeight() {
        return Math.random();
    }
}
