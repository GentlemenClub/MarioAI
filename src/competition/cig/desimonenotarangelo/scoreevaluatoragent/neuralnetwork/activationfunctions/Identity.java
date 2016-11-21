package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions;

public class Identity implements ActivationFunction{
  @Override
  public double getFunction(double x) {
    return x;
  }
  
  @Override
  public double getDerivative(double x) {
    return 1;
  }
}