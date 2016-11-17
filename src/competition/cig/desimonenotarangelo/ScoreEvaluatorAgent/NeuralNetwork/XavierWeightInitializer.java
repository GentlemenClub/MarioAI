package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

import java.util.Random;

public class XavierWeightInitializer implements WeightInitializer
{
  private final double mean, variance;
  Random rand;
  
  public XavierWeightInitializer(double mean, double variance)
  {
    this.mean = mean;
    this.variance = variance;
    rand = new Random();
  }
  
  @Override
  public double getWeight()
  {
    return mean + rand.nextGaussian() * variance;
  }
}
