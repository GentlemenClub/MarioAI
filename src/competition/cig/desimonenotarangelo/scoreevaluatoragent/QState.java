package competition.cig.desimonenotarangelo.scoreevaluatoragent;

public class QState
{
  private final double[][] observation;
  private final double marioMode;
  
  public QState(double[][] observation, double marioState)
  {
    this.observation = observation;
    this.marioMode = marioState;
  }
  
  public double[][] getObservation()
  {
    return observation;
  }
  public double getMarioMode()
  {
    return marioMode;
  }
  
}
