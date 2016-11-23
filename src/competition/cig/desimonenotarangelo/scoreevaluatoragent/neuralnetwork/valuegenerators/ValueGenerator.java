package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators;

import java.io.Serializable;

public interface ValueGenerator extends Serializable {
    public static enum Type {RANDOM, ZERO, XAVIER};
    public abstract double getValue();
}
