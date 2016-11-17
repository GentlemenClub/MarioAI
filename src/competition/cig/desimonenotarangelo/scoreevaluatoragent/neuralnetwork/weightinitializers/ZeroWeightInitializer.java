package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.weightinitializers;

public class ZeroWeightInitializer implements WeightInitializer {
    @Override
    public double getWeight() {
        return 0.0;
    }
}
