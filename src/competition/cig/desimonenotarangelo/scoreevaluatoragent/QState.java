package competition.cig.desimonenotarangelo.scoreevaluatoragent;

public class QState
{
  private double[][] observation;
  
  public QState(double[][] observation)
  {
    this.observation = observation;
  }
  
  public double[][] getObservation()
  {
    return observation;
  }
}
