import java.util.Random;
import java.util.Scanner;

public class Main {

    final static String CARACTERES_IDENTIFICADORES_ACEITOS = "XOUC";
    final static int TAMANHO_TABULEIRO = 3;
    static char[][] tabuleiro = new char[TAMANHO_TABULEIRO][TAMANHO_TABULEIRO];
    static Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {

        inicializarTabuleiro();

        char caractereUsuario = obterCaractereUsuario();
        char caractereComputador = obterCaractereComputador(caractereUsuario);
        boolean vezUsuarioJogar = sortearValorBooleano();
        boolean jogoContinua = false;

        do {
            jogoContinua = true;
            exibirTabuleiro();

            if (vezUsuarioJogar) {
                processarVezUsuario(caractereUsuario);

                if (teveGanhador()) {
                    exibirTabuleiro();
                    exibirVitoriaUsuario();
                    jogoContinua = false;
                }
                vezUsuarioJogar = false;
            } else {
                processarVezComputador(caractereComputador);

                if (teveGanhador()) {
                    exibirTabuleiro();
                    exibirVitoriaComputador();
                    jogoContinua = false;
                }
                vezUsuarioJogar = true;
            }

            if (jogoContinua && teveEmpate()) {
                exibirTabuleiro();
                exibirEmpate();
                jogoContinua = false;
            }

        } while (jogoContinua);

        teclado.close();
    }

    // Inicializa o tabuleiro com espaços vazios.
    private static void inicializarTabuleiro() {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                tabuleiro[i][j] = ' ';
            }
        }
    }

    // Pega o caractere escolhido pelo usuário.
    private static char obterCaractereUsuario() {
        limparTela();
        while (true) {
            System.out.println();
            System.out.print("Escolha seu caractere (X, O, U ou C): ");

            String entrada = teclado.nextLine().trim();

            if (entrada.isEmpty()) {
                continue;
            }
            // Pega o primeiro caractere da entrada.
            char caractereUsuario = Character.toUpperCase(entrada.charAt(0));
            boolean caractereValido = false;
            // Verifica se o caractere é válido.
            for (char c : CARACTERES_IDENTIFICADORES_ACEITOS.toCharArray()) {
                if (caractereUsuario == Character.toUpperCase(c)) {
                    caractereValido = true;
                    break;
                }
            }
            // Se for válido, retorna o caractere.
            if (caractereValido) {
                return caractereUsuario;
            } else {
                System.out.println("Caractere inválido.");
            }
        }
    }

    // Pega o caractere escolhido pelo computador.
    private static char obterCaractereComputador(char caractereUsuario) {
        limparTela();
        while (true) {
            System.out.println();
            System.out.print("Escolha o caractere do computador(X, O, U ou C): ");

            String entrada = teclado.nextLine().trim();
            // Se a entrada estiver vazia, continua o loop.
            if (entrada.isEmpty()) {
                continue;
            }
            // Pega o primeiro caractere da entrada.
            char caractereComputador = Character.toUpperCase(entrada.charAt(0));
            boolean caractereValido = false;

            // Verifica se o caractere é válido.
            for (char c : CARACTERES_IDENTIFICADORES_ACEITOS.toCharArray()) {
                if (caractereComputador == Character.toUpperCase(c)) {
                    caractereValido = true;
                    break;
                }
            }
            // Se caractere for invalido ou igual ao do usuário, exibe mensagem de erro.
            if (caractereComputador == caractereUsuario) {
                System.out.println("Caractere do computador não pode ser igual ao do usuário.");
                continue;
            }
            // Se for válido, retorna o caractere.
            if (caractereValido) {
                return caractereComputador;
            } else {
                System.out.println("Caractere inválido.");
            }
        }
    }

    // Verifica se a jogada é válida.
    static boolean jogadaValida(String posicoesLivres, int linha, int coluna) {
        String posicao = linha + "" + coluna;
        return posicoesLivres.contains(posicao);
    }

    // Pega a jogada do usuário.
    static int[] obterJogadaUsuario(String posicoesLivres, Scanner teclado) {
        while (true) {
            System.out.print("Digite linha e a coluna (ex: 1 3): ");
            // Pega a entrada do usuário.
            String entrada = teclado.nextLine().trim();
            String[] partes = entrada.split("\\s+");
            // Verifica se a entrada tem dois valores.
            if (partes.length != 2) {
                System.out.println("Erro: digite dois valores separados por espaço.");
                continue;
            }
            int linha, coluna;
            // Tenta converter os valores para inteiros.
            try {
                linha = Integer.parseInt(partes[0]);
                coluna = Integer.parseInt(partes[1]);
            } catch (NumberFormatException e) {
                System.out.println("Erro: linha e coluna devem ser números.");
                continue;
            }
            // Ajusta os valores para o índice do array (0-2).
            linha--;
            coluna--;

            // Verifica se os valores estão dentro do intervalo válido.
            if (linha < 0 || linha >= TAMANHO_TABULEIRO || coluna < 0 || coluna >= TAMANHO_TABULEIRO) {
                System.out.println("Erro: linha e coluna devem estar entre 1 e " + TAMANHO_TABULEIRO + ".");
                continue;
            }
            // Verifica se a jogada é válida.
            if (!jogadaValida(posicoesLivres, linha, coluna)) {
                System.out.println("Jogada não permitida. Posição já ocupada.");
                continue;
            }
            return new int[] { linha, coluna };
        }
    }

    // Pega a jogada do computador.
    static int[] obterJogadaComputador(String posicoesLivres, Scanner teclado) {
        // Sorteia uma posição aleatória.
        Random random = new Random();
        int linha = random.nextInt(TAMANHO_TABULEIRO);
        int coluna = random.nextInt(TAMANHO_TABULEIRO);
        String posicao = linha + "" + coluna;
        // Garante que a posição sorteada esteja livre.
        while (!posicoesLivres.contains(posicao)) {
            linha = random.nextInt(TAMANHO_TABULEIRO);
            coluna = random.nextInt(TAMANHO_TABULEIRO);
            posicao = linha + "" + coluna;
        }
        return new int[] { linha, coluna };
    }

    // Converte a jogada de String para vetor de inteiros.
    static int[] converterJogadaStringParaVetorInt(String jogada) {
        int linha = Character.getNumericValue(jogada.charAt(0));
        int coluna = Character.getNumericValue(jogada.charAt(1));

        return new int[] { linha, coluna };
    }

    // Processa a vez do usuário.
    static void processarVezUsuario(char caractereUsuario) {
        System.out.println();
        System.out.println("SUA VEZ!");
        System.out.println();
        int[] jogada = obterJogadaUsuario(retornarPosicoesLivres(), teclado);
        atualizaTabuleiro(jogada, caractereUsuario);
    }

    // Processa a vez do computador.
    static void processarVezComputador(char caractereComputador) {
        int[] jogada = obterJogadaComputador(retornarPosicoesLivres(), teclado);
        atualizaTabuleiro(jogada, caractereComputador);
    }

    // Retorna as posições livres do tabuleiro.
    static String retornarPosicoesLivres() {
        StringBuilder builder = new StringBuilder();
        // Percorre o tabuleiro para encontrar posições livres.
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                if (tabuleiro[i][j] == ' ') {
                    builder.append(i).append(j).append(" ");
                }
            }
        }
        return builder.toString();
    }

    // Verifica se houve um ganhador.
    static boolean teveGanhador(char caractereJogador) {
        if (teveGanhadorLinha(caractereJogador) ||
                teveGanhadorColuna(caractereJogador) ||
                teveGanhadorDiagonalPrincipal(caractereJogador) ||
                teveGanhadorDiagonalSecundaria(caractereJogador)) {
            return true;
        }
        return false;
    }

    // Verifica se houve um ganhador em alguma linha.
    static boolean teveGanhadorLinha(char caractereJogador) {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            boolean linhaCompleta = true;
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                if (tabuleiro[i][j] != caractereJogador) {
                    linhaCompleta = false;
                    break;
                }
            }
            if (linhaCompleta) {
                return true;
            }
        }
        return false;
    }

    // Verifica se houve um ganhador em alguma coluna.
    static boolean teveGanhadorColuna(char caractereJogador) {
        for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
            boolean colunaCompleta = true;
            for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
                if (tabuleiro[i][j] != caractereJogador) {
                    colunaCompleta = false;
                    break;
                }
            }
            if (colunaCompleta) {
                return true;
            }
        }
        return false;
    }

    // Verifica se houve um ganhador na diagonal principal.
    static boolean teveGanhadorDiagonalPrincipal(char caractereJogador) {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            if (tabuleiro[i][i] != caractereJogador) {
                return false;
            }
        }
        return true;
    }

    // Verifica se houve um ganhador na diagonal secundária.
    static boolean teveGanhadorDiagonalSecundaria(char caractereJogador) {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            if (tabuleiro[i][TAMANHO_TABULEIRO - 1 - i] != caractereJogador) {
                return false;
            }
        }
        return true;
    }

    // Limpa a tela do console.
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Exibe o tabuleiro na tela.
    private static void exibirTabuleiro() {
        limparTela();
        StringBuilder builder = new StringBuilder();
        System.out.println("   1   2   3");

        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                System.out.print(" " + tabuleiro[i][j] + " ");

                if (j < TAMANHO_TABULEIRO - 1) {
                    System.out.print("|");
                }
                if (tabuleiro[i][j] == ' ') {
                    builder.append(i).append(j).append(" ");
                }
            }
            System.out.println();

            if (i < TAMANHO_TABULEIRO - 1) {
                System.out.println("  ---+---+---");
            }
        }
    }

    // Atualiza o tabuleiro com a jogada feita.
    static void atualizaTabuleiro(int[] jogada, char caractereJogador) {
        int linha = jogada[0];
        int coluna = jogada[1];

        if ((linha >= 0 && linha < TAMANHO_TABULEIRO) &&
                (coluna >= 0 && coluna < TAMANHO_TABULEIRO)) {
            tabuleiro[linha][coluna] = caractereJogador;
        } else {
            System.out.println("ERRO! Posição inválida.");
        }
    }

    // Exibe a mensagem de vitória do computador.
    static void exibirVitoriaComputador() {
        System.out.println();
        System.out.println("VITÓRIA DO COMPUTADOR!");
        System.out.println();
        System.out.println("   (o_o)");
        System.out.println("    /|\\  ");
        System.out.println("    / \\ ");
        System.out.println();
    }

    // Exibe a mensagem de vitória do usuário.
    private static void exibirVitoriaUsuario() {
        System.out.println();
        System.out.println("VOCÊ VENCEU!");
        System.out.println();
        System.out.println("   \\Õ/ ");
        System.out.println("    | ");
        System.out.println("   / \\ ");
        System.out.println();
    }

    // Exibe a mensagem de empate.
    private static void exibirEmpate() {
        System.out.println();
        System.out.println("  EMPATE!");
        System.out.println();
        System.out.println("¯\\_(o_o)_/¯");
        System.out.println();
    }

    // Verifica se houve empate.
    private static boolean teveEmpate() {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                if (tabuleiro[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    // Verifica se houve um ganhador no jogo.
    private static boolean teveGanhador() {
        for (char c : CARACTERES_IDENTIFICADORES_ACEITOS.toCharArray()) {
            if (teveGanhador(c)) {
                return true;
            }
        }
        return false;
    }

    // Sorteia um valor booleano aleatório.
    static boolean sortearValorBooleano() {
        Random random = new Random();
        return random.nextBoolean();
    }
}