import java.io.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Manage the program, create two-fold tests using MLP algorithm and Euclidean distance algorithm
 * @author Tonda Kozák
 *
 */
public class Coursework {

	private static int DATA_LINE_LENGTH = 65;
	
	private String[] dataPath = {
			"cw2DataSet1.csv",
			"cw2DataSet2.csv"
	};

	private double[][] data1;
	private double[][] data2;

	public static void main(String[] args) {
		Coursework coursework = new Coursework();
		
		// read data
		coursework.data1 = coursework.readDataFromFile(coursework.dataPath[0]);
		coursework.data2 = coursework.readDataFromFile(coursework.dataPath[1]);

		
		// two fold test using Euclidean distance
		coursework.twoFoldTestEuclidean();
				
		System.out.println("\n\n");
		
		// two fold test using Multi-layer perceptron
		coursework.twoFoldTestMLP();
		
		
		// find the best configuration for MLP by genetic algorithm
		//coursework.findBestMLPByGenetic();
	}
	
	/**
	 * Two fold test with MLP
	 * @param coursework
	 */
	private void twoFoldTestMLP() {
		System.out.println("--------------------- Multi-layer Perceptron ---------------------");
		// First test
		System.out.println(" -------------- First test --------------");
		int[] neurons = {46, 42};
		int numberOfLayers = 2;
		double gama = 1.2;
		double errorRate = 0.998;
		int stuckIterations = 389;
		int mutationNum = 170;
		
		MLP mlp = new MLP(this.data1, this.data2, numberOfLayers, neurons, gama, errorRate, stuckIterations, mutationNum);	
		System.out.println("Training...");
		double trainResult = mlp.train();
		System.out.println("Training error rate: "+trainResult);
		System.out.println("\nTesting...");
		double testResult = mlp.test();
		System.out.println("Testing error rate: "+testResult);
		
		// Second test
		System.out.println("\n\n -------------- Second test --------------");
		MLP mlp2 = new MLP(this.data2, this.data1, numberOfLayers, neurons, gama, errorRate, stuckIterations, mutationNum);
		System.out.println("Training...");
		trainResult = mlp2.train();
		System.out.println("Training error rate: "+trainResult);
		System.out.println("Testing...");
		testResult = mlp2.test();
		
		System.out.println("Testing error rate: "+testResult);
	}
	
	private void twoFoldTestEuclidean() {
		System.out.println("--------------------- Euclidean Distance ---------------------");
		// First test
		System.out.println(" -------------- First test --------------");
		EuclideanDistance euDistance = new EuclideanDistance();
		euDistance.test(this.data1, this.data2);
		
		// Second test
		System.out.println("\n\n -------------- Second test --------------");
		euDistance.test(this.data2, this.data1);
	}

	/**
	 * Use genetic algorithm to find the best MLP
	 * @param coursework
	 */
	private void findBestMLPByGenetic() {
		int mutations = 10;
		int generationsNum = 1000;
		File resultFile = new File("result-n"+(System.nanoTime())+".txt");
        
        // create PrintWriter object
        PrintWriter result = null;
        try {
            result = new PrintWriter(resultFile);
        } catch (FileNotFoundException ex) { //the file doesn't exists
            System.out.println("The file doesn't exist");
            System.exit(2);
        }
		
		
        System.out.println("Starting genetics");
		Genetic generation = new Genetic(this.data1, this.data2, result);		
		generation.sort();
		generation.printStatus();
		
		// generations
		for (int generationId = 0; generationId< generationsNum; generationId++) {
			System.out.println("\n Generation: "+generationId);
			generation.nextGeneration();			
			generation.mutate(mutations);
			
			generation.printStatus();
		}
		
		
		result.close(); // close file
	}
	
	/**
	 * Read data from CSV file
	 * @param fileName
	 * @return
	 */
	public double[][] readDataFromFile(String fileName) {
		File dataFile = new File(fileName);
		ArrayList<double[]> content = new ArrayList<>();
		
	    Scanner input = null;
	    try {
	        input = new Scanner(dataFile);
	    } catch (FileNotFoundException ex) {
	        System.out.println("Problem with reading a file.");
	        System.exit(0);
	    }
	    
	    // reading the file
	    while (input.hasNextLine()) { // read line
	    	double[] contentLine = new double[DATA_LINE_LENGTH];
	    	
	    	String line = input.nextLine();
	    	String[] contentLineString = line.split(",");
	    	for (int numberIndex = 0; numberIndex < DATA_LINE_LENGTH; numberIndex++) {
	    		contentLine[numberIndex] = Integer.parseInt(contentLineString[numberIndex]);
	    	}
	        content.add(contentLine);
	    }	    
	    
	    input.close(); // close the file
	    
	    // convert list to array
	    double[][] r = new double[content.size()][DATA_LINE_LENGTH];
	    for(int i = 0; i < content.size(); i++) {
	    	r[i] = content.get(i);
	    }
	    return r;
	}
}
