import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

// Classe para visualizar a árvore binária de código morse
public class TreeVisualizer extends JPanel {
    private MorseNode root;
    private int nodeSize = 30;
    private int horizontalSpacing = 20;
    private int verticalSpacing = 50;
    private int treeHeight;
    private int maxNodeWidth;

    // Construtor para o visualizador de árvore
    // @param root Raiz da árvore a ser visualizada
    public TreeVisualizer(MorseNode root) {
        this.root = root;
        this.treeHeight = calculateTreeHeight(root);
        this.maxNodeWidth = 2400;

        setPreferredSize(new Dimension(maxNodeWidth, treeHeight * (nodeSize + verticalSpacing) + 50));
        setBackground(Color.WHITE);
    }

    // Calcula a altura da árvore
    // @param node O nó atual
    // @return A altura da árvore
    private int calculateTreeHeight(MorseNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(calculateTreeHeight(node.left), calculateTreeHeight(node.right));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Desenha a árvore a partir da raiz
        drawTree(g2d, root, getWidth() / 2, 40, getWidth() / 4);
    }

    // Desenha recursivamente a árvore
    // @param g2d Contexto gráfico
    // @param node Nó atual
    // @param x Posição x do nó
    // @param y Posição y do nó
    // @param xOffset Deslocamento horizontal para os nós filhos
    private void drawTree(Graphics2D g2d, MorseNode node, int x, int y, int xOffset) {
        if (node == null) {
            return;
        }

        // Desenha o nó
        g2d.setColor(new Color(240, 240, 255));
        g2d.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);

        // Desenha o caractere no nó
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        String nodeValue = (node.character != '\0') ? String.valueOf(node.character) : "•";
        int textWidth = fm.stringWidth(nodeValue);
        int textHeight = fm.getHeight();
        g2d.drawString(nodeValue, x - textWidth / 2, y + textHeight / 4);

        // Se for uma folha com caractere, desenha o código morse abaixo do nó
        if (node.character != '\0' && !node.morseCode.isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(Color.gray);
            fm = g2d.getFontMetrics();
            textWidth = fm.stringWidth(node.morseCode);
            g2d.drawString(node.morseCode, x - textWidth / 2, y + nodeSize);
        }

        // Desenha os filhos, se existirem
        int nextY = y + verticalSpacing;
        int newXOffset = Math.max(xOffset / 2, 20);

        if (node.left != null) {
            // Desenha a linha para o filho esquerdo com "." (ponto)
            int leftX = x - newXOffset;
            g2d.draw(new Line2D.Double(x, y + nodeSize / 2, leftX, nextY - nodeSize / 2));
            g2d.setFont(new Font("Arial", Font.BOLD, 44));
            g2d.setColor(Color.GREEN);
            g2d.drawString(".", (x + leftX) / 2 - 5, (y + nextY) / 2);
            drawTree(g2d, node.left, leftX, nextY, newXOffset);
        }

        if (node.right != null) {
            // Desenha a linha para o filho direito com "-" (traço)
            int rightX = x + newXOffset;
            g2d.draw(new Line2D.Double(x, y + nodeSize / 2, rightX, nextY - nodeSize / 2));
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            g2d.setColor(Color.RED);
            g2d.drawString("-", (x + rightX) / 2 - 5, (y + nextY) / 2);
            drawTree(g2d, node.right, rightX, nextY, newXOffset);
        }
    }

    // Salva a visualização da árvore como imagem
    // @param filename Nome do arquivo para salvar
    public void saveImage(String filename) {
        // Esta funcionalidade poderia ser implementada para salvar a árvore como imagem
        // Requer bibliotecas adicionais como ImageIO
        JOptionPane.showMessageDialog(this,
                "A função de salvar imagem seria implementada aqui.\n" +
                        "Salvaria em: " + filename);
    }
}