import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
 
public class PercolationVisualizer extends JFrame {
 
    private static final int CANVAS_TARGET_SIZE = 640; 
    private static final int MAX_N = 300;
 
    private static final Color COLOR_BLOCKED = Color.BLACK;
    private static final Color COLOR_OPEN = Color.WHITE;
    private static final Color COLOR_FULL = new Color(55, 138, 221); 
 
    private int n;
    private Percolation percolation;
 
    private final JLabel statsLabel;
    private final JLabel statusLabel;
    private final GridPanel gridPanel;
 
    private JButton autoButton;
    private Timer autoTimer; 
 
    public PercolationVisualizer() {
        super("Percolation Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        // estado inicial: n e percolation sao definidos juntos, uma unica vez
        this.n = askForN(null);
        this.percolation = new Percolation(n);
 
        statsLabel = new JLabel();
        statusLabel = new JLabel();
 
        setLayout(new BorderLayout());
        add(buildTopPanel(), BorderLayout.NORTH); 

        gridPanel = new GridPanel();

        JPanel centeredWrapper = new JPanel(new GridBagLayout());
        centeredWrapper.setBackground(Color.DARK_GRAY);
        centeredWrapper.add(gridPanel);

        JScrollPane scrollPane = new JScrollPane(centeredWrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
 
        add(buildLegendPanel(), BorderLayout.SOUTH);
 
        updateLabels();
 
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
 
    // inicializa um novo grid n x n
    private void startNewGrid(int newN) {
        this.n = newN;
        this.percolation = new Percolation(newN);
        gridPanel.recalculateCellSize();
        pack();
        updateLabels();
        gridPanel.repaint();
    }
 
    // pede um valor de n ao usuario; retorna -1 se cancelado (apenas quando parentForDialog != null)
    private int askForN(Component parentForDialog) {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    parentForDialog,
                    "Enter a value for N to generate an N X N grid (range limit: 1 to " + MAX_N + "):",
                    "Percolation Grid configuration",
                    JOptionPane.QUESTION_MESSAGE
            );
 
            if (input == null) {
                if (parentForDialog == null) {
                    System.exit(0); // cancelou na inicializacao: encerra o programa
                }
                return -1; // cancelou em uma chamada posterior: mantem o grid atual
            }
 
            try {
                int value = Integer.parseInt(input.trim());
                if (value > 0 && value <= MAX_N) {
                    return value;
                }
                JOptionPane.showMessageDialog(parentForDialog,
                        "Enter an INteger number between 1 and " + MAX_N,
                        "Invalid input", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(parentForDialog,
                        "Please, enter a valid Integer number",
                        "Invalid input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
 
        statsLabel.setFont(statsLabel.getFont().deriveFont(Font.PLAIN, 13f));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 13f));
 
        JButton newGridButton = new JButton("Generate grid");
        newGridButton.addActionListener(e -> {
            stopAutoTimer();
 
            int newN = askForN(this);
            if (newN == -1) return; // cancelado: mantem o grid atual
 
            startNewGrid(newN); // unico lugar que recria o estado do grid
        });
 
        JButton randomButton = new JButton("Open random site");
        randomButton.addActionListener(e -> {
            openRandomSite();
            updateLabels();
            gridPanel.repaint();
        });
 
        autoButton = new JButton("Simulate percolation");
        autoButton.addActionListener(e -> togglePercolateSimulation());
 
        panel.add(statsLabel);
        panel.add(statusLabel);
        panel.add(newGridButton);
        panel.add(randomButton);
        panel.add(autoButton);
 
        return panel;
    }
 
    private JPanel buildLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
        panel.add(legendItem(COLOR_BLOCKED, "Blocked"));
        panel.add(legendItem(COLOR_OPEN, "Open"));
        panel.add(legendItem(COLOR_FULL, "Full"));
        return panel;
    }
 
    private JPanel legendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JPanel swatch = new JPanel();
        swatch.setBackground(color);
        swatch.setPreferredSize(new Dimension(14, 14));
        swatch.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        item.add(swatch);
        item.add(new JLabel(text));
        return item;
    }
 
    private void openRandomSite() {
        if (percolation.numberOfOpenSites() >= n * n) return;
        int row, col;
        do {
            row = (int) (Math.random() * n) + 1;
            col = (int) (Math.random() * n) + 1;
        } while (percolation.isOpen(row, col));
        percolation.open(row, col);
    }
 
    private void togglePercolateSimulation() {
        if (autoTimer != null && autoTimer.isRunning()) {
            stopAutoTimer();
            return;
        }
        autoButton.setText("Stop");
        autoTimer = new Timer(25, e -> {
            if (percolation.percolates() || percolation.numberOfOpenSites() >= n * n) {
                stopAutoTimer();
                return;
            }
            openRandomSite();
            updateLabels();
            gridPanel.repaint();
        });
        autoTimer.start();
    }
 
    private void stopAutoTimer() {
        if (autoTimer != null && autoTimer.isRunning()) {
            autoTimer.stop();
        }
        if (autoButton != null) {
            autoButton.setText("Simulate percolation");
        }
    }
 
    private void updateLabels() {
        int total = n * n;
        int open = percolation.numberOfOpenSites();
        double frac = total == 0 ? 0 : (100.0 * open / total);
        statsLabel.setText(String.format("Open sites: %d / %d  (%.1f%%)", open, total, frac));
 
        boolean percolates = percolation.percolates();
        statusLabel.setText(percolates ? "Percolates" : "Does not percolate");
        statusLabel.setForeground(percolates ? new Color(0, 140, 0) : Color.DARK_GRAY);
    }
 
    // classe customizada que desenha o grid e trata eventos de cliques do mouse
    private class GridPanel extends JPanel {
        private int cellSize;
 
        GridPanel() {
            recalculateCellSize();
            setBackground(Color.DARK_GRAY);
 
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    final int size = n;
                    final Percolation current = percolation;
 
                    int col = e.getX() / cellSize + 1;
                    int row = e.getY() / cellSize + 1;
                    if (row >= 1 && row <= size && col >= 1 && col <= size
                            && !current.isOpen(row, col)) {
                        current.open(row, col);
                        updateLabels();
                        repaint();
                    }
                }
            });
        }
 
        void recalculateCellSize() {
            cellSize = Math.max(2, CANVAS_TARGET_SIZE / n);
            setPreferredSize(new Dimension(cellSize * n, cellSize * n));
            revalidate();
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            final int size = n;
            final Percolation current = percolation;
 
            for (int row = 1; row <= size; row++) {
                for (int col = 1; col <= size; col++) {
                    Color color;
                    if (!current.isOpen(row, col)) {
                        color = COLOR_BLOCKED;
                    } else if (current.isFull(row, col)) {
                        color = COLOR_FULL;
                    } else {
                        color = COLOR_OPEN;
                    }
 
                    int x = (col - 1) * cellSize;
                    int y = (row - 1) * cellSize;
                    g2.setColor(color);
                    g2.fillRect(x, y, cellSize, cellSize);
 
                    if (cellSize > 4) {
                        g2.setColor(new Color(160, 160, 160));
                        g2.drawRect(x, y, cellSize, cellSize);
                    }
                }
            }
        }
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PercolationVisualizer::new);
    }
}
