import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
	private final WeightedQuickUnionUF uf; // estrutura de dados com nós do topo e baixo
	private final WeightedQuickUnionUF backwash; // estrutura de dados apenas com nó do topo
	private final boolean[] states; // guarda os estados de cada site (true = aberto, false = fechado)
	private int openSitesCount;
	private final int vTop; // nó do topo
	private final int vBottom; // nó de baixo
	private final int n;

	// creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
    	if (n <= 0) {
    		throw new IllegalArgumentException("The value of n should be greater than 0, input: " + n);
    	}
    	
    	this.n = n;
    	this.states = new boolean[n * n];
    	this.openSitesCount = 0;
    	this.vTop = n * n;
    	this.vBottom = n * n + 1;
    	this.uf = new WeightedQuickUnionUF(n * n + 2); // inicializado com 2 nós extras (topo e baixo)
    	this.backwash = new WeightedQuickUnionUF(n * n + 1); // inicializado com 1 nó extra (apenas topo, para evitar backwash)
    }
    
    private void validate(int row, int col) {
    	if (row < 1 || row > n || col < 1 || col > n) {
    		throw new IllegalArgumentException("The values of row and col should be between 1 and " + n 
    				+ ", input: [" + row + ", " + col + "]");
    	}
    }
    
    // converte índice 2D (linha, coluna) para índice 1D (array)
    private int xyTo1D(int row, int col) {
    	return (row - 1) * n + (col - 1);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
    }

    // does the system percolate?
    public boolean percolates() {
    }
}
