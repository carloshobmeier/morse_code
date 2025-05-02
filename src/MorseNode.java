// Nó da árvore binária para o código morse
public class MorseNode {
    char character;
    MorseNode left;  // Representa o ponto (.)
    MorseNode right; // Representa o traço (-)
    String morseCode; // Código morse associado ao caractere

    // Construtor para criar um nó folha com um caractere
    // @param character O caractere armazenado no nó
    public MorseNode(char character) {
        this.character = character;
        this.left = null;
        this.right = null;
        this.morseCode = "";
    }

    // Construtor para criar um nó folha com um caractere e seu código morse
    // @param character O caractere armazenado no nó
    // @param morseCode O código morse associado ao caractere
    public MorseNode(char character, String morseCode) {
        this.character = character;
        this.left = null;
        this.right = null;
        this.morseCode = morseCode;
    }

    // Construtor para criar um nó interno (sem caractere definido)
    public MorseNode() {
        this.character = '\0';
        this.left = null;
        this.right = null;
        this.morseCode = "";
    }
}