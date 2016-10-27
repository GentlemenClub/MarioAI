package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import ch.idsia.mario.engine.sprites.Mario;

public class QState
{
  private double[][] observation;
  private double levelPosition;
  private double marioMode;
  
  public QState(double[][] observation, double levelPosition, double marioMode)
  {
    this.observation = observation;
    this.levelPosition = levelPosition;
    this.marioMode = marioMode;
  }
  
  public double[][] getObservation()
  {
    return observation;
  }
  public double getLevelPosition()
  {
    return levelPosition;
  }
  public double getMarioMode()
  {
    return marioMode;
  }
}
