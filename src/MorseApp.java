import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;


// Interface gráfica para a aplicação de código morse
public class MorseApp extends JFrame {
    private MorseDecoder decoder;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JRadioButton encodeOption;
    private JRadioButton decodeOption;
    private JButton processButton;
    private JButton viewTreeButton;

    // Construtor da aplicação
    public MorseApp() {
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

        buttonPanel.add(processButton);
        buttonPanel.add(viewTreeButton);
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
                        "\tA: .-\tJ: .---\tS: ...\t1: .----\n" +
                        "\tB: -...\tK: -.-\tT: -\t2: ..---\n" +
                        "\tC: -.-.\tL: .-..\tU: ..-\t3: ...--\n" +
                        "\tD: -..\tM: --\tV: ...-\t4: ....-\n" +
                        "\tE: .\tN: -.\tW: .--\t5: .....\n" +
                        "\tF: ..-.\tO: ---\tX: -..-\t6: -....\n" +
                        "\tG: --.\tP: .--.\tY: -.--\t7: --...\n" +
                        "\tH: ....\tQ: --.-\tZ: --..\t8: ---..\n" +
                        "\tI: ..\tR: .-.\t0: -----"
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

    // Processa a entrada do usuário baseado na opção selecionada
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

    // Exibe a visualização da árvore em uma nova janela

    private void showTreeVisualization() {
        JFrame treeFrame = new JFrame("Visualização da Árvore de Código Morse");
        treeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        TreeVisualizer visualizer = new TreeVisualizer(decoder.getRoot());
        JScrollPane scrollPane = new JScrollPane(visualizer);
        treeFrame.add(scrollPane);

        treeFrame.setSize(800, 600);
        treeFrame.setLocationRelativeTo(this);
        treeFrame.setVisible(true);
    }
}