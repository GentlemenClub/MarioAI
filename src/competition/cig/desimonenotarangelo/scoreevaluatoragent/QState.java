package competition.cig.desimonenotarangelo.scoreevaluatoragent;

public class QState {
    private final double[][] observation;
    private final double[] action;
    private final double marioMode;

    public QState(double[][] observation, double marioMode, double[] action) {
        this.observation = observation;
        this.marioMode = marioMode;
        this.action = action;
    }

    public double[][] getObservation() {
        return observation;
    }

    public double getMarioMode() {
        return marioMode;
    }

    public double[] getAction() {
        return action;
    }
}
