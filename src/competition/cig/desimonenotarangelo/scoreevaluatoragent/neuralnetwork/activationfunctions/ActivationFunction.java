package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions;

import java.io.Serializable;

public interface ActivationFunction extends Serializable {
    public abstract double getFunction(double x);
    public abstract double getDerivative(double x);
}
