import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Hold information about the gene (one individual) (configuration of one MLP)
 * @author Tonda Kozák
 *
 */
public class Gene implements Comparable<Gene>{
	double[][] testData;
	double[][] trainData;
	int hiddenNum; // number of hidden layers
	int[] neurons; // numbers of neurons in layers
	double gama; 
	double errorRate; // min error rate for training
	int stuckIterations;  
	int mutationNum;
	
	double evaluation = 0; 
	PrintWriter result; // PrintWriter for saving evaluation result into a file
		
	/**
	 * Constructor for the gene
	 * @param testData
	 * @param trainData
	 * @param hiddenNum
	 * @param neurons
	 * @param gama
	 * @param errorRate
	 * @param stuckIterations
	 * @param mutationNum
	 * @param result
	 */
	public Gene(double[][] testData, double[][] trainData, int hiddenNum, int[] neurons, double gama, double errorRate, int stuckIterations, int mutationNum, PrintWriter result) {
		this.testData = testData;
		this.trainData = trainData;
		this.hiddenNum = hiddenNum;
		this.neurons = new int[neurons.length];
		System.arraycopy( neurons, 0, this.neurons, 0, neurons.length );
		this.gama = gama;
		this.errorRate = errorRate;
		this.stuckIterations = stuckIterations;
		this.mutationNum = mutationNum;
		this.result = result;
	}
	
	/**
	 * Create string with parameters and results of evaluation
	 * @param fold
	 * @param hiddenNum
	 * @param neurons
	 * @param gama
	 * @param errorRate
	 * @param stuckIterations
	 * @param mutationNum
	 * @param trainResult
	 * @param timeTrain
	 * @param timeTest
	 * @param testResult
	 * @return
	 */
	private String printEvaluationResult(int fold, int hiddenNum, int[] neurons, double gama, double errorRate, int stuckIterations, int  mutationNum, double trainResult, long timeTrain, long timeTest, double testResult) {
		return ((1+fold)+"; "+hiddenNum+"; "+(Arrays.toString(neurons))+"; "+gama+"; "+errorRate+"; "+stuckIterations+"; "+mutationNum+"; "+trainResult+"; "+timeTrain+"; "+timeTest+"; "+testResult);
	}
	
	/**
	 * Evaluate the MLP using 2-fold test	
	 * @return
	 */
	public double evaluate() {
		// if the MLP was already evaluated, return saved value
		if (evaluation != 0) {
			return evaluation;
		}
		
		// do 2-fold test
		double[][][][] trainTestData = {
				{testData, trainData},
				{trainData, testData}
		};
		
		this.evaluation = 0;
		for (int fold = 0; fold < 2; fold++) { // 2 fold test
			MLP mlp = new MLP(trainTestData[fold][0], trainTestData[fold][1], hiddenNum, neurons, gama, errorRate, stuckIterations, mutationNum);
			// train
			long startTrain = System.nanoTime();
			double trainResult = mlp.train();
			long timeTrain = System.nanoTime()-startTrain;
			// test
			long startTest = System.nanoTime();
			double testResult = mlp.test();
			long timeTest = System.nanoTime()-startTest;
			
			//double coef = (trainResult*testResult*1.22057*Math.pow(10, 20))/(timeTrain*timeTest+1.07252*Math.pow(10, 18));
			
			String resultString = printEvaluationResult(fold, hiddenNum, neurons, gama, errorRate, stuckIterations, mutationNum, trainResult, timeTrain, timeTest, testResult);
			System.out.println(resultString);
			// save result into file
			result.println(resultString);
			result.flush();
			
			this.evaluation += testResult;
		}
		
		
		
		//evaluation = (coef+coef2)/2f;
		
		return evaluation;
	}
	
	
	
	

	/**
	 * Compare two genes
	 * @return
	 */
	@Override
	public int compareTo(Gene o) {	
		double x = this.evaluate();
		double y = o.evaluate();
		
		if (Math.abs(x - y) < .000000001) {
			return 0;
		}
		if (x < y) {
			return -1;
		} 
		return 1;
		
	}
	
}
