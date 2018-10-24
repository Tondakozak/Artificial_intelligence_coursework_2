import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Manage generations for genetic algoritm
 * @author Tonda Kozák
 *
 */
public class Genetic {
	public static int NUMBER_OF_GENES = 50; // number of genes in one generation
	LinkedList<Gene> genes = new LinkedList<>(); // hold genes of the generations
	LinkedList<Gene> offSprings = new LinkedList<>(); // children for the next generation
	Gene bestGene; // the best gene found so far
	
	int generationCounter = 1; // generation counter
	
	// data for testing and training
	double[][] testData;
	double[][] trainData;
	
	// PrintWriter for writing results of gene evaluations to the file
	PrintWriter result;

	// min and max values for genes parameters
//	int hiddenNumMin = 1;
//	int hiddenNumMax = 6;
//	int neuronNumMin = 5;
//	int neuronNumMax = 65;
//	
//	double gamaMin = 0.5;
//	double gamaMax = 1.5;
//	double errorRateMin = 0.98;
//	double errorRateMax = 1;
//	
//	int stuckIterationMin = 100;
//	int stuckIterationMax = 600;
//	int mutationNumMin = 5;
//	int mutationNumMax = 1000;
	
	
	int hiddenNumMin = 1;
	int hiddenNumMax = 4;
	int neuronNumMin = 1;
	int neuronNumMax = 20;
	
	double gamaMin = 0.9;
	double gamaMax = 1.1;
	double errorRateMin = 0.988;
	double errorRateMax = 1;
	
	int stuckIterationMin = 100;
	int stuckIterationMax = 600;
	int mutationNumMin = 5;
	int mutationNumMax = 1000;
	
	
	
	/**
	 * Constructor
	 * @param testData
	 * @param trainData
	 * @param result
	 */
	public Genetic(double[][] testData, double[][] trainData, PrintWriter result) {
		this.testData = testData;
		this.trainData = trainData;
		this.result = result;
		
		initGeneration();
	}
	
	/**
	 * Create first generation
	 */
	private void initGeneration() {
		for (int geneId = 0; geneId < NUMBER_OF_GENES; geneId++) {
			int hiddenNum = (int)(hiddenNumMin+Math.random()*(hiddenNumMax-hiddenNumMin));
			int[] neurons = new int[hiddenNum];
			for (int layerId = 0; layerId < hiddenNum; layerId++) {
				neurons[layerId] = (int)(neuronNumMin+Math.random()*(neuronNumMax-neuronNumMin));
			}
			double gama = (gamaMin + Math.random()*(gamaMax-gamaMin));
			double errorRate = (errorRateMin + Math.random()*(errorRateMax-errorRateMin));
			int stuckIterations = (int)(stuckIterationMin+Math.random()*(stuckIterationMax-stuckIterationMin));
			int mutationNum = (int)(stuckIterationMin+Math.random()*(stuckIterationMax-stuckIterationMin));
			
			genes.add(new Gene(testData, trainData, hiddenNum, neurons, gama, errorRate, stuckIterations, mutationNum, result));
		}
		
		// set one gene as the best
		bestGene = genes.getFirst();
	}
		
		
	/**
	 * Return average of two int
	 * @param x
	 * @param y
	 * @return
	 */
	private int avg(int x, int y) {
		return (x+y)/2;
	}
	
	/**
	 * Return average of two double
	 * @param x
	 * @param y
	 * @return
	 */
	private double avg(double x, double y) {
		return (x+y)/2f;
	}
		
	/**
	 * Apply cross-over on given genes, the first off spring is the first gene, the second one is made by average of the two genes
	 * @param gene1
	 * @param gene2
	 */
	private void crossOver(Gene gene1, Gene gene2) {
		// get child gene properties
		int hiddenNum = avg(gene1.hiddenNum, gene2.hiddenNum);
		int[] neurons = new int[hiddenNum];
		// get number of neurons in the hidden layers
		for (int layerId = 0; layerId < hiddenNum; layerId++) {
			if (gene1.hiddenNum > layerId && gene2.hiddenNum > layerId ) { // if both genes has this layer, get average
				neurons[layerId] = avg(gene1.neurons[layerId], gene2.neurons[layerId]);
				
			} else if (gene1.hiddenNum > layerId) { // otherwise if first gene has this layer, get this value
				neurons[layerId] = gene1.neurons[layerId];
			} else { // otherwise get value from the second gene
				neurons[layerId] = gene2.neurons[layerId];				
			}
		}
		
		double gama = avg(gene1.gama, gene2.gama);
		double errorRate = avg(gene1.errorRate, gene2.errorRate);
		int stuckIterations = avg(gene1.stuckIterations, gene2.stuckIterations);
		int mutationNum = avg(gene1.mutationNum, gene2.mutationNum);
				
		
		offSprings.add(new Gene(testData, trainData, hiddenNum, neurons, gama, errorRate, stuckIterations, mutationNum, result)); // child from average
		offSprings.add(new Gene(testData, trainData, gene1.hiddenNum, gene1.neurons, gene1.gama, gene1.errorRate, gene1.stuckIterations, gene1.mutationNum, result)); // the better parent goes to the next generation
	}
	
	
	/**
	 * Print status of the generation
	 */
	public void printStatus() {		
		System.out.println("Generation NO: "+generationCounter);
		System.out.println("Best value: "+genes.getFirst().evaluate() + " - "+bestGene.evaluate());
		System.out.println(bestGene.hiddenNum+"; "+(Arrays.toString(bestGene.neurons))+"; "+bestGene.gama+"; "+bestGene.errorRate+"; "+bestGene.stuckIterations+"; "+bestGene.mutationNum+"; R:"+bestGene.evaluation);
		System.out.println("------------------");
	}
	
	
	
	/**
	 * Increase generation counter
	 */
	public void increaseGenerationCounter() {
		this.generationCounter++;
	}
	
	/**
	 * Sort the list of genes
	 */
	public void sort() {
		try {
		Collections.sort(genes, Collections.reverseOrder());
		} catch (Exception e) {
			System.out.println("Problem with sorting");
			System.exit(3);
		}
	}
	
	/**
	 * Apply cross-over to the genes
	 */
	private void doCrossOver() {
		int size = genes.size()/2-1;
		
		for (int i = 0; i < size; i++) {			
			int secondGene = (int)(1 + Math.random()* (genes.size()-1)); // find second gene randomly
			crossOver(genes.get(0), genes.get(secondGene));
			genes.remove(secondGene);
			genes.remove(0);
		}
	}
	
	/**
	 * Create next generation - sort the genes, find the best gene, do cross-over
	 */
	public void nextGeneration() {
		sort();
		
		// if the best gene of the generation is the better, than the previous best, set new best gene
		if (bestGene.evaluate() < getBestValue()) {
			Gene g = getBestGene();
			bestGene = new Gene(g.testData, g.trainData, g.hiddenNum, g.neurons, g.gama, g.errorRate, g.stuckIterations, g.mutationNum, g.result);
		}
		
		// add two best genes to the next generation
		offSprings = new LinkedList<>();
		offSprings.add(genes.get(0));
		offSprings.add(genes.get(1));
		
		
		increaseGenerationCounter();
		doCrossOver();
		
		genes = offSprings; // set the new generation as the current one
		
		
	}
	
	/**
	 * Mutate genes
	 * @param mutationsNum
	 */
	public void mutate(int mutationsNum) {
		for (int mutationId = 0; mutationId < mutationsNum; mutationId++) {
			Gene gene = genes.get((int)(Math.random()*genes.size())); // gene for the mutation

			// generate new parameters
			gene.hiddenNum = (int)(hiddenNumMin+Math.random()*(hiddenNumMax-hiddenNumMin));
			int[] neuronsNew = new int[gene.hiddenNum];
			for (int layerId = 0; layerId < gene.hiddenNum; layerId++) {
				if (layerId < gene.neurons.length) {
					neuronsNew[layerId] = gene.neurons[layerId];
				} else {
					neuronsNew[layerId] = (int)(neuronNumMin+Math.random()*(neuronNumMax));
				}
			}
			gene.neurons = neuronsNew;
			
			gene.gama = (gamaMin + Math.random()*(gamaMax-gamaMin));
			gene.errorRate = (errorRateMin + Math.random()*(errorRateMax-errorRateMin));
			gene.stuckIterations = (int)(stuckIterationMin+Math.random()*(stuckIterationMax-stuckIterationMin));
			gene.mutationNum = (int)(mutationNumMin+Math.random()*(mutationNumMax-mutationNumMin));
			gene.evaluation = 0;
			
		}
	}
	
	// get value of the best gene of the generation
	public double getBestValue() {
		return genes.getFirst().evaluate();
	}
	
	/**
	 * Return best gene of the generation
	 * @return
	 */
	public Gene getBestGene() {
		return genes.getFirst();
	}
}





