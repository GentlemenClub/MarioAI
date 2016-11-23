package competition.cig.desimonenotarangelo.scoreevaluatoragent.neuralnetwork.valuegenerators;

import java.util.Random;

/*
* Based on: http://andyljones.tumblr.com/post/110998971763/an-explanation-of-xavier-initialization
* https://prateekvjoshi.com/2016/03/29/understanding-xavier-initialization-in-deep-neural-networks/
 */
public class XavierGenerator implements ValueGenerator
{
    private final double mean, variance;
    private Random rand;

    //Variation of original formula by He, Rang, Zhen and Sun: https://arxiv.org/abs/1502.01852
    public XavierGenerator(int inputNeurons) {
        //a rectifying linear unit is zero for half of its input, so you need to double
        // the size of weight variance to keep the signal’s variance constant.
        this(inputNeurons, 0);
    }

    //Glorot & Bengio’s paper originally recommended formula with mean 0
    public XavierGenerator(int inputNeurons, int outputNeurons) {
        this.mean = 0.0;
        this.variance = 2.0/(inputNeurons + outputNeurons);
        rand = new Random();
    }

    @Override
    public double getValue() {
        double ret =mean + rand.nextGaussian() * variance;
        return ret;
    }
}
