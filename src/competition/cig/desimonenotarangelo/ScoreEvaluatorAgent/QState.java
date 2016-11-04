package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

import ch.idsia.mario.engine.sprites.Mario;

import java.util.Arrays;

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
