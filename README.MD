# Morse Code Visualizer

Somativo – Projeto **Java Swing**

> Um utilitário desktop para **codificar, decodificar e *visualizar*** o processo de tradução entre texto e Código Morse, incluindo animação passo‑a‑passo e efeitos sonoros.

---

## 💡 Funcionalidades

* **Codificação** de texto (A–Z, 0–9, espaço) para Código Morse.
* **Decodificação** de sequências Morse para texto legível.
* **Visualização estática** da árvore binária que representa o alfabeto Morse.
* **Visualização animada** do caminho percorrido na árvore durante a decodificação, com:

    * Destaque do nó corrente (vermelho) e do nó destino (verde).
    * Reproduções sonoras distintas para **ponto (·)** e **traço (—)**.
    * Controle de velocidade (`JSlider`), reinício/limpeza, e *loop* opcional.
* **Interface responsiva** redimensionável (Java Swing + `BorderLayout`, `GridLayout`, `JScrollPane`).

---

## 🚀 Tecnologias & Implementação

| Camada           | Tecnologias                                                  | Destaques                                                         |
| ---------------- | ------------------------------------------------------------ | ----------------------------------------------------------------- |
| **Linguagem**    | Java 17 +                                                    | Código 100 % em português, organizado em classes coesas.          |
| **GUI**          | **Swing** (`JFrame`, `JPanel`, `JSlider`, `JTextArea`, etc.) | Look‑and‑feel nativo (`UIManager.getSystemLookAndFeel`).          |
| **Árvore Morse** | Estruturas de dados próprias (`MorseNode`)                   | Inserção automática das 26 letras + 10 dígitos + espaço.          |
| **Áudio**        | `javax.sound.sampled`                                        | Tons gerados programaticamente para evitar dependências externas. |
| **Animação**     | `java.util.Timer`                                            | Atualização a cada *n* ms; fácil ajuste pelo slider.              |

**Principais classes**

| Classe                   | Responsabilidade                                                 |
| ------------------------ | ---------------------------------------------------------------- |
| `MorseDecoder`           | Encapsula a árvore e mapas de (des)codificação.                  |
| `AnimatedTreeVisualizer` | Renderiza e anima a árvore com áudio.                            |
| `TreeVisualizer`         | Versão estática da árvore (scroll infinito).                     |
| `MorseAppEnhanced`       | UI principal: troca texto ⇄ Morse, aciona visualizações.         |
| `Main`                   | *Bootstrap* para iniciar o aplicativo com o look‑and‑feel do SO. |

---

## 🛠️ Instalação & Execução

```bash
# 1 – Clone o repositório
$ git clone https://github.com/carloshobmeier/morse_code
$ cd morse‑visualizer

# 2 – Compile (requires JDK 17 +)
$ javac -encoding UTF-8 src/**/*.java -d out

# 3 – Execute
$ java -cp out Main
```

> **Nota:** nenhum framework externo é necessário. Todo o projeto utiliza apenas bibliotecas padrão da JDK.

---

## 🎬 Demonstração em Vídeo

Confira o fluxo completo no YouTube: **[Morse Code Visualizer – Demo](https://www.youtube.com/watch?v=RGeKQJkh_N4)**

---

## 👥 Equipe

* Amanda Queiroz
* Carlos Hobmeier
* João Vitor de Freitas


---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** – veja o arquivo [`LICENSE`](LICENSE) para detalhes.
