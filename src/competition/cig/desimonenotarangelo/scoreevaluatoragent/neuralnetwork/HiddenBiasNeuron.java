package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork;

import competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.activationfunctions.SigmoidFunction;

public class HiddenBiasNeuron extends HiddenNeuron
{
  public HiddenBiasNeuron()
  {
    super(0, new SigmoidFunction());
  }
}