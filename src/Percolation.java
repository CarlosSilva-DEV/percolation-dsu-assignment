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
    	validate(row, col);
    	
    	int i = xyTo1D(row, col);
    	
    	if (states[i]) { // se i já estiver aberto, retorno vazio
    		return;
    	}
    	
    	states[i] = true;
    	openSitesCount++;
    	
    	if (row == 1) { // se estiver na primeira linha, conecta com o topo virtual
    		uf.union(i, vTop);
    		backwash.union(i, vTop);
    	}
    	
    	if (row == n) { // se estiver na última linha, conecta com o fundo virtual
    		uf.union(i, vBottom);
    	}
    	
    	if (row > 1 && isOpen(row - 1, col)) { // conecta com o vizinho de cima se estiver aberto
    		int neighbor = xyTo1D(row - 1, col);
    		uf.union(i, neighbor);
    		backwash.union(i, neighbor);
    	}
    	
    	if (row < n && isOpen(row + 1, col)) { // conecta com o vizinho de baixo se estiver aberto
    		int neighbor = xyTo1D(row + 1, col);
    		uf.union(i, neighbor);
    		backwash.union(i, neighbor);
    	}
    	
    	if (col > 1 && isOpen(row, col - 1)) {
    		int neighbor = xyTo1D(row, col - 1);
        	uf.union(i, neighbor);
        	backwash.union(i, neighbor);
    	}
    	
    	if (col < n && isOpen(row, col + 1)) {
    		int neighbor = xyTo1D(row, col + 1);
        	uf.union(i, neighbor);
        	backwash.union(i, neighbor);
    	}
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
    	validate(row, col);
    	return states[xyTo1D(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
    	validate(row, col);
    	int i = xyTo1D(row, col);
    	return backwash.find(i) == backwash.find(vTop); // compara se o nó raiz de i e vTop são iguais
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
    	return openSitesCount;
    }

    // does the system percolate?
    public boolean percolates() {
    }
}
