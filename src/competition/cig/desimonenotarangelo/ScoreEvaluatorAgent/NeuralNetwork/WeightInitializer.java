package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.io.Serializable;

public interface WeightInitializer extends Serializable
{
  public abstract double getWeight();
}
