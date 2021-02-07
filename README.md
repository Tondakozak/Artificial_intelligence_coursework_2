# Artificial_intelligence_coursework_2
Artificial Inteligence, Computer Science, Middlesex University, London

## Overview
In this project we should choose and implement an algorithm for simple text recognition from pictures. There were a set of standardized pictures of numbers.


## Multi layer perceptron
### Description
Single-layer perceptron (or just Perceptron) was one of the first attempts at simulating brain functions.  The basic idea of perceptron algorithm is simple: the perceptron consists of n-number of inputs with n-number of weights and a transfer function. The weighted inputs are summed up, and the sum is passed through a transport function which returns one result of the perceptron. The weights indicate an importance of the particular input. There are many kinds of transfer functions: step function, a sigmoid function, linear function, etc. 
The only one perceptron is a linear classifier, thus it would not be efficient for our problem, so I use the extended version which is multi-layer perceptron.
In that case, many perceptrons are connected into neural network. 
A typical neural network based on multi-layer perceptron has one input layer, one or more hidden layers and one output layer. All the perceptrons in one layer receive a signal from all the perceptrons from the previous layer, and they are connected with all perceptrons in subsequent layer
Supervised learning is usually implemented using back-propagation algorithm which consists of two steps. Firstly there is a forward pass – the given inputs are evaluated and compared with predicted outputs. Secondly, in backward pass, the weights are updated to get the required output.
I have used backpropagation algorithm described here https://www.root.cz/clanky/biologicke-algoritmy-5-neuronove-site/?ic=serial-box&icc=text-title (sorry, it is in the Czech language).


### Training
The network initializes weights in the beginning to random double number in the range <-1.0; 1.0>, the threshold is set to be 1, learning rate 0.05. The network trains at maximum 2000 iterations or 2 minutes or the training result reaches defined value (depends what comes first). If the network stuck, mutation is applied.

### Mutation
Sometimes it happens, that while learning the system is stuck in a local maximum. To prevent this situation, I monitor if the learning result getting better, if not a mutation is applied. It means that defined number of weights are set to random values.

### Defining parameters
Quality of the networks depends on many parameters such as a number of hidden layers and number of neurons in each layer, γ, etc. To try to find the best combination of parameters I have used a genetic algorithm. There were 50 individuals in each generation and the program run for several hours. I stored configuration of the networks (individuals) and their results to text file so that I could analyse it later. 
The network can return results with accuracy about 96 % and these do not depend too much on the number of hidden layers (I tried it on range <1; 6> layers). For reasonable result ( > 90 %) it seems to be necessary to have more than 10 neurons in two hidden layers or at least 17 neurons in one hidden layer.
