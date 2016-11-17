package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions;

public class SigmoidFunction implements ActivationFunction{
    @Override
    public double getFunction(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    @Override
    public double getDerivative(double x) {
        return x * (1 - x);
    }
}
