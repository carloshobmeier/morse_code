import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.*;

/**
 * Classe para visualizar a árvore binária de código morse com animação
 * para mostrar o processo de decodificação e som para dots e dashes
 */
public class AnimatedTreeVisualizer extends JPanel {
    private MorseNode root;
    private int nodeSize = 30;
    private int horizontalSpacing = 20;
    private int verticalSpacing = 50;
    private int treeHeight;
    private int maxNodeWidth;

    // Variáveis para animação
    private List<MorseNode> highlightPath = new ArrayList<>();
    private int currentHighlightIndex = -1;
    private MorseNode targetNode = null;
    private Timer animationTimer;
    private int animationDelay = 500; // Delay em milissegundos entre cada passo

    // Status da animação
    private boolean animationRunning = false;
    private boolean loopAnimation = false; // Controla se a animação deve repetir

    // Variáveis para som
    private Clip dotSound;
    private Clip dashSound;
    private boolean soundEnabled = true;

    /**
     * Construtor para o visualizador de árvore
     * @param root Raiz da árvore a ser visualizada
     */
    public AnimatedTreeVisualizer(MorseNode root) {
        this.root = root;
        this.treeHeight = calculateTreeHeight(root);
        this.maxNodeWidth = 2400;

        setPreferredSize(new Dimension(maxNodeWidth, treeHeight * (nodeSize + verticalSpacing) + 50));
        setBackground(Color.WHITE);

        // Inicializa os sons
        initializeSounds();
    }

    /**
     * Inicializa os sons para dots e dashes gerando-os programaticamente
     */
    private void initializeSounds() {
        try {
            // Gera som para o ponto (dot) - frequência mais alta e duração curta
            dotSound = generateToneClip(800, 150); // 800Hz, 150ms

            // Gera som para o traço (dash) - frequência mais baixa e duração longa
            dashSound = generateToneClip(600, 450); // 600Hz, 450ms

            soundEnabled = true;
        } catch (LineUnavailableException e) {
            System.err.println("Erro ao gerar sons: " + e.getMessage());
            e.printStackTrace();
            soundEnabled = false;
        }
    }

    /**
     * Gera um clip de áudio com um tom de uma determinada frequência e duração
     *
     * @param frequency Frequência do tom em Hz
     * @param duration Duração do tom em ms
     * @return Um Clip contendo o tom gerado
     * @throws LineUnavailableException Se houver problema ao criar o clip
     */
    private Clip generateToneClip(float frequency, int duration) throws LineUnavailableException {
        // Formato de áudio: PCM_SIGNED, 44100Hz, 16 bits, mono
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

        // Tamanho do buffer em bytes
        int numSamples = (int)(format.getSampleRate() * duration / 1000.0);
        byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes por amostra

        // Gera uma onda senoidal
        double amplitude = 0.7 * Short.MAX_VALUE;
        double twoPiF = 2 * Math.PI * frequency;
        double step = frequency / format.getSampleRate();

        for (int i = 0; i < numSamples; i++) {
            double angle = twoPiF * i / format.getSampleRate();
            // Aplica um envelope ADSR simples para evitar cliques
            double envelope = 1.0;
            if (i < numSamples * 0.1) { // Attack: 10% do tempo
                envelope = i / (numSamples * 0.1);
            } else if (i > numSamples * 0.8) { // Release: 20% do tempo
                envelope = (numSamples - i) / (numSamples * 0.2);
            }

            short sample = (short)(amplitude * Math.sin(angle) * envelope);
            buffer[i * 2] = (byte)(sample & 0xFF);
            buffer[i * 2 + 1] = (byte)((sample >> 8) & 0xFF);
        }

        // Cria o clip e carrega o buffer
        Clip clip = AudioSystem.getClip();
        clip.open(format, buffer, 0, buffer.length);

        return clip;
    }

    /**
     * Reproduz o som do ponto (dot)
     */
    private void playDotSound() {
        if (soundEnabled && dotSound != null) {
            if (dotSound.isRunning()) {
                dotSound.stop();
            }
            dotSound.setFramePosition(0);
            dotSound.start();
        }
    }

    /**
     * Reproduz o som do traço (dash)
     */
    private void playDashSound() {
        if (soundEnabled && dashSound != null) {
            if (dashSound.isRunning()) {
                dashSound.stop();
            }
            dashSound.setFramePosition(0);
            dashSound.start();
        }
    }

    /**
     * Habilita ou desabilita os sons
     * @param enabled true para habilitar, false para desabilitar
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    /**
     * Verifica se o som está habilitado
     * @return true se estiver habilitado, false caso contrário
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Calcula a altura da árvore
     * @param node O nó atual
     * @return A altura da árvore
     */
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

    /**
     * Desenha recursivamente a árvore
     * @param g2d Contexto gráfico
     * @param node Nó atual
     * @param x Posição x do nó
     * @param y Posição y do nó
     * @param xOffset Deslocamento horizontal para os nós filhos
     */
    private void drawTree(Graphics2D g2d, MorseNode node, int x, int y, int xOffset) {
        if (node == null) {
            return;
        }

        // Determina a cor do nó com base na animação
        Color nodeColor;
        if (animationRunning && node == targetNode) {
            nodeColor = Color.GREEN; // Nó de destino (final)
        } else if (animationRunning && currentHighlightIndex >= 0 &&
                currentHighlightIndex < highlightPath.size() &&
                node == highlightPath.get(currentHighlightIndex)) {
            nodeColor = Color.RED; // Nó atual na animação
        } else {
            nodeColor = new Color(240, 240, 255); // Cor padrão
        }

        // Desenha o nó
        g2d.setColor(nodeColor);
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
        if (node.character != '\0' && node.morseCode != null && !node.morseCode.isEmpty()) {
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
            g2d.setColor(Color.BLACK);
            g2d.draw(new Line2D.Double(x, y + nodeSize / 2, leftX, nextY - nodeSize / 2));
            g2d.setFont(new Font("Arial", Font.BOLD, 44));
            g2d.setColor(Color.GREEN);
            g2d.drawString(".", (x + leftX) / 2 - 5, (y + nextY) / 2);
            drawTree(g2d, node.left, leftX, nextY, newXOffset);
        }

        if (node.right != null) {
            // Desenha a linha para o filho direito com "-" (traço)
            int rightX = x + newXOffset;
            g2d.setColor(Color.BLACK);
            g2d.draw(new Line2D.Double(x, y + nodeSize / 2, rightX, nextY - nodeSize / 2));
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            g2d.setColor(Color.RED);
            g2d.drawString("-", (x + rightX) / 2 - 5, (y + nextY) / 2);
            drawTree(g2d, node.right, rightX, nextY, newXOffset);
        }
    }

    /**
     * Inicia a animação de decodificação para um código morse
     * @param morseCode O código morse a ser decodificado
     * @param loop Indica se a animação deve repetir continuamente
     */
    public void animateDecode(String morseCode, boolean loop) {
        // Se já houver uma animação em andamento, pare-a
        if (animationRunning && animationTimer != null) {
            animationTimer.cancel();
        }

        // Encontra o caminho para o código morse
        highlightPath = new ArrayList<>();
        targetNode = findNodePath(root, morseCode.trim(), highlightPath);

        if (targetNode == null) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível decodificar: '" + morseCode + "'",
                    "Erro de Decodificação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Configura o loop da animação
        this.loopAnimation = loop;

        // Inicia a animação
        currentHighlightIndex = 0;
        animationRunning = true;

        // Array para armazenar a sequência de sons (. ou -)
        final char[] sequence = morseCode.trim().toCharArray();

        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Atualiza o índice do nó atual
                if (currentHighlightIndex < highlightPath.size()) {
                    // Toca o som correspondente ao caractere atual
                    if (currentHighlightIndex > 0 && currentHighlightIndex <= sequence.length) {
                        char symbol = sequence[currentHighlightIndex - 1];
                        if (symbol == '.') {
                            playDotSound();
                        } else if (symbol == '-') {
                            playDashSound();
                        }
                    }

                    repaint();
                    currentHighlightIndex++;
                } else {
                    // Animação concluída, mostra o nó de destino
                    currentHighlightIndex = -1;
                    repaint();

                    // Exibe uma mensagem informando o caractere decodificado
                    // (Apenas na primeira vez, não em loops)
                    if (targetNode.character != '\0' && !loopAnimation) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(AnimatedTreeVisualizer.this,
                                    "Caractere decodificado: '" + targetNode.character + "'",
                                    "Decodificação Concluída",
                                    JOptionPane.INFORMATION_MESSAGE);
                        });
                    }

                    // Se estiver em modo loop, reinicia a animação após um breve intervalo
                    if (loopAnimation) {
                        // Adiciona um pequeno delay para destacar o nó de destino


                        // Reinicia a animação
                        currentHighlightIndex = 0;
                        repaint();
                    } else {
                        // Para o timer se não estiver em loop
                        animationTimer.cancel();
                        animationRunning = false;
                    }
                }
            }
        }, animationDelay, animationDelay);
    }

    /**
     * Versão sobrecarregada para compatibilidade com código existente
     */
    public void animateDecode(String morseCode) {
        animateDecode(morseCode, false);
    }

    /**
     * Encontra o nó correspondente ao código morse e constrói o caminho
     * @param node O nó atual
     * @param morseCode O código morse a ser procurado
     * @param path Lista que armazenará o caminho percorrido
     * @return O nó correspondente ao código morse ou null se não encontrado
     */
    private MorseNode findNodePath(MorseNode node, String morseCode, List<MorseNode> path) {
        // Caso base: nó nulo
        if (node == null) {
            return null;
        }

        // Adiciona o nó atual ao caminho
        path.add(node);

        // Caso base: código morse vazio (chegamos ao destino)
        if (morseCode.isEmpty()) {
            return node;
        }

        // Processa o primeiro caractere do código morse
        char firstChar = morseCode.charAt(0);
        String remainingCode = morseCode.length() > 1 ? morseCode.substring(1) : "";

        if (firstChar == '.') {
            // Ponto: vá para o filho esquerdo
            MorseNode result = findNodePath(node.left, remainingCode, path);
            if (result != null) {
                return result;
            }
        } else if (firstChar == '-') {
            // Traço: vá para o filho direito
            MorseNode result = findNodePath(node.right, remainingCode, path);
            if (result != null) {
                return result;
            }
        } else {
            // Caractere inválido
            return null;
        }

        // Se chegou aqui, o caminho atual não leva ao nó desejado
        path.remove(path.size() - 1); // Remove o nó atual do caminho
        return null;
    }

    /**
     * Limpa qualquer animação em andamento
     */
    public void clearAnimation() {
        if (animationTimer != null) {
            animationTimer.cancel();
        }
        highlightPath.clear();
        currentHighlightIndex = -1;
        targetNode = null;
        animationRunning = false;
        loopAnimation = false;
        repaint();
    }

    /**
     * Ativa ou desativa o modo de loop da animação
     * @param loop true para ativar o loop, false para desativar
     */
    public void setLoopMode(boolean loop) {
        this.loopAnimation = loop;

        // Se já estiver em animação e o modo loop foi modificado para false, para a animação
        if (!loop && animationRunning && animationTimer != null) {
            animationTimer.cancel();
            animationRunning = false;
            repaint();
        }
    }

    /**
     * Verifica se a animação está em modo loop
     * @return true se estiver em modo loop, false caso contrário
     */
    public boolean isLoopMode() {
        return loopAnimation;
    }

    /**
     * Define a velocidade da animação
     * @param delay Tempo em milissegundos entre cada passo
     */
    public void setAnimationSpeed(int delay) {
        this.animationDelay = delay;
    }

    /**
     * Libera os recursos de áudio ao fechar o programa
     */
    public void dispose() {
        clearAnimation();
        if (animationTimer != null) {
            animationTimer.cancel();
            animationTimer = null;
        }

        if (dotSound != null) {
            dotSound.stop();      // <- garante que o áudio pare
            dotSound.close();
            dotSound = null;
        }
        if (dashSound != null) {
            dashSound.stop();
            dashSound.close();
            dashSound = null;
        }
    }
}