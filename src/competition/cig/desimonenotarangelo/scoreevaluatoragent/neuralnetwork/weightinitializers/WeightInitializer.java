package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.weightinitializers;

import java.io.Serializable;

public interface WeightInitializer extends Serializable {
    public static enum Type {RANDOM, ZERO, XAVIER};
    public abstract double getWeight();
}
