import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
	private static final double CONFIDENCE = 1.96;
	private final double[] thresholds;
	private final int trials;
	
	// perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
    	if (n <= 0) {
    		throw new IllegalArgumentException("The value of n should be greater than 0, input: " + n);
    	}
    	
    	if (trials <= 0) {
    		throw new IllegalArgumentException("The value of trials should be greater than 0, input: " + n);
    	}
    	
    	this.trials = trials;
    	this.thresholds = new double[trials];
    	
    	for (int T = 0; T < trials; T++) {
    		Percolation p = new Percolation(n);
    		
    		while (!p.percolates()) {
    			int row = StdRandom.uniformInt(1, n + 1);
    			int col = StdRandom.uniformInt(1, n + 1);
    			
    			if (!p.isOpen(row, col)) {
    				p.open(row, col);
    			}
    		}
    		
    		thresholds[T] = (double) p.numberOfOpenSites() / (n * n);
    	}
    	
    }

    // sample mean of percolation threshold
    public double mean() {
    	return StdStats.mean(thresholds);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
    	if (trials == 1) {
    		return Double.NaN; // desvio padrão é indefinido para 1 único trial
    	}
    	return StdStats.stddev(thresholds);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
    	return mean() - (CONFIDENCE * stddev() / Math.sqrt(trials));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
    	return mean() + (CONFIDENCE * stddev() / Math.sqrt(trials));
    }
    
    public static void main(String[] args) {
    }
}