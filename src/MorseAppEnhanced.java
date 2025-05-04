import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

/**
 * Interface gráfica aprimorada para a aplicação de código morse
 * com visualização animada do processo de decodificação
 */
public class MorseAppEnhanced extends JFrame {
    private MorseDecoder decoder;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JRadioButton encodeOption;
    private JRadioButton decodeOption;
    private JButton processButton;
    private JButton viewTreeButton;
    private JButton visualizeDecodingButton;
    private JFrame treeFrame;
    private AnimatedTreeVisualizer visualizer;
    private TreeVisualizer visualizer2;

    private JSlider speedSlider;
    private JCheckBox loopCheckBox;

    /**
     * Construtor da aplicação
     */
    public MorseAppEnhanced() {
        decoder = new MorseDecoder();

        // Configuração da janela principal
        setTitle("Decodificador de Código Morse");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 615);
        setLocationRelativeTo(null); // Centraliza na tela

        // Painel principal com layout de borda
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Painel superior para opções
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        encodeOption = new JRadioButton("Texto para Morse");
        decodeOption = new JRadioButton("Morse para Texto");
        ButtonGroup options = new ButtonGroup();
        options.add(encodeOption);
        options.add(decodeOption);
        encodeOption.setSelected(true); // Opção padrão

        optionsPanel.add(new JLabel("Modo de operação:"));
        optionsPanel.add(encodeOption);
        optionsPanel.add(decodeOption);
        mainPanel.add(optionsPanel, BorderLayout.NORTH);

        // Painel central para entrada e saída
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 10));

        // Painel de entrada
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel("Entrada:"), BorderLayout.NORTH);
        inputTextArea = new JTextArea(5, 20);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputTextArea);
        inputPanel.add(inputScroll, BorderLayout.CENTER);

        // Painel de saída
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.add(new JLabel("Saída:"), BorderLayout.NORTH);
        outputTextArea = new JTextArea(5, 20);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputTextArea);
        outputPanel.add(outputScroll, BorderLayout.CENTER);

        centerPanel.add(inputPanel);
        centerPanel.add(outputPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        processButton = new JButton("Processar");
        viewTreeButton = new JButton("Visualizar Árvore");
        visualizeDecodingButton = new JButton("Visualizar Decodificação");

        buttonPanel.add(processButton);
        buttonPanel.add(viewTreeButton);
        buttonPanel.add(visualizeDecodingButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Adiciona ação ao botão de processamento
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processInput();
            }
        });

        // Adiciona ação ao botão de visualização da árvore
        viewTreeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTreeVisualization();
            }
        });

        // Adiciona ação ao botão de visualização da decodificação
        visualizeDecodingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizeDecoding();
            }
        });

        // Dicas de uso
        JPanel helpPanel = new JPanel(new BorderLayout());
        helpPanel.setPreferredSize(new Dimension(415, 400));
        JTextArea helpText = new JTextArea(
                "Dicas:\n\n" +
                        "- Ao converter de TEXTO para MORSE, use:\n \t letras, números e espaços.\n \n" +
                        "- Ao converter de MORSE para TEXTO, use: \n \t pontos (.) e traços (-) \n \t espaços ( ) para separar letras.\n" +
                        "\t barra (/) para espaços entre palavras.\n\n" +
                        "Exemplo de código Morse (SOS): \n \t ... --- ... \n \n" +
                        "Alfabeto Morse:\n" +
                        "\tA: .-\tJ: .---\tS: ...\n" +
                        "\tB: -...\tK: -.-\tT: -\n" +
                        "\tC: -.-.\tL: .-..\tU: ..-\n" +
                        "\tD: -..\tM: --\tV: ...-\n" +
                        "\tE: .\tN: -.\tW: .--\n" +
                        "\tF: ..-.\tO: ---\tX: -..-\n" +
                        "\tG: --.\tP: .--.\tY: -.--\n" +
                        "\tH: ....\tQ: --.-\tZ: --..\n" +
                        "\tI: ..\tR: .-.\n\n" +
                        "Números:\n" +
                        "\t1: .----\t6: -....\n" +
                        "\t2: ..---\t7: --...\n" +
                        "\t3: ...--\t8: ---..\n" +
                        "\t4: ....-\t9: ----.\n" +
                        "\t5: .....\t0: -----\n\n" +
                        "Visualizar Decodificação: \n" +
                        "- Digite um código Morse (como \".-\") \n" +
                        "- Use o botão 'Visualizar Decodificação' \n" +
                        "- Observe a animação na árvore"
        );
        helpText.setEditable(false);
        helpText.setBackground(new Color(240, 240, 240));
        helpText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Ajuda"),
                new EmptyBorder(5, 5, 5, 5)
        ));
        helpPanel.add(helpText, BorderLayout.CENTER);
        mainPanel.add(helpPanel, BorderLayout.EAST);

        // Adiciona o painel principal à janela
        add(mainPanel);
        setVisible(true);
    }

    /**
     * Processa a entrada do usuário baseado na opção selecionada
     */
    private void processInput() {
        String input = inputTextArea.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um texto para processar.",
                    "Entrada vazia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (encodeOption.isSelected()) {
            // Converte texto para código morse
            String morseCode = decoder.encodeText(input);
            outputTextArea.setText(morseCode);
        } else {
            // Converte código morse para texto
            String text = decoder.decodeMorse(input);
            outputTextArea.setText(text);
        }
    }

    /**
     * Exibe a visualização estática da árvore em uma nova janela
     */
    private void showTreeVisualization() {
        if (treeFrame != null && treeFrame.isVisible()) {
            treeFrame.requestFocus();
            return;
        }

        treeFrame = new JFrame("Visualização da Árvore de Código Morse");
        treeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        visualizer2 = new TreeVisualizer(decoder.getRoot());
        JScrollPane scrollPane = new JScrollPane(visualizer2);

        treeFrame.add(scrollPane, BorderLayout.CENTER);
        treeFrame.setSize(800, 600);
        treeFrame.setLocationRelativeTo(this);
        treeFrame.setVisible(true);
    }

    /**
     * Visualiza o processo de decodificação de um código morse
     */
    private void visualizeDecoding() {
        String input = inputTextArea.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um código Morse para visualizar.",
                    "Entrada vazia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verifica se a entrada contém apenas pontos e traços (código morse simples)
        if (!input.matches("^[.-]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Para visualizar a decodificação, insira apenas um código Morse simples (apenas pontos e traços).\n" +
                            "Exemplo: .- (A), ... (S), etc.",
                    "Formato inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (treeFrame != null && treeFrame.isVisible()) {
            treeFrame.requestFocus();
            return;
        }

        treeFrame = new JFrame("Visualização da Árvore de Código Morse");
        treeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.treeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        visualizer = new AnimatedTreeVisualizer(decoder.getRoot());
        JScrollPane scrollPane = new JScrollPane(visualizer);

// 3 ─ pare timer + áudio quando a janela for fechada
        this.treeFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                visualizer.dispose();        // pára sons, cancela Timer
            }
            // cobre o caso de ela já ter sido fechada por outro motivo
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                visualizer.dispose();
            }
        });
        // Usar AnimatedTreeVisualizer em vez de TreeVisualizer

        // Adiciona controle de velocidade de animação
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel speedLabel = new JLabel("Velocidade de Animação: ");
        speedSlider = new JSlider(JSlider.HORIZONTAL, 100, 500, 100);
        speedSlider.setInverted(true); // Inverte para que valores menores sejam mais rápidos
        speedSlider.setMajorTickSpacing(200);
        speedSlider.setMinorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        speedSlider.addChangeListener(e -> {
            if (!speedSlider.getValueIsAdjusting()) {
                visualizer.setAnimationSpeed(speedSlider.getValue());
            }
        });

        sliderPanel.add(speedLabel);
        sliderPanel.add(speedSlider);

        // Checkboxes e botões de controle
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Botão para limpar a animação
        JButton clearButton = new JButton("Limpar Animação");
        clearButton.addActionListener(e -> visualizer.clearAnimation());

        buttonPanel.add(clearButton);

        controlPanel.add(sliderPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.EAST);

        treeFrame.add(scrollPane, BorderLayout.CENTER);
        treeFrame.add(controlPanel, BorderLayout.SOUTH);

        treeFrame.setSize(800, 600);
        treeFrame.setLocationRelativeTo(this);
        treeFrame.setVisible(true);
        // Depois de treeFrame.setVisible(true)
        SwingUtilities.invokeLater(() -> {
            JViewport vp   = scrollPane.getViewport();
            Dimension vsz  = vp.getExtentSize();                 // tamanho visível
            int xRoot      = visualizer.getPreferredSize().width / 2;  // 1200
            int targetX    = xRoot - vsz.width / 2;              // raiz no centro
            targetX        = Math.max(0, targetX);               // evita negativo
            vp.setViewPosition(new Point(targetX, 0));           // rola até lá
        });
        // Inicia a animação com o código morse da entrada (com loop ativado)
        visualizer.animateDecode(input, true);
    }

    /**
     * Método principal para iniciar a aplicação
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MorseAppEnhanced();
            }
        });
    }
}