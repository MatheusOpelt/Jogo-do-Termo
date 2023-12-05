import java.io.*;
import java.util.*;

public class JogoDoTermo {

    public static void main(String[] args) throws IOException {
        Jogo jogo = new Jogo();
        jogo.iniciar();
    }
}

class Jogo {

    private static final String PALAVRAS_ARQUIVO = "src/palavras.txt";
    private static final int NUMERO_CARACTERES = 5;
    private static final int NUMERO_TENTATIVAS = 7;

    private List<String> listaPalavras;
    private Scanner scanner;
    private JogoTermo jogoTermo;

    public Jogo() {
        this.listaPalavras = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.jogoTermo = new JogoTermo();
    }

    public void iniciar() throws IOException {
        carregarPalavras();

        String palavraAleatoria = jogoTermo.gerarPalavraAleatoria(listaPalavras);
        String palavra = jogoTermo.formatarPalavra(palavraAleatoria);

        jogoTermo.exibirInicio();
        jogoTermo.exibirIntegrantes();
        jogoTermo.exibirApresentacao(NUMERO_TENTATIVAS);

        jogar(palavra);
    }

    private void carregarPalavras() throws IOException {
        File arquivo = new File(PALAVRAS_ARQUIVO);
        FileReader fr = new FileReader(arquivo);
        BufferedReader br = new BufferedReader(fr);
        jogoTermo.filtrarPalavras(br, listaPalavras, NUMERO_CARACTERES);
        br.close();
    }

    private void jogar(String palavra) {
        int tentativas = NUMERO_TENTATIVAS;

        while (tentativas > 0) {
            boolean acerto = true;

            String usuarioPalavra = null;
            while (acerto) {
                jogoTermo.pedirPalavra(NUMERO_CARACTERES);
                usuarioPalavra = scanner.nextLine().toUpperCase();
                Map<Integer, Boolean> resultadoVerificacao = jogoTermo.verificarTamanho(usuarioPalavra, tentativas);

                for (Map.Entry<Integer, Boolean> entry : resultadoVerificacao.entrySet()) {
                    tentativas = entry.getKey();
                    acerto = entry.getValue();
                }
            }

            boolean resultado = jogoTermo.verificarPalavra(usuarioPalavra, palavra, acerto);
            Map<Boolean, String[]> resultadoFinal = jogoTermo.verificarLetras(usuarioPalavra, palavra);

            boolean resultadoBoolean = false;
            String[] resultadoLista = new String[0];

            for (Map.Entry<Boolean, String[]> entry : resultadoFinal.entrySet()) {
                resultadoBoolean = entry.getKey();
                resultadoLista = entry.getValue();
            }

            jogoTermo.exibirResultado(resultadoLista, usuarioPalavra);

            if (resultadoBoolean || tentativas == 0) {
                jogoTermo.exibirFimDoJogo(resultado);
                break;
            } else {
                acerto = true;
            }

            jogoTermo.exibirTentativas(tentativas);
        }
    }
}

class JogoTermo {

    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";

    private String[] cores;

    public JogoTermo() {
        this.cores = new String[]{ANSI_GREEN, ANSI_YELLOW, ANSI_RED, ANSI_RESET};
    }

    public void exibirInicio() {
        System.out.println("Bem-vindo ao Jogo do Termo!");
    }

    public void exibirIntegrantes() {
        System.out.println("Integrantes:");
        System.out.println("1. Matheus Eduardo Opelt");
        System.out.println("2. Giovanna Campos Cordeiro");
    }

    public void exibirApresentacao(int tentativas) {
        System.out.println("Você terá " + tentativas + " tentativas para adivinhar uma palavra de 5 letras.");
        System.out.println("Cada letra que você acertar será exibida na palavra.");
        System.out.println("Se você adivinhar a palavra corretamente, você vence o jogo.");
        System.out.println("Se esgotar suas tentativas, você perde o jogo.");
    }

    public void pedirPalavra(int caracteres) {
        System.out.println("Digite uma palavra de " + caracteres + " letras: ");
    }

    public void exibirResultado(String[] resultado, String palavraUsuario) {
        System.out.print("Resultado: ");
        for (String letra : resultado) {
            System.out.print(letra + " ");
        }
        System.out.println("Palavra digitada: " + palavraUsuario);
    }

    public void exibirFimDoJogo(boolean resultadoFinal) {
        if (resultadoFinal) {
            System.out.println("Parabéns! Você venceu!");
        } else {
            System.out.println("Infelizmente, você perdeu. Tente novamente.");
        }
    }

    public void exibirTentativas(int tentativas) {
        System.out.println("Tentativas restantes: " + tentativas);
    }

    public void filtrarPalavras(BufferedReader br, List<String> lista, int caracteres) throws IOException {
        String linha;
        while ((linha = br.readLine()) != null) {
            if (linha.length() == caracteres) {
                lista.add(linha);
            }
        }
    }

    public String gerarPalavraAleatoria(List<String> lista) {
        Random rand = new Random();
        return lista.get(rand.nextInt(lista.size()));
    }

    public String formatarPalavra(String palavra) {
        return palavra.toUpperCase();
    }

    public Map<Integer, Boolean> verificarTamanho(String entradaUsuario, int tentativas) {
        Map<Integer, Boolean> resultado = new HashMap<>();
        if (entradaUsuario.length() != 5) {
            System.out.println("A palavra deve ter 5 letras. Tente novamente.");
            tentativas--;
            resultado.put(tentativas, true);
        } else {
            resultado.put(tentativas, false);
        }
        return resultado;
    }

    public boolean verificarPalavra(String entradaUsuario, String palavra, boolean acerto) {
        boolean resultado = palavra.equals(entradaUsuario);
        if (resultado) {
            System.out.println("Parabéns! Você acertou a palavra!");
        }
        return resultado;
    }

    public Map<Boolean, String[]> verificarLetras(String entradaUsuario, String palavra) {
        Map<Boolean, String[]> resultadoFinal = new HashMap<>();
        char[] palavraArray = palavra.toCharArray();
        char[] entradaArray = entradaUsuario.toCharArray();
        String[] resultado = new String[palavraArray.length];

        for (int i = 0; i < palavraArray.length; i++) {
            if (palavraArray[i] == entradaArray[i]) {
                resultado[i] = String.valueOf(entradaArray[i]);
            } else {
                resultado[i] = "_";
            }
        }

        boolean resultadoBoolean = Arrays.equals(palavraArray, entradaArray);
        resultadoFinal.put(resultadoBoolean, resultado);
        return resultadoFinal;
    }
}
