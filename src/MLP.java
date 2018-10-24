import java.util.Arrays;

/**
 * Implements Multi-layer perceptron algorithm described here: https://www.root.cz/clanky/biologicke-algoritmy-5-neuronove-site/?ic=serial-box&icc=text-title
 * @author Tonda Kozák
 *
 */
public class MLP {

	private int inputNum = 64; // number of input perceptrons
	private int inputNumAll = inputNum + 1;
	private int resultPosition = 64;
	
	private int outNum = 10;
	private int maxIterations = 2000;
	private int maxTrainingTime = 1000*60*2;
	
	private double errorRate = 0.9989;
	private double learningRate = 0.05;
	private double gama = 1.0;
	private double treshold = 1;
	private int hiddenNum = 3; // number of hidden layers
	private int[] hiddenLayerNeuronsNum = {65, 66, 67, 68, 80}; // numbers of neurons in hidden layers
	
	// mutation
	private int stuckIterations = 100;
	private int mutationNum = 100;
	
	private double minWeight = -1;
	private double maxWeight = 1.0;
	
	
	private double[][] trainData;
	private double[][] testData;
	
	// Training
	private double[][] entryL; // entry layer 
	private double[][] entryResult; // expected output for the given inputs
	private double[][][] weight; // weights of hidden layers
	private double[][][] previousWeight; // previous weights of hidden layers
	private double[][] hiddenL; // hidden layers
	private double[] outLayer; // output layer
	private double[][] outWeight; // weights of the output layer
	private double[][] previousOutWeight; // previous weights of the output layer
	
	
	public MLP(double[][] testData, double[][] trainData) {
		this.testData = testData;
		this.trainData = trainData;
	}
	
	public MLP(double[][] testData, double[][] trainData, int hiddenNum, int[] neurons, double gama, double errorRate, int stuckIterations, int mutationNum) {
		this.testData = testData;
		this.trainData = trainData;
		this.hiddenNum = hiddenNum;
		this.hiddenLayerNeuronsNum = neurons;
		this.gama = gama;
		this.errorRate = errorRate;
		this.stuckIterations = stuckIterations;
		this.mutationNum = mutationNum;
	}
	
	/**
	 * Initiation of the entry layer (set inputs for the entry layer and expected output)
	 */
	private void initEntryL() {
		entryL = new double[trainData.length][inputNumAll];
		entryResult = new double[trainData.length][outNum];
		
		for (int dataId = 0; dataId < trainData.length; dataId++) {
			for (int inputId = 0; inputId < inputNum; inputId++) {
				entryL[dataId][inputId] = trainData[dataId][inputId];
			}
			entryL[dataId][inputNum] = treshold;
			for (int outputId = 0; outputId < outNum; outputId++) { // set expected output
				entryResult[dataId][outputId] = (trainData[dataId][resultPosition] == outputId)?1:0;
			}
		}
	}
	
	/**
	 * Set default weights for hidden layers and for output layer
	 */
	private void initWeights() {
		// hidden layers weights
		this.weight = new double[hiddenNum][inputNum][inputNum];
		this.previousWeight = new double[hiddenNum][inputNum][inputNum];		
		
		// layer
		for (int layerId = 0; layerId < hiddenNum; layerId++) {
			weight[layerId] = new double[hiddenLayerNeuronsNum[layerId]][inputNum];
			previousWeight[layerId] = new double[hiddenLayerNeuronsNum[layerId]][inputNum];
			
			for (int toId = 0; toId < weight[layerId].length; toId++) {
				int fromNum = (layerId == 0)?inputNum:hiddenLayerNeuronsNum[layerId-1];
				weight[layerId][toId] = new double[fromNum+1];
				previousWeight[layerId][toId] = new double[fromNum+1];
				
				for (int fromId = 0; fromId < weight[layerId][toId].length; fromId++) {
					weight[layerId][toId][fromId] = weightRand();
				}
			}
		}
		
		// Output layer weights
		this.outWeight = new double[outNum][hiddenL[hiddenNum-1].length];
		this.previousOutWeight = new double[outNum][hiddenL[hiddenNum-1].length];
		
		for (int fromId = 0; fromId < outWeight.length; fromId++) {			
			for (int toId = 0; toId < outWeight[0].length; toId++) {
				outWeight[fromId][toId] = weightRand();
			}
		}
		
	}
	
	/**
	 * Return random double for using as weight
	 * @return
	 */
	private double weightRand() {
		return (minWeight + Math.random()*(maxWeight-minWeight));
	}
	
	/**
	 * Initiation of the output layer
	 */
	private void initOutL() {
		outLayer = new double[outNum];
	}
	
	/**
	 * Initiation of hiden layers
	 */
	private void initHiddenL() {
		hiddenL = new double[hiddenNum][0];
		for (int layerId = 0; layerId < hiddenNum; layerId++) {
			hiddenL[layerId] = new double[hiddenLayerNeuronsNum[layerId]];
		}
	}

	/**
	 * Test (classify) the given data, returns error rate of the testing
	 * @return
	 */
	public double test() {
		int errors = 0;
		for (int dataId = 0; dataId < testData.length; dataId++) {
			int result = getResult(testData[dataId]);
			if (getResult(testData[dataId]) != testData[dataId][resultPosition]) {
				errors++;
			}
			// uncomment the next line to see detailed results of the testing
			//System.out.println("result: "+result+", should be: "+testData[dataId][resultPosition]);
		}
		
		//System.out.println("Errors: "+errors+", error rate"+((testData.length-errors*1f)/(testData.length)));
		return ((double)(testData.length-errors)/(testData.length));
	}
	
	/**
	 * Classify the input data and return the digit which is the result of the classification
	 * @param data
	 * @return
	 */
	private int getResult(double[] data) {
		result(data);
		// check
		double max = Double.MIN_VALUE;
		int maxId = 0;
		for (int outId = 0; outId < outNum; outId++) {
			if (outLayer[outId] > max) {
				max = outLayer[outId];
				maxId = outId;
			}
		}
		return maxId;
	}
	
	/**
	 * Classify the input data, result is in outLayer field
	 * @param features
	 * @return
	 */
	private double[] result(double[] features) {
		// classify in hidden layers
		double[] input;// = Arrays.copyOf(entryL[dataId], entryL[dataId].length);
		double[] output = Arrays.copyOf(features, features.length);
		output[resultPosition] = 1;
		
		for (int layerId = 0; layerId < hiddenNum; layerId++) {
			input = Arrays.copyOf(output, output.length);
			output = new double[hiddenLayerNeuronsNum[layerId]];
			
			// set neurons in the layer
			for (int neuronId = 0; neuronId < output.length-1; neuronId++) {
				double sum = sumInput(input, weight[layerId][neuronId]);
				output[neuronId] = sigmoid(sum);
				
				hiddenL[layerId][neuronId] = output[neuronId]; // set output to layer data
			}
			
			
		}
		
		// Classify in output layer				
		for (int neuronId = 0; neuronId < outNum; neuronId++) {
			double sum = sumInput(hiddenL[hiddenNum-1], outWeight[neuronId]);
			outLayer[neuronId] = sigmoid(sum);
		}
		return Arrays.copyOf(outLayer, outLayer.length);
	}

	/**
	 * Train the neural network
	 * @return
	 */
	public double train() {
		initHiddenL();
		initEntryL();
		initWeights();
		initOutL();
		
		long startTime = System.currentTimeMillis();
		double currentErrorRate = 0;
		double previousErrorRate = 0;
		
		int iterations = 0;
		int stuck = 0;
		do {
			iterations++;			
			// try all training data
			int errors = trainAllInputs();			
			
			currentErrorRate = (trainData.length-((double)errors)) / ((double)(trainData.length));			

			if (currentErrorRate > previousErrorRate) {
				stuck = 0;
			} else {
				stuck++;
				if (stuck == stuckIterations) { // if the result of the training didn't get better for more time than defined, mutation is done
					stuck = 0;
					currentErrorRate = 0;
					// mutation
					mutation();
				}
			}
			previousErrorRate = currentErrorRate;
			
			if (System.currentTimeMillis() - startTime > maxTrainingTime) { // if time is over
				return currentErrorRate;
			}
		} while (errorRate > currentErrorRate && iterations < maxIterations);
		
		return currentErrorRate;
	}
	
/**
 * Train the network on the set of inputs one time
 * @return
 */
	private int trainAllInputs() {
		int errors = 0;
		for (int dataId = 0; dataId < trainData.length; dataId++) {

			// find result
			result(trainData[dataId]);				
			
			/// Backpropagation
			backpropagation(dataId);			
			
			// check result
			double max = Double.MIN_VALUE;
			int maxId = 0;
			for (int outId = 0; outId < outNum; outId++) {
				if (outLayer[outId] > max) {
					max = outLayer[outId];
					maxId = outId;
				}
			}
			if (entryResult[dataId][maxId] != 1) {
				errors += 1;
			}
			
		}
		return errors;
	}
	
	/**
	 * Update weights using backpropagation
	 * @param dataId
	 */
	private void backpropagation(int dataId) {
		 // output layer
		// compute delta
		double[] delta = outputDelta(dataId);
		for (int neuronId = 0; neuronId < outNum; neuronId++) {					
			for (int previousId = 0; previousId < outWeight[neuronId].length; previousId++) {
				double weightChange = outputWeightChange(neuronId, previousId, delta);
				outWeight[neuronId][previousId] = outWeight[neuronId][previousId] + weightChange+previousOutWeight[neuronId][previousId];
			}
		}
		
		for (int layerId = hiddenNum-1; layerId >= 0; layerId--) { // update weights - last layer first
			delta = hiddenDelta(delta, layerId);
			for (int neuronId = 0; neuronId < hiddenL[layerId].length; neuronId++) {
				
				double[] previousLayer = (layerId == 0)?(entryL[dataId]):hiddenL[layerId-1];
				for (int previousId = 0; previousId < previousLayer.length; previousId++) {
					double weightChange = hiddenWeightChange(neuronId, previousId, dataId, layerId, delta);
					weight[layerId][neuronId][previousId] = weight[layerId][neuronId][previousId] + weightChange + previousWeight[layerId][neuronId][previousId];
				}
			}
		}
	}
	
	/**
	 * Mutate neurons
	 */
	private void mutation() {
		for (int i = 0; i < mutationNum; i++) {
			// mutate neurons in output layer
			int x = (int)(Math.random() * outWeight.length);
			int y = (int)(Math.random() * outWeight[x].length);
			outWeight[x][y] = weightRand();
			
			/**
			 * Mutate neurons in hidden layers
			 */
			int l = (int)(Math.random() * hiddenNum);
			 x = (int)(Math.random() * weight[l].length);
			 y = (int)(Math.random() * weight[l][x].length);
			 weight[l][x][y] = weightRand();
		}
	}
	
	/**
	 * calculate delta for output layer (backpropagation)
	 * @param dataId
	 * @return
	 */
	private double[] outputDelta(int dataId) {
		double[] delta = new double[outNum];
		for (int neuronId = 0; neuronId < outNum; neuronId++) {
			delta[neuronId] = gama * outLayer[neuronId] * (1 - outLayer[neuronId]) * (entryResult[dataId][neuronId] - outLayer[neuronId]);
		}
		
		return delta;		
	}
	
	/**
	 * calculate change for output layer (backpropagation)
	 * @param neuronId
	 * @param previousId
	 * @param delta
	 * @return
	 */
	private double outputWeightChange(int neuronId, int previousId, double[] delta) {
		return  learningRate * delta[neuronId] * hiddenL[hiddenNum-1][previousId];     
	}
	
	/**
	 * calculate delta for hidden layers (backpropagation)
	 * @param nextDelta
	 * @param layerId
	 * @return
	 */
	private double[] hiddenDelta(double[] nextDelta, int layerId) {
		double[] delta = new double[hiddenL[layerId].length];
		
		for (int neuronId = 0; neuronId < hiddenL[layerId].length; neuronId++) {
			// compute suma
			double suma = 0;
			double[][] nextWeights = (layerId+1 == hiddenNum)?outWeight:weight[layerId+1];
			double[] nextLayer = (layerId+1 == hiddenNum)?outLayer:hiddenL[layerId+1];
			for (int nextNeuronId = 0; nextNeuronId < nextLayer.length; nextNeuronId++) {
				suma += nextWeights[nextNeuronId][neuronId] * nextDelta[nextNeuronId];
			}
			delta[neuronId] = gama * hiddenL[layerId][neuronId] * (1 - hiddenL[layerId][neuronId]) * suma;
		}
		
		return delta;
	}
	
	/**
	 * Calculate change for hidden layers (backpropagation)
	 * @param neuronId
	 * @param previousId
	 * @param dataId
	 * @param layerId
	 * @param delta
	 * @return
	 */
	private double hiddenWeightChange(int neuronId, int previousId, int dataId, int layerId, double[] delta) {	
		
		double[] previousLayer = (layerId == 0)?(entryL[dataId]):hiddenL[layerId-1];		
		return learningRate * delta[neuronId] * previousLayer[previousId];     
	}
	
	/**
	 * calculate sigmoid function
	 * @param x
	 * @return
	 */
	private double sigmoid(double x) {
		return (1d / (1d + Math.pow(Math.E, -gama*x)));
	}
	
	/**
	 * Sum inputs multiplied by weights for perceptron
	 * @param input
	 * @param weights
	 * @return
	 */
	private double sumInput(double[] input, double[] weights) {
		double sum = 0;
		for (int inputId = 0; inputId < input.length; inputId++) {
			sum += input[inputId] * weights[inputId];
		}
		return sum;
	}
	
}
