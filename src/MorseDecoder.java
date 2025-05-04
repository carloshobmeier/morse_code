import java.util.HashMap;
import java.util.Map;

// Classe principal para o codificador/decodificador de código morse
public class MorseDecoder {
    private MorseNode root;
    private Map<Character, String> charToMorseMap;

    // Construtor que inicializa a árvore de morse
    public MorseDecoder() {
        // Inicializa a árvore com um nó raiz vazio
        root = new MorseNode();

        // Inicializa o mapa para codificação rápida
        charToMorseMap = new HashMap<>();

        // Popula a árvore com o padrão de código morse
        insertMorseCode('A', ".-");
        insertMorseCode('B', "-...");
        insertMorseCode('C', "-.-.");
        insertMorseCode('D', "-..");
        insertMorseCode('E', ".");
        insertMorseCode('F', "..-.");
        insertMorseCode('G', "--.");
        insertMorseCode('H', "....");
        insertMorseCode('I', "..");
        insertMorseCode('J', ".---");
        insertMorseCode('K', "-.-");
        insertMorseCode('L', ".-..");
        insertMorseCode('M', "--");
        insertMorseCode('N', "-.");
        insertMorseCode('O', "---");
        insertMorseCode('P', ".--.");
        insertMorseCode('Q', "--.-");
        insertMorseCode('R', ".-.");
        insertMorseCode('S', "...");
        insertMorseCode('T', "-");
        insertMorseCode('U', "..-");
        insertMorseCode('V', "...-");
        insertMorseCode('W', ".--");
        insertMorseCode('X', "-..-");
        insertMorseCode('Y', "-.--");
        insertMorseCode('Z', "--..");
        insertMorseCode('1', ".----");
        insertMorseCode('2', "..---");
        insertMorseCode('3', "...--");
        insertMorseCode('4', "....-");
        insertMorseCode('5', ".....");
        insertMorseCode('6', "-....");
        insertMorseCode('7', "--...");
        insertMorseCode('8', "---..");
        insertMorseCode('9', "----.");
        insertMorseCode('0', "-----");
        insertMorseCode(' ', "/");   // Espaço é codificado como "/"
    }

    // Insere um caractere na árvore com sua sequência morse
    // @param character O caractere a ser inserido
    // @param morseCode A sequência morse correspondente
    private void insertMorseCode(char character, String morseCode) {
        // Adiciona ao mapa para codificação rápida
        charToMorseMap.put(character, morseCode);

        MorseNode current = root;

        // Percorre a sequência morse para inserir o caractere no local correto
        for (int i = 0; i < morseCode.length(); i++) {
            char symbol = morseCode.charAt(i);

            if (symbol == '.') {  // Ponto vai para a esquerda
                if (current.left == null) {
                    current.left = new MorseNode();
                }
                current = current.left;
            } else if (symbol == '-') {  // Traço vai para a direita
                if (current.right == null) {
                    current.right = new MorseNode();
                }
                current = current.right;
            }
        }

        // Define o caractere no nó final
        current.character = character;
        current.morseCode = morseCode;
    }

    // Converte uma sequência morse em um caractere utilizando a árvore
    // @param sequence A sequência morse a ser convertida
    // @return O caractere correspondente à sequência morse
    public char morseToChar(String sequence) {
        return morseToChar(root, sequence, 0);
    }

    // Metodo recursivo auxiliar para converter morse em caractere
    // @param node O nó atual na árvore
    // @param sequence A sequência morse completa
    // @param i O índice atual na sequência
    // @return O caractere encontrado
    private char morseToChar(MorseNode node, String sequence, int i) {
        // Caso base: chegou ao final da sequência
        if (i == sequence.length()) {
            return node.character;
        }

        // Navega à esquerda para '.' (ponto)
        if (sequence.charAt(i) == '.') {
            return morseToChar(node.left, sequence, i + 1);
        } else if (sequence.charAt(i) == '-') {
        // Navega à direita para '-' (traço)
            return morseToChar(node.right, sequence, i + 1);
        }
        else {
            return '\0';
        }
    }

    // Decodifica uma string de código morse em texto
    // @param morseString A string de código morse com sequências separadas por espaço
    // @return O texto decodificado
    public String decodeMorse(String morseString) {
        StringBuilder decoded = new StringBuilder();
        String[] sequences = morseString.split(" ");

        for (String sequence : sequences) {
            if (sequence.equals("/")) {
                decoded.append(' ');  // '/' representa um espaço
            } else if (!sequence.isEmpty()) {
                decoded.append(morseToChar(sequence));
            }
        }

        return decoded.toString();
    }

    // Codifica texto normal em código morse
    // @param text O texto a ser codificado
    // @return A sequência morse correspondente
    public String encodeText(String text) {
        StringBuilder encoded = new StringBuilder();
        text = text.toUpperCase();  // Converte para maiúsculas para simplificar

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String morse = charToMorseMap.get(c);

            if (morse != null) {
                encoded.append(morse);
                // Adiciona espaço entre caracteres
                if (i < text.length() - 1) {
                    encoded.append(" ");
                }
            }
        }

        return encoded.toString();
    }

    // Codifica texto normal em código morse
    // @param text O texto a ser codificado
    // @return A sequência morse correspondente
    public String encodeTextNoMap(String text) {
        StringBuilder encoded = new StringBuilder();
        text = text.toUpperCase();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == ' ') {
                encoded.append("/");  // espaço vira “/”
            } else {
                String morse = getMorseFromTree(root, c, "");
                if (morse != null) encoded.append(morse);
            }

            if (i < text.length() - 1) encoded.append(" ");
        }
        return encoded.toString();
    }

    private String getMorseFromTree(MorseNode node, char target, String path) {
        if (node == null) return null;
        if (node.character == target) return path;

        String leftPath  = getMorseFromTree(node.left,  target, path + ".");
        if (leftPath != null) return leftPath;

        return getMorseFromTree(node.right, target, path + "-");
    }

    // Obtém a raiz da árvore morse
    // @return O nó raiz da árvore
    public MorseNode getRoot() {
        return root;
    }
}