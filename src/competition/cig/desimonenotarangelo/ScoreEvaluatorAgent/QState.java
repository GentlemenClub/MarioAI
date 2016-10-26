package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import ch.idsia.mario.engine.sprites.Mario;

public class QState
{
  private byte[][] observation;
  private double levelPosition;
  private int marioMode;
  
  public QState(byte[][] observation, double levelPosition, int marioMode)
  {
    this.observation = observation;
    this.levelPosition = levelPosition;
    this.marioMode = marioMode;
  }
  
  public byte[][] getObservation()
  {
    return observation;
  }
  public double getLevelPosition()
  {
    return levelPosition;
  }
  public int getMarioMode()
  {
    return marioMode;
  }
}
