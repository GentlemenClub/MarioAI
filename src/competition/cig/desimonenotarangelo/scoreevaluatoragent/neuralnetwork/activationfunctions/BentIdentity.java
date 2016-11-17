package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions;

public class BentIdentity implements ActivationFunction{
    @Override
    public double getFunction(double x) {
        return (Math.sqrt(x * x + 1) - 1) * 0.5 + x;
    }

    @Override
    public double getDerivative(double x) {
        return ((x / (2 * Math.sqrt(x * x + 1))) + 1);
    }
}