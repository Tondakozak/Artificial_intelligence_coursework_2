

/**
 * Classify the data using Euclidean Distance algorithm
 * @author Tonda Kozák
 *
 */
public class EuclideanDistance {
	private static int DATA_LINE_LENGTH = 65;
	private static int DATA_RESULT_POSITION = 64;
	


	/**
	 * Get distance between two points
	 * @param input1
	 * @param input2
	 * @return
	 */
	private double distance(double[] input1, double[] input2) {
		double sum = 0;
		for (int featureIndex = 0; featureIndex < DATA_LINE_LENGTH-1; featureIndex++) {
			sum += Math.pow(input2[featureIndex] - input1[featureIndex], 2);
		}
		
		return Math.pow(sum, 0.5);
	}
	
	
	/**
	 * Get result number from the test
	 * @param test
	 * @param training
	 * @return
	 */
	public double result(double[] test, double[][] training) {
		double bestDistance = Double.MAX_VALUE;
		double answer = -1;
		for (int dataIndex = 0; dataIndex < training.length; dataIndex++) {
			double distance = distance(test, training[dataIndex]);
			if (distance < bestDistance) {
				bestDistance = distance;
				answer = training[dataIndex][DATA_RESULT_POSITION];
			}
		}
		return answer;
	}
	
	/**
	 * Test the algorithm
	 * @param trainingData
	 * @param testData
	 */
	public void test(double[][] trainingData, double[][] testData) {
		int errorNum = 0;
		int correctNum = 0;
		for (int testIndex = 0; testIndex < testData.length; testIndex++) {
			double result = result(testData[testIndex], trainingData);
			
			// uncomment the next line to see detailed results of the testing
			// System.out.println("result: "+result+", should be: "+testData[testIndex][DATA_RESULT_POSITION]);
			
			if (result == testData[testIndex][DATA_RESULT_POSITION]) {
				correctNum++;
			} else {
				errorNum++;
			}
		}
		
		//System.out.println("Errors: "+errorNum);
		//System.out.println("Correct: "+correctNum);
		System.out.println("Error rate: "+(correctNum*1f/testData.length));
	}
	

}
