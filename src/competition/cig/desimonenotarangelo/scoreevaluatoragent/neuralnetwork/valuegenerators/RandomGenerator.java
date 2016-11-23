package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators;


public class RandomGenerator implements ValueGenerator
{

    @Override
    public double getValue() {
        return Math.random();
    }
}
