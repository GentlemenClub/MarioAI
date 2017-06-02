MarIA

Deep Learning Agent for playing Super Mario Bros

[*https://github.com/GentlemenClub/MarioAI*](https://github.com/GentlemenClub/MarioAI)

Lorenzo De Simone

Department of Computer Science

Università degli Studi di Milano

Via Comelico, 39 – 20135 Milan, Italy

[*lorenzo.desimone@studenti.unimi.it*](mailto:lorenzo.desimone@studenti.unimi.it)

Antonio Notarangelo

Department of Computer Science

Università degli Studi di Milano

Via Comelico, 39 – 20135 Milan, Italy

[*antonio.notarangelo@studenti.unimi.it*](mailto:antonio.notarangelo@studenti.unimi.it)

*Abstract*—This project investigates the application of DeepMind’s Deep
Reinforcement Learning model to create an intelligent agent capable of
successfully playing a random generated level of Super Mario Bros. Our
custom model is a fully connected neural network to which a
reinforcement learning module has been applied, whose input is a
partially observable dynamic environment provided by Mario AI framework
and whose output is the action chosen by the reinforcement learning
policy that will be performed by the agent. Our goal is to build a model
that could be theoretically used for any game when a discrete game state
and a fitness function is definable.

Keywords—Super Mario Bros; Artificial Neural Networks; Backpropagation;
Reinforcement Learning; Machine Learning; SARSA; Q-Learning; Deep Mind;
Deep Learning; Java

 Introduction
=============

Videogame industry has drawn the attention of many researchers that
weren’t previously interested in this specific topic before.

Artificial intelligence plays a key role in order to create a more
immersive experience for players; as a matter of fact, virtual
characters are required to have a believable and coherent behaviour and
sophisticated algorithms are needed to obtain this goal. Nowadays
videogame players are seeking a real challenge against intelligent
computer-controller enemies; these enemies should be aware of their
surroundings in order to use their skills efficiently.

Reinforcement learning has been used to create agents that are able to
refine their behaviour and learn from their mistakes but has shown its
limitation in contexts with an exponential number of states. Those
algorithms rely on a discrete state-action representation of a
reasonable cardinality, which is not always true.

In 2014 Google DeepMind created agents able to play classic Atari games
using convolutional neural networks combined with reinforcement learning
algorithms in order to overcome the aforementioned limitations.

Given the intrinsic simplicity of their rules, a static environment and
few external factors, those games proved a perfect case study to test
the interaction between neural networks and reinforcement learning in
videogames. This machine learning technique is Deep Reinforcement
Learning.

Our project tries to apply deep reinforcement learning in a more complex
environment.

Our goal is to create an agent that is able to play a Super Mario Bros
clone feeding it only the following information:

-   The state of the game world at every time *t*

-   The main and secondary goals to accomplish using an appropriate
    > rewards and fitness function

Our model can be therefore applied to other games if the system can
provide all the information about game world at every time *t*, and
rewards are given after the agent performs an action.

One big problem was the absence of any kind of training set that could
have been given our agent, as often done by neural network classifiers.

These problems have been partially solved training to neural network
on-line, using data collected by the reinforcement learning module that,
in our case, implements a tweaked version of SARSA.

This reinforcement learning module acquires the input at every time *t*,
chooses an action following current policy and at time *t+1*, after
receiving a reward, modifies neural network’s weights with a
backpropagation algorithm.

For this project, we have chosen a particular Super Mario Bros clone
named Infinite Mario.

Infinite Mario is a Java framework that easily offered the API to create
intelligent agents and test their performance in randomly generated
levels. The framework offers all the environment information we needed
in an easy readable format.

Description of the Game and the Framework
=========================================

Super Mario Bros Rules
----------------------

We wanted to test our model on a game that is undoubtedly more complex
than Atari games: Super Mario Bros.

In this game, you play as a plumber named Mario as he attempts to reach
the goal at the end of each level while traversing it from left to
right. You can make Mario move left and right, run, jump, and shoot
fireballs and, during his path, he must navigate various platforms while
avoiding various dangers such as enemies and gaps on the ground.

There are two items, called power-ups, that Mario can get to help him
reach the goal. The first is a power mushroom that makes Mario grow,
allowing him to take an extra hit from an enemy before dying. The second
power-up is the fire flower, which grants Mario the ability to launch
fire balls, which kill enemies instantly, and the powers of the power
mushroom as well.

Mario will face a wide range of obstacles during the game: from basic
obstacles, such as a gap that must be jumped over, to more complex
obstacles, such as dead ends that imply significant backtracking.

There are secondary goals that Mario can accomplish, like collecting
coins and killing enemies.

These activities aren’t fundamental but are needed to increment game
score and often done by the majority of human players, whose behaviour
we want to emulate.

The mixture of complexity and simplicity in Super Mario Bros. makes it
perfect for machine learning: an AI agent would not be able to complete
the game if it’s not properly trained, since it must possess the precise
movements to perform the right actions at the right time, so it’s
abundantly complicated; on the other hand, Super Mario Bros. is simple
because the levels do not necessarily need to be explored, so the player
can always go to the right to reach the end, and also because an action
can be carried out by a combination of up to five keys.

Infinite Mario Bros: an AI benchmark framework
----------------------------------------------

In 2008, a public domain clone of Super Mario Bros, named Infinite Mario
Bros, was published by Markus Persson, Minecraft’s father. This clone
features the art assets and general game mechanics of Super Mario Bros
but differs in level construction. While implementing most features of
Super Mario Bros, the standout feature of Infinite Mario Bros is the
automatic generation of levels according to certain heuristics.

In 2009, a researchers’ group decided to transform the original Markus
Persson’s project into a piece of benchmarking software that can be
interfaced with reinforcement learning algorithms. This included
removing the real-time element of the game so that it can be “stepped”
forward by the learning algorithm, removing the dependency on graphical
output, and substantial refactoring. The resulting software is a single
threaded Java application that can easily be run on any major hardware
architecture and operating system, with the key methods that a
controller needs to implement specified in a single Java interface file.

### How it works

At each time step, which corresponds to 40 milliseconds of simulated
time (an update frequency of 25 fps), the Super Mario-based agent

1.  receives a description of the environment;

2.  selects an action;

3.  performs it;

4.  once the action is done, the framework updates the state of the
    world.

The cycle repeats over again until one of these three terminal states
are reached:

-   Mario died

-   Level cleared

-   Time is over

Let’s get further into details and let’s analyse every cycle step.

1.  At each time-step the agent receives an observation of the current
    > game’s state which is represented as a 22x22 grid containing a
    > numerical representation of the graphical interface. Each
    > container in the grid represents a block on the screen and Mario
    > is always placed at centre of the grid, in the coordinate of
    > \[11,11\]. Each block may contain one of these elements, each with
    > a different byte value that distinguishes it:

-   Question box

-   Coin

-   Cannon tube

-   Flower tube

-   Free space

-   Brick

-   Terrain

-   Enemy (Goomba, bullet, etc.)

> The framework also provides additional information not directly
> related to the grid, such as:

-   How many enemies were killed

-   Mario’s position in the level

-   Mario’s mode (small, big, fire)

-   Mario’s status (dead, alive, winning)

-   How many coins were collected

1.  The agent must then process this observation of the world and choose
    > an action. In this framework, an action is defined as a boolean
    > array which length is 5, that is the number of keys that can be
    > pressed. Every key is represented by the boolean on off state and
    > the list of keys, which respectively occupy the positions of the
    > array, is the following:

-   Left

-   Right

-   Down

-   Jump

-   Speed

> The possible key combinations are 2^5^.

1.  Once the action has been chosen based on the current state, we
    > return the corresponding Boolean array to the framework that will
    > be tasked to perform it right away.

2.  The framework modifies the game state according to the action
    > previously performed and provides the updated observation to the
    > agent only if one of the terminal states has not been reached. If
    > the game ends, the benchmark system returns a score based on
    > agent’s performance.

Used Machine Learning Techniques
================================

Artificial Neural Network
-------------------------

### What is a ANN

An Artificial Neural Network (ANN) is an information processing paradigm
based on how the information is processed in biological nervous systems,
such as the brain.

However, this model might seem too simplified because we know far too
little about biological systems, especially at lower levels.

The brand-new structure of this information processing system is
composed of a large number of highly interconnected processing units
named neurons working in unison to solve specific problems. Learning in
biological systems, and indeed in ANNs, involves adjustments to the
synaptic connections between the neurons.

Neural networks can be used to extract patterns and detect trends that
are too complex to be noticed by either humans or other computer
techniques, through their ability to extract meaningful information from
seemingly inaccurate or complicated data. A neural network, once
trained, can be considered as an expert in the field which it has been
qualified for.

Artificial neural networks can be most sufficiently characterised as
'computational models' with specific properties, such as the ability to
adapt or learn, to self-organize, to generalise, or to cluster or
classify data.

Each operation is based on parallel processing since numerous units
simultaneously perform their computations.

There are many non-neural computational models that share the same
properties, nevertheless we cannot say with absolute certainty that
these are better than the neural networks.

#### Architecture

The neural network model is composed of several components, each with
specific roles:

-   A set of processing units called neurons

-   An output *y~k~* for every unit (state of activation)

-   Connections between units represented by weights. Each weight
    > *w~jk~* determines the effect which the signal of unit *j* has on
    > unit *k*

-   A propagation rule that determines the effective input *s~k~* of a
    > unit from its external inputs

-   An activation function *F~k~*, which determines the new level of
    > activation based on the effective input *s~k~(t)* and the current
    > activation and the current activation *y~k~(t)*

-   An external input (bias or offset) *θ*~k~ for each unit

-   A learning rule that determines a method to collect information

-   An environment which provides input signals to the network and error
    > signals

#### Neuron

Each neuron performs two basic tasks:

-   to use the input received from neighbours or external sources to
    > compute and propagate to other units an output signal

-   the tuning of the weights

![](media/image1.wmf){width="2.682638888888889in" height="1.53125in"}

We can distinguish essentially three types of units within the neural
networks:

-   *input* units (indicated by an index $i$) which receive data from
    > outside the neural network

-   *hidden* units (indicated by an index $h$) whose input and output
    > signals remain within the neural network.

-   *output* units (indicated by an index $o$) which send data out of
    > the neural network

In most cases, we assume that each unit provides an additive
contribution to the input of the unit with which it is connected. The
effect that each input has at decision making is dependent on the weight
of the input. The total input to unit $k$ is simply the weighted sum of
the separate outputs from each of the connected units plus a *bias*
(*offset* or *threshold*) term $\theta_{k}$:

$$s_{k}\left( t \right) = \ \sum_{j}^{}{w_{\text{jk}}\left( t \right)y_{j}\left( t \right) + \theta_{k}(t)}$$

The weighted input is a value obtained by multiplying the weight and
input. If the weight value is positive, then it will be interpreted as
an excitation signal, otherwise as an inhibitory signal.

These weighted inputs are then added together and if they exceed a
pre-set threshold value, the neuron fires.

The threshold value (bias) generally refers to a constant term, which is
input to a unit and which is adapted by the learning rule.

In mathematical terms, the neuron fires when the addition of the
weighted inputs and the threshold makes it very flexible and powerful
one. In any other case the neuron does not fire.

#### Activation and output rules

We also need a rule which gives the effect of the total input on the
activation of the unit. We need a function $F_{k}$ which takes the total
input $s_{k}(t)$ and produces a new value of the activation of the unit
$k$:

$$y_{k}\left( t + 1 \right) = \ F_{k}\left( s_{k}\left( t \right) \right) =$$

$$= \ F_{k}\left( \sum_{j}^{}{{w_{\text{jk}}\left( t \right)\text{\ y}}_{j}\left( t \right) + \ \theta_{k}\left( t \right)} \right)$$

In biologically inspired neural networks, the activation function
$F_{k}$ (also known as transfer function) is usually an abstraction
representing the rate of action potential firing in the cell. In its
simplest form, this function is binary that is, either the neuron is
firing or not. A line of positive slope may also be used to reflect the
increase in firing rate that occurs as input current increases.

This function typically falls into one of three categories:

-   Linear (or semi-linear): the output activity is proportional to the
    > total weighted output.

> ![](media/image2.wmf){width="1.4583333333333333in"
> height="1.2395833333333333in"}

-   Sgn (limiting threshold): the output is set at one of two levels,
    > depending on whether the total input is greater than or less than
    > some threshold value.

> ![](media/image2.wmf){width="1.3541666666666667in" height="1.1875in"}

-   Sigmoid (smoothly limiting threshold): the output varies
    > continuously but not linearly as the input changes.

> ![](media/image2.wmf){width="1.34375in" height="1.2708333333333333in"}

Sigmoid units bear a greater resemblance to real neurons than do linear
or threshold units, but all three must be considered rough
approximations.

#### Network topologies

In the previous section, we discussed about the various properties owned
by the fundamental unit neuron in an artificial neural network. Now we
will describe the various network topologies, according to which rules
the neurons are connected to each other and how they propagate data.

As for this pattern of connections, the main distinction we can make is
between:

-   Feed-forward networks: extensively used in pattern recognition, the
    > signals can travel one way only, from input to output, so this
    > kind of network tends to be straight forward as inclined to
    > associate inputs with outputs. There are no feedback loops i.e.
    > the output of any layer does not affect that same layer.

> ![](media/image3.wmf){width="2.1145833333333335in"
> height="1.9479166666666667in"}

-   Feedback networks: powerful and extremely complicated, the signals
    > can travel in both directions by introducing loops in the network.
    > The state of these networks tends to change continuously until
    > they reach an equilibrium point, consequently they are very
    > dynamic. These networks remain at the equilibrium point until the
    > input changes and a new equilibrium needs to be found.

> ![](media/image3.wmf){width="2.1145833333333335in"
> height="1.9638888888888888in"}

#### Learning approach

All learning methods used for adaptive neural networks can be classified
into two major categories:

-   Supervised learning incorporates an external entity that matches
    > every input with a specific output. During the learning process,
    > the network must adjust its own parameters to emulate the external
    > teacher in a statistically optimal way. Paradigms of supervised
    > learning include error-correction learning, reinforcement learning
    > and stochastic learning.

-   Unsupervised learning uses no external supervisor, consequently the
    > networks autonomously learns. It is also referred to as
    > self-organization, so the system must develop its own way to
    > represent data and find patterns within them to detect their
    > properties. Paradigms of unsupervised learning are Hebbian
    > learning and competitive learning.

### Forward Propagation

Now we are going to show in detail how does a neural network compute
input in order to produce the output analysing one commonly known neural
network structure, the perceptron.

The perceptron is the simplest form of neural network used to classify
linearly separable patterns, i.e. patterns which are at the opposite
sides of a hyperplane. It consists of two inputs and a single output
neuron with synaptic weights and modifiable threshold.

![](media/image4.wmf){width="2.203472222222222in"
height="1.9583333333333333in"}

The input of the neuron is the weighted sum of the inputs plus the bias
term. The output of the network is formed by the activation of the
output neuron, which is some function of the input:

$$y = F\ \left( \sum_{i = 1}^{2}w_{i}x_{\text{i\ }} + \ \theta \right)$$

The activation function $F$ can be linear so that we have a linear or
nonlinear network. In this section, we consider the threshold (or
Heaviside or sgn) function:

$$F(s) = \left\{ \begin{matrix}
1\ if\ s > 0 \\
\  - 1\ otherwise \\
\end{matrix} \right.\ $$

The network can now be used for a classification task: if the total
input is positive, the pattern will be assigned to class +1, if the
total input is negative, the sample will be assigned to class -1.

Rosenblatt proved that if the patterns chosen to train the network
belong to two separate classes linearly, then the learning algorithm
converges and the decision space is divided into 2 by a hyperplane.

The separation between the two classes in this case is a straight line,
given by the equation:

$$w_{1}x_{1} + \ w_{2}x_{2} + \theta = 0$$

In this geometrical representation of the linear threshold neural
network, we see that the weights determine the slope of the line and the
bias determines the 'offset', i.e. how far the line is from the origin.

![](media/image5.wmf){width="2.46875in" height="2.1145833333333335in"}

Note that also the weights can be plotted in the input space: the weight
vector is always perpendicular to the discriminant function.

#### Forward Propagation in general

In neural networks with several layers, the same procedure must be
applied recursively for each layer. It is possible to choose different
activation functions for each layer in order to obtain particular
results. In the example shown above, the activation function is used to
classify the input; a more refined approach might consider converting
the output in a probability function that expresses with continuous
values how likely the input was a certain pattern rather than expressing
it with a boolean value.

### Backward Propagation

We have shown how a neural network can produce an output given a
particular input. Now we can proceed and analyse how a neural network
can modify its internal weights and biases in order to actually learn
from its mistakes using the perceptron as an example.

Suppose we have a set of learning samples consisting of an input vector
$\overrightarrow{x}$ and a desired output $d(x),$ which value could be
$+ 1$ or $- 1$ for a classification task.

The following is the proposed learning algorithm known as the
convergence algorithm of the perceptron.

I.  Initialization: set every weight of the network to $0$ or random
    > value and *t* to *0*

II. Activation: at time *t*, the perceptron is activated by applying an
    > input vector $\overrightarrow{x}\mathbf{\ }$from the set of
    > training samples and the desired response *d(x)*

III. Output calculation: the output *y* is calculated using the sign
    > function mentioned previously

IV. Weights update: if the perceptron gives an incorrect response
    > ($y\  \neq \ d(x)$), an adjustment to all connections $w_{i}$ is
    > needed at time $t\  + \ 1$ using the following formula.

$${w_{i}\left( t + 1 \right) = \ w_{i}\left( t \right) + w_{i}\left( t \right)\backslash n}{w_{i}\left( t \right) = - \eta d(x)x_{i}}$$

I.  Bias update: besides modifying the weights, we must also modify the
    > threshold *θ* at time *t + 1*

$${\theta\left( t + 1 \right) = \ \theta\left( t \right) + \ \theta\left( t \right)\backslash n}{\Delta\theta(t) = \left\{ \begin{matrix}
0\ \ if\ \& y = d(x) \\
 - \eta d\left( x \right)\ \ if\ \& otherwise \\
\end{matrix} \right.\ }$$

I.  Continue: increase *t* and return to step 2

#### Backward Propagation in general

In deep neural networks, backpropagation needs to go through several
layers and follows slightly different rules to update output and hidden
layer weights.

As a matter of fact, for each output node, we compute some $\delta$
values that will be used later:

$$\delta_{k}\  = \ O_{k}^{'} \bullet (O_{k} - \ t_{k}\ )$$

Where $O_{k}$ is the output of the neuron in the output layer $k$ and
$t_{k}$ is the target value for the output neuron.

For each hidden node, we compute:

$$\delta_{j}\  = \ O_{j}^{'} \bullet \sum_{n \in N}^{}{\delta_{n}w_{\text{jn}}}$$

Where $O_{j}$ is the output of the neuron in the hidden layer $j$, $n$
is the layer right after $j$, $\delta_{n}$ is the $\delta$ of node in
the layer $n$ and $w_{\text{jn}}$ is the weight of the link between the
node and one of its next.

This means that the weight adjustment for output neuron is directly
influenced by the error in the output which is represented by the
difference $(O_{k} - \ t_{k}\ )$, while the same adjustment for each
hidden node is influenced recursively by all the following hidden
neuron. As we will see in the following chapter, this recursion can have
some quite problematic consequences.

This $\delta$ value can be used to calculate how weights and biases must
be updated.

The weight of the link between a node in layer $l - 1$ and
$\text{l\ }$and the bias of the node in layer $l$ are calculated as
follows:

$$w = - \eta\delta_{l}O_{l - 1}$$

$$\theta = - \eta\delta_{l}$$

Where $\eta$ is the neural network learning rate. We can update all
links and neurons in the network:

$$w \longleftarrow w + w$$

$$\theta \longleftarrow \theta + \theta$$

### Unstable Gradient Problem

The idea that adding more layers to produce a more precise output isn’t
true. As a matter of fact, increasing the number of layers in a neural
network, the accuracy of the network might even decrease. The reason can
be identified with a closer look to how the backpropagation algorithm
works. As a matter of fact, each layer might learn with a very different
speed. The ultimate reason for this apparently strange behaviour is
caused by the application of the gradient descent in neural networks;
shallow layers are basically a multiplication of deeper layers’
gradient.

The problem can easily be shown with a practical example, a simple
“linear” neural network.

Given $y = f\left( x \right)$ the function we want this neural network
to approximate, where $x$ is the input and $y$ is the output, $w_{l}$
the weight of the link at layer $l$ and $\sigma$ is the activation
function we have:

$f\left( x \right) = \ \sigma\left( w_{3} \bullet \sigma(w_{2} \bullet \sigma(w_{1} \bullet x)) \right)$

This is, as expected, how the output is calculated with a forward
propagation.

The partial derivative of the error in the first layer is composed by
many terms:

$$\frac{\partial f}{\partial w_{1}} = \sigma'(h_{2}w_{3}) \bullet w_{3} \bullet \sigma'(h_{1}w_{2}) \bullet w_{2} \bullet \sigma'(xw_{1})x$$

With:

$$h_{2} = \ \sigma(w_{2}\sigma \bullet \left( w_{1} \bullet x \right))$$

$$h_{1} = \ \sigma\left( w_{1} \bullet x \right)$$

This chain of products is the culprit of unstable gradient. When those
terms are very small, the result is a vanishing gradient; this means
that the learning is so small that is barely noticeable and the network
doesn’t actually learn.

When those terms are bigger the product can grow quickly and cause an
overflow known as exploding gradient.

#### Xavier Weight Initialization

Unstable gradient can be solved using some techniques that help to
preserve the variance of input data through the network.

Xavier weight initialization is a technique for initializing neural
network weights in a smart way, preserving the variance and therefore
preventing unstable gradient.

Given $O_{l}$ the output of the neuron at layer $l$ with a linear
activation function and $n$ the number of neuron in the layer $l - 1$
linked to this neuron we have:

$$O_{l} = \ w_{1}x_{1} + w_{2}x_{2} + \ldots + w_{n}x_{\text{n\ }}$$

$$\text{Var}\left( w_{i}x_{i} \right) = {E\left\lbrack x_{i} \right\rbrack}^{2}\text{Var}\left( w_{i} \right) + {E\left\lbrack w_{i} \right\rbrack}^{2}\text{Var}\left( x_{i} \right) + \ Var\left( w_{i} \right)\text{Var}\left( x_{i} \right)$$

We can further simplify this if inputs and weights both have mean 0.

$$\text{Var}\left( w_{1}x_{1} \right) = \ Var\left( w_{i} \right)\text{Var}\left( x_{i} \right)$$

Assuming that $x_{i}$ and $w_{i}$ are independent and identically
distributed:

$$\text{Var}\left( O_{l} \right) = \ Var\left( w_{1}x_{1} + w_{2}x_{2} + \ldots + w_{n}x_{\text{n\ }} \right) = nVar\left( w_{i} \right)\text{Var}\left( x_{i} \right)$$

The variance of the input and the variance of the output should be the
same; in order to obtain that result
$\text{nVar}\left( w_{i} \right) = 1$ must be true. So, we have:

$$\text{Var}\left( w_{i} \right) = \frac{1}{n_{\text{in}}}$$

Doing the same for the backward propagation we have:

$$\text{Var}\left( w_{i} \right) = \frac{1}{n_{\text{out}}}$$

In ideal condition, both should be satisfied and this can be true only
if $n_{\text{in}} = n_{\text{out}}$. This is highly unlikely and so the
following formula is used as an approximation:

$$\text{Var}\left( w_{i} \right) = \frac{2}{n_{\text{in}} + n_{\text{out}}}$$

When Xavier weight initialization is used, each weight in the network is
initialized using a distribution function that has the aforementioned
value ad variance in order to solve the unstable gradient problem.

### Underfitting

Neural networks require architectural experimentation and parameters
tuning in order to work correctly. Sometimes their performance is far
from being acceptable and the cause can be underfitting or overfitting.
If a neural network performs poorly on the training set, it probably
means that the model is unable to generalize new data. This phenomenon
is known as underfitting; it is often caused by an inadequate network
architecture. Some complex problems might require a bigger network, a
better tuned one or simply neural networks might not be the best tool to
solve them. It is a difficult phenomenon to analyse and requires
case-by-case study in order to be solved.

### Overfitting

In General, overfitting refers to a model that learns the training data
incredibly well but is unable to generalize new data. This is caused by
the fact that the model acquired the noise and interpreted it as a
feature to be learnt; these features do not apply to test set date and
they produce a substantial drop in performance.

Models that have several degrees of freedom are mora likely to overfit;
in our case, neural network often overfit when they are bigger than
necessary and they tune too much on specific training data. Instead of
focusing on all methods used to reduce overfit in general, we will
explain a technique used efficiently in order reduce overfitting named
dropout.

#### Dropout

The dropout technique constraints the network in order to limit its
adaptation to the training data by virtually removing some random nodes
form the network in each forward and backward propagation.

In order to explain why this is extremely useful, it can be said that
the dropout is a form of *ensemble learning*. Basically, ensemble
learning consists in creating several smaller classifiers and train them
separately; once they are trained, the final result of the function
approximation can be calculated as an average of the outputs of the
single classifiers. This is useful since different classifiers might
have grasped different aspects of the same problem and by combining them
we might obtain a better result mitigating their mistakes.

Dropout applies this concept with a virtual removal; in practice, each
time a forward propagation is done, a slightly different network is
used. The virtually removed nodes and their link are not updated when
dropped; this has the positive effect of making the nearby neuron less
reliant on their surroundings and more capable of handling data
autonomously.

In the end, this approach produces a combined classifier that it less
prone to overfitting.

Reinforcement Learning
----------------------

### What is Reinforcement Learning

Reinforcement learning is a type of Machine Learning which allow agents
to detect what is the right action to perform within a specific context,
in order to maximize its performance. Initially the agent doesn’t know
what are the right actions to take, so it has to find out which are the
most profitable by trying them through a trial-and-error process.
Whenever the agent performs an action, it receives a numerical reward
feedback from the environment, known as reinforcement signal, useful to
correct its behaviour and improve it.

In the this class of problems, an agent is supposed to decide the best
action according to its current state and when this step is repeated,
the problem is known as a Markov Decision Process. Clearly, such an
agent must be able to sense the state of the environment to some extent
and must be able to take actions that affect the state. The agent also
must have a goal or goals relating to the state of the environment.

Supervised learning is a machine learning technique to train the agent
from examples provided by an external expert but alone it is not
adequate for learning from interaction. In uncharted territories, where
the agent is immersed in an unknown environment, it must be able to
learn from its own experience, so reinforcement learning is the most
practical way to approach interactive problems.

### Architecture

A reinforcement learning system is composed by four sub-elements:

-   A policy is the core of reinforcement learning and decides the way
    > the agents behaves at a certain time. It's a rule that is executed
    > by the agent to decide what action to take, i.e. mapping between
    > state (input from the environment) and actions. In some cases, the
    > policy may be a simple function or lookup table, whereas in others
    > it may involve extensive computation such as a search process. In
    > general, policies can have a stochastic component.

-   A reward function specifies the immediate reward associated with
    > action taken in a certain state. It maps every state-action pair
    > of the environment to a reward value, indicating the goodness of
    > the choice of the action taken in that state. If the action
    > selected by the policy is followed by low reward, then the policy
    > may be changed to select some other action in that situation in
    > the future. The agent’s goal is to maximize the total reward it
    > receives in the long term.

-   A value function, differently from a reward function which indicates
    > what is good in the immediate, specifies what is good in the long
    > term. So, the value of a state is the sum of the rewards
    > associated with the selected actions instant by instant, starting
    > from that state. For example, a certain state may be associated
    > with a low reward immediate, though this choice could lead to
    > obtain higher rewards in the long term. Vice versa, a state may
    > have a very high immediate reward but it could lead the agent to
    > not reach higher rewards in the future.

-   The environment provides the reward function and input on the basis
    > of which the agent updates the status. The agent must build an
    > implicit representation of the environment through the value
    > function. The agent learns by interacting with the environment
    > with a trial-and-error strategy, learns a model of the
    > environment, and use the model for planning. Models are used for
    > planning, by which we mean any way of deciding on a course of
    > action by considering possible future situations before they are
    > actually experienced.

### Strategies

#### State-action value updating strategies

As mentioned above, a reinforcement learning agent receives rewards as
it moves through the environment. It uses these rewards for future
reference; that is, when it reaches a state it already seen, it picks an
action that has given it good rewards in the past. Thus, rewards need to
be stored somehow. Since several actions may be taken from each state,
we store a value for each action from each state: the state-action
value, denoted Q(state, action). This value depends in part on the
reward received, in part on the current value, and can also depend on
other values as well. Here we describe two possible updating strategies,
two ways to determine the Q-Value.

##### SARSA Learning

Sarsa learning is an on-policy updating strategy. The new state-action
value depends on the reward received after taking an action, on the
current value of the state, as well as the value of the next
state-action pair seen.

//s, s’ → states\
//a, a’ → actions\
//Q → state-action value\
//α, γ → learning parameters (learning rate, discount factor)\
Initialize **Q(s, a)** arbitrarily;\
Repeat (for each episode)\
Initialize **s;**\
Choose **a** from **s** using policy derived

from **Q** (e.g. Є-Greedy);\
Repeat (for each step of episode) until **s** is terminal\
Take action **a**, observe reward **r**, state **s’**;\
Choose **a’** from **s’** using policy derived

from **Q** (e.g. Є-Greedy);\
$Q\left( s,\ a \right) \leftarrow Q\left( s,\ a \right) + \ \alpha\begin{bmatrix}
r + \gamma \bullet Q\left( s^{'},\ a^{'} \right) \\
 - Q(s,\ a) \\
\end{bmatrix}$;$Q\left( s,\ a \right) \leftarrow Q\left( s,\ a \right) + \ \alpha\left\lbrack r + \gamma \bullet Q\left( s^{'},\ a^{'} \right) - Q(s,\ a) \right\rbrack$\
**s** ← **s’**, **a** ← **a’;**

##### Q-Learning

Q-learning, unlike Sarsa learning, is an off-policy updating strategy.
Where the new state-action value in Sarsa depends on the value of the
next state-action pair taken, in Q-learning it depends of the optimal
state-action pair of the next state.

//s, s’ → states\
//a, a’ → actions\
//Q → state-action value\
//α, γ → learning parameters (learning rate, discount factor)\
Initialize **Q(s, a)** arbitrarily;\
Repeat (for each episode)\
Initialize **s;**\
Repeat (for each step of episode) until **s** is terminal\
Choose **a** from **s** using policy derived

from **Q** (e.g. Є-Greedy);\
Take action **a**, observe reward **r**, state **s’;\
** $Q\left( s,\ a \right) \leftarrow Q\left( s,\ a \right)$

$+ \ \alpha\begin{bmatrix}
r + \gamma \bullet \max_{a^{'}}Q\left( s^{'},\ a^{'} \right) \\
 - Q\left( s,\ a \right) \\
\end{bmatrix}$;\
**s** ← **s’**;

#### Action selection strategies

It has been mentioned already that in each state (except a terminal
state) the agent must select an action. There are several ways in which
to decide which action to take. However, we need to solve the dilemma of
the choice of action to be taken:

-   Exploration: the need for an agent to explore new possible actions
    > and re-exploring those already tried earlier to get an up to date
    > policy and correct;

-   Exploitation: the need to exploit the known policy, because the best
    > choices actions repeatedly provide a reward. The greedy selection
    > represents a pure exploitation example: the agent always selects
    > the action with the highest state-action value. Nevertheless, if
    > an agent does not explore new solutions it may be outclassed by
    > new, more dynamic agents.

##### Є-Greedy selection

Є-Greedy is a variation on normal greedy selection. In both cases, the
agent identifies the best move according to the state-action values.
However, there is a small probability Є that, rather than take the best
action, the agent will uniformly select an action from the remaining
actions.

Our Approach
============

Model Architecture
------------------

In this chapter, we will describe each module of system architecture
explaining in details their features. After that, it will be explained
how they interact with each other to produce an intelligent behaviour.

### The Neural Network

In this paragraph, we will analyse how the neural network is built and
its structure.

In our deep reinforcement learning approach, this neural network will be
used as a Q-Table; this network can be seen as a Q\* function
approximator.

Given the impossibility to create a table for each state-action couple
as done in classic reinforcement learning, we used a fully connected
neural network with the following architecture to handle world data.

Since most of the environment on which Mario decides his actions on is
basically his surroundings, we decided to use a 11x11 sub-Environment
instead of all the 22x22 matrix provided by the framework.

This choice allowed us to reduce our neural network complexity, to speed
up learning process and to have enough computational room to implement
history, as will be shown.

In our network the input is constructed as follows:

Given current time $t$, For three frames in the past,
$t_{- 1}$,$\ t_{- 2}$ ,$\ t_{- 3}$, we give in input the environment
around Mario in a 11x11 matrix, plus the action that was taken at that
time and the $\text{MarioState}_{t}$, where $\text{Mar}i\text{oState}$
expresses if Mario is small, big or flower.

For $\text{enviromnent}_{t}$ we give in input a 11x11 submatrix for the
environment and $\text{MarioState}_{t}$.

Giving the history as input led our agent to understand patterns
relative to consequent frames: without history of the action previously
taken, the agent wouldn’t understand that, for example, Mario can
actually jump only when the Jump button wasn’t already pressed in the
past frame.

The internal structure is as follows:

-   1 Input layer of 502 neurons, using reLU as activation function.

-   2 Hidden layer respectively of 250 and 125 neurons, using Bent
    > identity as activation function.

-   1 Output layer of 32 neurons, using Bent Identity as activation
    > function.

The input is normalized between 0 and 1, and the output of the neural
network is the Q-Value relative to each action.

Here are our considerations and the reasons that led us to choose
parameters and activation functions:

-   The Bent Identity was used for its properties, since its range
    > varies from \[-ꝏ,+ꝏ\] and, as we will see in the next chapter,
    > fitted the problem perfectly since, given the nature of our
    > environment, the reward function can vary indefinitely.

-   The dropout percentage is set to 0.5; the value is commonly used in
    > neural network and experimentally helped us to avoid overfitting.

-   Xavier weight initialization was used and proved useful to solve an
    > exploding gradient problem we often encountered before
    > implementing it.

### Reward Definition

Our machine learning approach needs a reward function to update Q-Value
in the neural networks accordingly using a backpropagation algorithm. At
each time $t$, the agent score is calculated as follows:

$${Score = Level\ Position\backslash n}{+ Mario\ Status\backslash n}{+ \ Mario\ Mode\backslash n}{+ Enemies\ Killed\backslash n}{+ Coins\ Collected}$$

Where:

-   $\text{Level\ Position}$ is the X coordinate of Mario relative to
    > the entire level, not just the current screen.

-   $\text{Mario\ Status}$ is -300 if Mario is dead in the current frame
    > (and there is still time left) and 0 otherwise.

-   $\text{Mario\ Mode}$ is 300 is Mario is in flower status, 200 if he
    > is big and 0 if he is small.

-   $\text{Enemies\ Killed}$ is the number of enemies killed multiplied
    > by 100.

-   $\text{Coins\ Collected}$ is how many coins Mario managed to
    > retrieve.

At each time $t$, the agent calculates a delta value as reward to use
for the reinforcement learning module:

$$\text{reward}_{t} = \text{score}_{t} - \text{score}_{t - 1}$$

### Reinforcement Learning Module

Starting from the classic SARSA formula:

$$Q\left( s_{t},a_{t} \right) \longleftarrow \ Q\left( s_{t},a_{t} \right) + \alpha\lbrack r_{t + 1} + \gamma Q\left( s_{t + 1},a_{t + 1} \right) - \ Q\left( s_{t},a_{t} \right)\rbrack$$

Here are our considerations and the reasons that led us to our final
formula.

-   Normally in any reinforcement learning algorithm α defines how
    > easily information will be overwritten by new data collected. The
    > learning rate η$\eta$ of our neural network now plays the role of
    > α, so we can remove it from the equation. The learning rate η is
    > set to a very small value that was decided through several
    > experiments: a bigger value made our network fluctuate heavily and
    > eventually resulted in an exploding gradient. A smaller value,
    > instead, made the converging process too slow and, with very small
    > values, produced a vanishing gradient problem. The final value we
    > settled down was 2 ∙ 10^-5^$2\  \bullet 10^{- 5}$.

-   It is not possible to directly update the value
    > $Q\left( s_{t},a_{t} \right)$Q(s~t~, a~t~) since it is a value
    > calculated by a forward pass in the neural network rather than a
    > value memorized in a table. In our architecture, we use the value
    > resulting from the right part of the expression in a
    > backpropagation algorithms in order to modify network’s weights to
    > reduce the error.

-   We apply an ε-greedy strategy with a coefficient of 0.2 in order to
    > decide the value of $Q\left( s_{t},a_{t} \right)$Q(s~t+1~,
    > a~t+1~)$Q\left( s_{t + 1},a_{t + 1} \right)$. This value helped
    > the agent’s decision making algorithms because it forces it to
    > keep account of the possibility that in the next time window the
    > agent might perform a random action.

-   Since our videogame is a perfectly deterministic world, γ should
    > theoretically be 1 in a standard reinforcement learning
    > algorithms. Given the presence of a neural network though, any
    > value very near to 1 would make it diverge. That is the reason why
    > we opted for a value of 0.9.

Decision Making and Policy Update
=================================

Now that we explained every component of our learning architecture, we
are able to describe in detail how they interact with each other in
order to make decisions and update the policy.

Initialization
--------------

The agent searches in the local directory there is a neural network file
to load, otherwise it initializes it using the Xavier algorithm.

The neural network module is initialized using the starting environment
of the level as story and the default action \[0,0,0,0,0\] which means
no button pressed: this basically let the agent start with a neutral
story.

This initialized environment is given as input to the neural network and
for each combination of buttons a Q-Value is calculated using a forward
propagation.

Using an ε-greedy strategy the agent chooses the action with the highest
Q-Value or a random one.

### Action decision

As stated before, the agent wants to know which action is best in a
given time t in order to maximize its Q-Value.

$Q\left( s_{t},a_{t} \right)$Q(s~t~, a~t~) is calculated for each
possible action in the current state using a forward propagation.

Using an ε-greedy strategy the agent chooses the action with the highest
Q-Value or a random one. It is important to state that the action took
and the entire neural network output is saved in order to learn in t+1.

### Policy Update

In time $t$ the agent takes an action and we know what its reward is. In
standard reinforcement learning we would simply update its value using
SARSA formula but since we are using a neural network we need to use an
algorithm to update the network weights with a backpropagation
algorithm. The main problem with backpropagation is that in order to
update the network we should provide target values for every output of
the approximation function, in this case Q-Values for each action, even
if we only took one action.

For target value, we identify what is commonly used for classifiers as
the correct value that the classifier should have predicted for that
input, even if this is not exactly true for our case.

Each time the agent takes an action and receives a reward from it, a new
Q-Value for that $(state,action)$(state, action) couple can be
calculated. That Q-Value is not Q^\*^ (which would be the true target
value), but we can say that it is a better approximation of the previous
one thanks to the convergence of reinforcement learning algorithms.

Target value for each action cannot be calculated for a simple reason:
the agent took an action in time $t$, and we can only calculate the
reward~t+1~ for the action the agent actually took.

In order to apply backpropagation, we first calculate the target value
for the action we took.

$Q\left( s_{t},a_{t} \right)$*Q(s~t+1~, a~t+1~)* is calculated for each
possible action in the new state using a forward propagation. Since we
are using SARSA, next action isn’t always the best according to the
policy so the agent might choose random one using an
$\varepsilon$-greedy strategy.

We hereby remind that each neural network output is an action. So, in
order to do backpropagation, we set as
$Q\left( s_{t},a_{t} \right)$Q(s~t~, a~t~)~target~ the previous Q-Value
with the exception of the action took a~t~. For the actually took
action, we know the actual reward given, so
$Q\left( s_{t},a_{t} \right)$*Q(s~t~, a~t~)~target~* is calculated as
follows:

$${Q\left( s_{t},a_{t} \right)}_{\text{target}} = \text{reward}_{t} + \ \gamma\ Q\left( s_{t + 1},a_{t + 1} \right)$$

Weights will be then adjusted accordingly and the agent learned from the
action he took in a particular state.

Experiments
===========

Our approach has been tested using the random level generation module
offered by the framework.

We will use an abuse of notation when we are referring to the training
set and the test set.

Strictly formally speaking, training set should be composed of gameplay
instants and their relative correct key-combination in order to obtain
the best score; basically, a recording of skilled human players playing
some levels. Test set should be the same, but some instants never seen
before by the agent.

The goal of our project though is very different: we want the agent to
learn by itself, using only our reward function as a guide.

We are here referring to training set as the set of randomly generated
levels that the agent will see during training, which is intuitively
correct.

The test set will be composed by randomly generated levels that the
agent has never seen before and its score playing them will provide a
benchmark.

Since Q^\*^ values cannot be calculated, our loss function cannot be
represented by
*|Q^\*^*${|Q}^{*}(s_{t},a_{t}) - Q^{*}(s_{t},a_{t})|$*(s~t~, a~t~) -
Q(s~t~, a~t~)|* therefore it cannot represent how well does our agent
and its regressor performs.

That is the reason why we measured the loss function differently and we
will analyse its values during the training process, but in order to
analyse the agent’s performance we will use its score, which is more
fitting and represents a way more concrete measurement.

With the term epoch, we are referring to a single playthrough of a
randomly generated level.

The number of epochs used in the experiments might seem low at a first
glance but since we are using an online learning algorithm, for each
epoch we are doing a forward and backward pass at each time t, which is
approximately 40 milliseconds.

Given the time limit of 200 seconds in order to finish the level,
approximately 5000 backward propagations per epoch can be done.

The score function is the one we are using for the learning algorithm
and has been chosen for benchmark as it takes into account multiple
factors such as coins, enemies killed and distance covered.

As stated before, it is impossible to measure the difference between
current Q-Value and $Q^{*}$, so our loss function is basically the
difference between the newly calculated Q-Value that will be used for
backpropagation at time t and the old one that was the result of a
forward pass at time t-1.

Training
--------

Each epoch is single level run. We created a pool of 10 levels generated
randomly; we can consider this pool as our training set.

From this pool of 10 randomly generated levels, we kept the seed of 1 of
them; this level will be used in the benchmark chapter to add additional
considerations on the correlation between performance on the test and
the training set.

We have chosen the lowest possible difficulty since on higher levels the
hole detection represented an obstacle too complex for our agent to
learn as we it will be explained in chapter 7.2.

It is important to state that while theoretically our goal should be
minimizing the loss function and therefore small reading should be
desirable, we do not have a $Q^{*}$ to calculate difference with.

As shown in the following graph, the values still fluctuate even after
several epochs but an interesting result can be seen comparing
approximately the first 150 values with the values after that and its
correlation with the agent benchmark performance.

[\[CHART\]]{.chart}

Values after approximately 150 are higher and pretty much stable in
their fluctuation range: this means that during the first epochs the
agent took mostly actions that led him to stand still and do nothing.
This resulted in a small Q-Value that was pretty close to the actual
Q^\*^ for those (state, action) couples which is 0. In other words, the
agent spent a lot of time in order to understand that certain key
combination provided a 0 reward and after having discarded them, it
started exploring the world with key combinations that produced a reward
very far from 0, whose Q^\*^ cannot be calculated. Therefore, the
fluctuation registers how the newly calculated Q-Value was different by
the previous estimate and this quantity is affected by noise.

Benchmark
---------

In order to evaluate the performance of the agent, we are going to use a
test set formed by 3 levels that will be identical through the
experiment; 1 more levels will be added that were previously part of the
training set: this way we can show how well the agent performs on the
test and training set levels at various stages of the learning process.
The value using for measuring performance is, of course, our score
function.

For each level, each agent has gone through 50 runs and the score we
report here is the average among the playthroughs.

During these runs, the neural network didn’t learn since we did only
forward propagations to be sure that the degree of intelligence of the
system was frozen at the time it finished its training.

           Average Score
  -------- --------------- ----------- ----------- -----------
  Epochs   Level 10        Level 100   Level 200   Level 300
  10       1719,12         1174,44     1188,96     2423,84
  50       2066,32         1170,88     1205,46     2731,96
  100      1831,42         1377,64     1294,02     2571,52
  150      1795            1070,3      1402,24     2786,46
  200      3679,48         2699,76     2942,44     3607,24
  300      3424            2053,02     3377,12     3578,56
  400      3246,18         1901,84     3341,66     3327,66
  500      3522,6          2631,76     2919,34     3394,48

[\[CHART\]]{.chart}

The score function starts with very low values: in the first iterations,
the agents basically does random actions and often remains at the start
of the level dying because of timeout. Increasing the number of the
epochs up to 200, the agent gets better results and increases its score
consistently: after that, the score fluctuates on random factors (for
example, the results of $\varepsilon$-greedy policy), showing the
performance limits of our approach. That score fluctuation is caused by
some game mechanisms that the agent didn’t understand properly; for
example, the agent often dies because he is unable to avoid enemies.

It is interesting to see that the average score starts to increase
considerably after the loss function value stabilizes, as mentioned in
the previous chapter.

One positive outcome of our experiments is the acceptable consistency
between the performance shown in the training set level and the other
ones, part of the test set, that were never seen before by the agent.

Conclusions
===========

Our agent shown an acceptable result with levels without holes, even if
generated at runtime.

We find the result particularly positive if we think about how abstract
our model is; if a game is able to provide a coherent state
representation and its fitness function, our model could still be a
viable solution by just tweaking hyperparameters such as learning rate
or using a different reinforcement learning algorithm such as
Q-Learning.

It would be interesting to find out results of our model on similar
games.

Reached Goals
-------------

Our agent manages to learn the basic rules needed to play the game in a
decent way. It manages to understand that it needs to go from left to
right in order to win, managed to overcome static obstacles and
understood some of the secondary goals.

### Input Correlation

In order to overcome static obstacles, Mario needed to learn how to
jump. What seems an easy task at first, is fairly complex instead
because our system isn’t Markovian. If in the time frame t our agent
presses the Jump button, it jumps only if in the previous time frame
that button wasn’t pressed. Since in the neural network input we have
given previous inputs, basically previous inputs are part of the state:
this led our agent to understand input correlation and understood how to
jump.

### Secondary goals

Our score function managed to give a small but concrete bonus for coin
collection: this led the agent to follow different rules only to collect
coins, even if it required little backtracking.

Limitations of our Approach
---------------------------

Out agent failed to understand more complex patterns, and in the
following chapters we will try to explain why. Basically, the agent
understood to go from left to right and to avoid static obstacles (like
tubes or steps) but was unable to understand how to accomplish more
complex tasks. The problems we have encountered are pretty much the same
that other researchers experienced (an example of a similar approach can
be seen in a github repository of an MIT researcher:
[*http://github.com/aleju/mario-ai*](http://github.com/aleju/mario-ai),
even if it focuses on overfitting on one very easy level).

### Jumping Holes

The main problem with jumping holes is that giving a negative reward on
death makes the agent “scared” of getting near its pattern, even if it
is the only path to victory. Unfortunately, this doesn’t work very well
with the fact that the reward is given at runtime with the progress on
the level, so the agent is unable to grasp the connection between these
two reward’s components. This caused disastrous results for our agent in
environments with holes: it either went straight into them or ran away
from them going back in the level.

### False Positives

Reinforcement learning works best in environments in which turns are
defined in a discrete way. Unfortunately, this is not the case for Super
Mario; sometimes false rewards are given to bad key combinations for
this reason.

For example, let’s say our agent is running as fast as possible to the
right, getting the highest reward possible. Let’s say that at time t+1,
the agent decides to do random action using exploration and stops. Given
the nature or our environment, Mario will go on a little bit thanks to
the inertia of the previous inputs and this causes a bad key combination
to obtain a small positive reward.

We tried to mitigate this problem putting player input history as neural
network input, but unfortunately the problem still remains and
negatively impacts on the agent performance.

### Avoiding Enemies

Our approach tried to give negative rewards each time Mario bumps into
an enemy, in order to teach him to avoid them.

Tweaking score function parameters was incredibly hard: giving a reward
too low for going right, made the agent stand still and giving a reward
too high made it ignore enemies on the way in order to run faster. We
didn’t manage to find a perfect balance between the components of the
score function and unfortunately, even if the agent understands to avoid
static obstacles, it struggles to avoid enemies and sometimes still dies
because of them. This problem is particularly hard to solve since
jumping on enemies gives a positive reward but touching them hits Mario:
the input sequence must be extremely precise and our agent often doesn’t
manage to reach the necessary precision.

Our Contribution Related to Other Similar Approaches
----------------------------------------------------

This is something many other approaches didn’t even try to do; as a
matter of fact, most genetic algorithms or machine learning approaches
to Super Mario tried to overfit to a single level so the agent actually
learned to play a particular Super Mario level rather than the entire
game.

Our approach tries to grasp the common patterns between randomly
generated levels and produce an agent that is able to solve
never-seen-before levels in an acceptable way.

##### References

1.  A. Jones, “An Explanation of Xavier Initialization,”
    [*http://andyljones.tumblr.com/post/110998971763/an-explanation-of-xavier-initialization*](http://andyljones.tumblr.com/post/110998971763/an-explanation-of-xavier-initialization),
    February 2015.

2.  T. Matiisen, “Demystifying Deep Reinforcement Learning,”
    [*http://neuro.cs.ut.ee/demystifying-deep-reinforcement-learning/*](http://neuro.cs.ut.ee/demystifying-deep-reinforcement-learning/),
    Institute of Computer Science, University of Tartu, December 2015.

3.  V. Mnih, K. Kavukcuoglu, D. Silver, A. Graves, I. Antonoglou, D.
    Wierstra and M. Riedmiller, “Playing Atari with Deep Reinforcement
    Learning”
    [*https://www.cs.toronto.edu/\~vmnih/docs/dqn.pdf*](https://www.cs.toronto.edu/~vmnih/docs/dqn.pdf),
    DeepMind Technologie, December 2013.

4.  A. Jung, “Playing Mario with Deep Reinforcement,”
    [*https://github.com/aleju/mario-ai*](https://github.com/aleju/mario-ai),
    May 2016.

5.  B. Krose and P. Van der Smagt, “An Introduction to Neural Networks,”
    Eighth edition, The University of Amsterdam, 1996.

6.  R. S. Sutton and A. G. Barto, “Reinforcement Learning: An
    Introduction,” Second edition, The MIT Press Cambridge, 2012.


