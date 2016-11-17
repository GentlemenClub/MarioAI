package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent.NeuralNetwork;

public class ReLU implements ActivationFunction
{

  @Override
  public double getFunction(double x)
  {
    if(x<0)
      return 0;
    else
      return x;
  }
  
  @Override
  public double getDerivative(double x)
  {
    if(x<0)
      return 0;
    else
      return 1;
  }
}
