import java.util.Random;
import java.util.Scanner;

public class Main {

    final static String CARACTERES_IDENTIFICADORES_ACEITOS = "XOUC";
    final static int TAMANHO_TABULEIRO = 3;
    static char[][] tabuleiro = new char[TAMANHO_TABULEIRO][TAMANHO_TABULEIRO];
    static Scanner teclado = new Scanner(System.in);
    static String posicoesLivres;

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
                int[] jogadaUsuario = obterJogadaUsuario(posicoesLivres, teclado);
                atualizaTabuleiro(jogadaUsuario, caractereUsuario);

                if (teveGanhador(caractereUsuario)) {
                    exibirTabuleiro();
                    exibirVitoriaUsuario();
                    jogoContinua = false;
                }
                vezUsuarioJogar = false;
            } else {
                int[] jogadaComputador = obterJogadaComputador(posicoesLivres, teclado);
                atualizaTabuleiro(jogadaComputador, caractereComputador);

                if (teveGanhador(caractereComputador)) {
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

    private static void inicializarTabuleiro() {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                tabuleiro[i][j] = ' ';
            }
        }
    }

    private static char obterCaractereUsuario() {

        while (true) {
            limparTela();
            System.out.println();
            System.out.println("Digite o caractere para ser representado. Utilize uma dessas 4 letras: X, O, U, C");

            String entrada = teclado.nextLine().trim();

            if (entrada.isEmpty()) {
                continue;
            }

            char caractereUsuario = Character.toUpperCase(entrada.charAt(0));

            for (char c : CARACTERES_IDENTIFICADORES_ACEITOS.toCharArray()) {
                if (caractereUsuario == Character.toUpperCase(c)) {
                    return c;
                }
            }
        }
    }

    private static char obterCaractereComputador(char caractereUsuario) {
        caractereUsuario = Character.toUpperCase(caractereUsuario);
        switch (caractereUsuario) {
            case 'X':
                return 'O';
            case 'O':
                return 'X';
            case 'U':
                return 'C';
            case 'C':
                return 'U';
        }
        return ' ';
    }

    static boolean jogadaValida(String posicoesLivres, int linha, int coluna) {
        String posicao = linha + "" + coluna;
        return posicoesLivres.contains(posicao);
    }

    static int[] obterJogadaUsuario(String posicoesLivres, Scanner teclado) {
        while (true) {
            System.out.print("Digite a linha e a coluna (ex: 1 3): ");

            String entrada = teclado.nextLine().trim();
            String[] partes = entrada.split("\\s+");

            if (partes.length != 2) {
                System.out.println("Erro: digite dois valores separados por espaço.");
                continue;
            }

            int linha, coluna;

            try {
                linha = Integer.parseInt(partes[0]);
                coluna = Integer.parseInt(partes[1]);
            } catch (NumberFormatException e) {
                System.out.println("Erro: linha e coluna devem ser números.");
                continue;
            }

            linha--;
            coluna--;

            if (linha < 0 || linha >= TAMANHO_TABULEIRO || coluna < 0 || coluna >= TAMANHO_TABULEIRO) {
                System.out.println("Erro: linha e coluna devem estar entre 1 e " + TAMANHO_TABULEIRO + ".");
                continue;
            }

            if (!jogadaValida(posicoesLivres, linha, coluna)) {
                System.out.println("Jogada não permitida. Posição já ocupada.");
                continue;
            }
            return new int[] { linha, coluna };
        }
    }

    static int[] obterJogadaComputador(String posicoesLivres, Scanner teclado) {
        Random random = new Random();
        int linha = random.nextInt(TAMANHO_TABULEIRO);
        int coluna = random.nextInt(TAMANHO_TABULEIRO);
        String posicao = linha + "" + coluna;
        while (!posicoesLivres.contains(posicao)) {
            linha = random.nextInt(TAMANHO_TABULEIRO);
            coluna = random.nextInt(TAMANHO_TABULEIRO);
            posicao = linha + "" + coluna;
        }
        return new int[] { linha, coluna };
    }

    static int[] converterJogadaStringParaVetorInt(String jogada) {
        int linha = Character.getNumericValue(jogada.charAt(0));
        int coluna = Character.getNumericValue(jogada.charAt(1));

        return new int[] { linha, coluna };
    }

    static void processarVezUsuario(char caractereUsuario) {
        posicoesLivres = retornarPosicoesLivres();
        int[] jogada = obterJogadaUsuario(posicoesLivres, teclado);
        atualizaTabuleiro(jogada, caractereUsuario);
    }

    static void processarVezComputador(char caractereComputador) {
        posicoesLivres = retornarPosicoesLivres();
        int[] jogada = obterJogadaComputador(posicoesLivres, teclado);
        atualizaTabuleiro(jogada, caractereComputador);
    }

    static String retornarPosicoesLivres() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                if (tabuleiro[i][j] == ' ') {
                    builder.append(i).append(j).append(" ");
                }
            }
        }
        return builder.toString();
    }


    static boolean teveGanhador(char caractereJogador) {
        if (teveGanhadorLinha(caractereJogador) ||
                teveGanhadorColuna(caractereJogador) ||
                teveGanhadorDiagonalPrincipal(caractereJogador) ||
                teveGanhadorDiagonalSecundaria(caractereJogador)) {
            return true;
        }
        return false;
    }

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

    static boolean teveGanhadorDiagonalPrincipal(char caractereJogador) {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            if (tabuleiro[i][i] != caractereJogador) {
                return false;
            }
        }
        return true;
    }

    static boolean teveGanhadorDiagonalSecundaria(char caractereJogador) {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            if (tabuleiro[i][TAMANHO_TABULEIRO - 1 - i] != caractereJogador) {
                return false;
            }
        }
        return true;
    }

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void exibirTabuleiro() {
        limparTela();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
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

                System.out.println("---+---+---");
            }
        }

        posicoesLivres = builder.toString();
    }

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

    static void exibirVitoriaComputador() {
        System.out.println();
        System.out.println("VOCÊ PERDEU!");
        System.out.println();
        System.out.println("(o_o)");
        System.out.println(" /|\\  ");
        System.out.println(" / \\ ");
    }

    private static void exibirVitoriaUsuario() {
        System.out.println();
        System.out.println("VOCÊ VENCEU!");
        System.out.println();
        System.out.println(" \\Õ/ ");
        System.out.println("  | ");
        System.out.println(" / \\ ");
    }

    private static void exibirEmpate() {
        System.out.println();
        System.out.println("  EMPATE!");
        System.out.println();
        System.out.println("¯\\_(o_o)_/¯");
        System.out.println();
    }

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

    private static boolean teveGanhador() {
        for (char c : CARACTERES_IDENTIFICADORES_ACEITOS.toCharArray()) {
            if (teveGanhador(c)) {
                return true;
            }
        }
        return false;
    }

    static boolean sortearValorBooleano() {
        Random random = new Random();
        return random.nextBoolean();
    }
}
