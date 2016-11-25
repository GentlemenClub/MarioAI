package competition.cig.desimonenotarangelo.scoreevaluatoragent;

public class QState
{
  private final double[][] observation;
  private final double[] lastAction;
  private final double marioMode;
  
  public QState(double[][] observation, double[] lastAction, double marioMode)
  {
    this.observation = observation;
    this.lastAction = lastAction;
    this.marioMode = marioMode;
  }
  
  public double[][] getObservation()
  {
    return observation;
  }
  public double getMarioMode() { return marioMode; }
  public double[] getLastAction() { return lastAction; }
}
