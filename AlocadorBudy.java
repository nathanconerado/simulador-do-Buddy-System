import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AlocadorBudy {

    // Constantes de Memória
    private static final int MEMORIA_TOTAL_BYTES = 4 * 1024 * 1024; // 4 MB
    private static final int MIN_BLOCO_BYTES = 1024; // 1 KB
    private static final int MAX_NIVEL = 12; // log2(4MB/1KB) = 12. Níveis de 0 a 12.

    // Estruturas de Dados do Alocador Budy
    // arvore: 0=Livre, 1=Ocupado, 2=Dividido, -1=Fundido/Inválido
    private int[] arvore;
    private int arvore_size;

    // Rastreamento de programas alocados
    private static final int MAX_PROGRAMAS = 100;
    private char[] rotulos;
    private int[] tamanhosReais;
    private int[] indicesArvore;
    private int numProgramasAlocados;

    private int memoriaLivreBytes;

    // Construtor
    public AlocadorBudy() { // Construtor  
        this.arvore_size = (1 << (MAX_NIVEL + 1)) - 1; // 8191
        this.arvore = new int[arvore_size];
        this.rotulos = new char[MAX_PROGRAMAS];
        this.tamanhosReais = new int[MAX_PROGRAMAS];
        this.indicesArvore = new int[MAX_PROGRAMAS];
        this.numProgramasAlocados = 0;
        this.memoriaLivreBytes = MEMORIA_TOTAL_BYTES;
        this.arvore[0] = 0; // Bloco raiz está LIVRE
    }

    /**
     * Aloca um programa na memória usando a lógica budy.  
     * @param rotulo O rótulo do programa.
     * @param tamanhoKB O tamanho do programa em KB.
     * @return true se a alocação foi bem-sucedida, false caso contrário.
     */
    public boolean alocar(char rotulo, int tamanhoKB) { // alocar  
        int tamanhoBytes = tamanhoKB * 1024;
        if (tamanhoBytes <= 0) return false;
        int tamanhoBudy = calcularTamanhoBudy(tamanhoBytes);
        int nivelDesejado = calcularNivel(tamanhoBudy);

        int indiceParaAlocar = encontrarBlocoLivreParaAlocacao(0, nivelDesejado);

        if (indiceParaAlocar == -1) {
            return false; // Não há bloco livre adequado
        }

        arvore[indiceParaAlocar] = 1; // Marcar como OCUPADO
        memoriaLivreBytes -= tamanhoBudy;

        if (numProgramasAlocados < MAX_PROGRAMAS) {
            rotulos[numProgramasAlocados] = rotulo;
            tamanhosReais[numProgramasAlocados] = tamanhoKB;
            indicesArvore[numProgramasAlocados] = indiceParaAlocar;
            numProgramasAlocados++;
            return true;
        } else {
            return false; // Limite de programas atingido
        }
    }

    /**
     * Encontra o índice do bloco livre (first fit) e divide no caminho.  
     * @param indiceAtual O índice do nó atual na árvore.
     * @param nivelDesejado O nível do bloco que queremos alocar.
     * @return O índice do bloco que será alocado ou -1 se não encontrado.
     */
    private int encontrarBlocoLivreParaAlocacao(int indiceAtual, int nivelDesejado) { // encontrarBlocoLivreParaAlocacao  
        int nivelAtual = calcularNivelDoIndice(indiceAtual);

        if (arvore[indiceAtual] == 1 || arvore[indiceAtual] == -1) {
            return -1; // Ocupado ou inválido
        }

        if (nivelAtual == nivelDesejado && arvore[indiceAtual] == 0) {
            return indiceAtual; // Encontrou bloco livre no nível exato
        }

        if (nivelAtual < nivelDesejado) {
            if (arvore[indiceAtual] == 0) { // Se o bloco está livre, divide
                arvore[indiceAtual] = 2; // Marcar como DIVIDIDO
                int filhoEsquerdo = 2 * indiceAtual + 1;
                int filhoDireito = 2 * indiceAtual + 2;
                if (filhoEsquerdo < arvore_size) arvore[filhoEsquerdo] = 0;
                if (filhoDireito < arvore_size) arvore[filhoDireito] = 0;
            }

            if (arvore[indiceAtual] == 2) { // Continua a busca nos filhos
                int filhoEsquerdo = 2 * indiceAtual + 1;
                if (filhoEsquerdo < arvore_size) {
                    int resultado = encontrarBlocoLivreParaAlocacao(filhoEsquerdo, nivelDesejado);
                    if (resultado != -1) return resultado;
                }

                int filhoDireito = 2 * indiceAtual + 2;
                if (filhoDireito < arvore_size) {
                    int resultado = encontrarBlocoLivreParaAlocacao(filhoDireito, nivelDesejado);
                    if (resultado != -1) return resultado;
                }
            }
        }

        return -1; // Não encontrado
    }

    /**
     * Libera um bloco de memória e tenta fundir com seu budy.  
     * @param rotulo O rótulo do programa a ser liberado.
     * @return true se a liberação foi bem-sucedida, false caso contrário.
     */
    public boolean liberar(char rotulo) { // liberar  
        int indicePrograma = -1;
        for (int i = 0; i < numProgramasAlocados; i++) {
            if (rotulos[i] == rotulo) {
                indicePrograma = i;
                break;
            }
        }

        if (indicePrograma == -1) return false; // Programa não encontrado

        int indiceBloco = indicesArvore[indicePrograma];
        int tamanhoBlocoBytes = calcularTamanhoDoNivel(calcularNivelDoIndice(indiceBloco));

        arvore[indiceBloco] = 0; // Marcar como LIVRE
        memoriaLivreBytes += tamanhoBlocoBytes;

        fusaoBudy(indiceBloco);

        // Remover programa da lista
        if (indicePrograma != numProgramasAlocados - 1) {
            rotulos[indicePrograma] = rotulos[numProgramasAlocados - 1];
            tamanhosReais[indicePrograma] = tamanhosReais[numProgramasAlocados - 1];
            indicesArvore[indicePrograma] = indicesArvore[numProgramasAlocados - 1];
        }
        numProgramasAlocados--;

        return true;
    }

    /**
     * Tenta fundir um bloco livre com seu budy (irmão).  
     * @param indiceBloco O índice do bloco que acabou de ser liberado.
     */
    private void fusaoBudy(int indiceBloco) { // fusaoBudy  
        if (indiceBloco == 0) return; // Raiz não pode ser fundida

        int indicePai = (indiceBloco - 1) / 2;
        int indiceBudy = (indiceBloco % 2 != 0) ? indiceBloco + 1 : indiceBloco - 1;

        if (indiceBudy < arvore_size && arvore[indiceBudy] == 0) {
            arvore[indicePai] = 0; // Fundir: pai se torna LIVRE
            arvore[indiceBloco] = -1; // Marcar filhos como inválidos
            arvore[indiceBudy] = -1;
            fusaoBudy(indicePai); // Tentar fundir o pai recursivamente
        }
    }

    // --- Métodos Auxiliares ---

    private int calcularTamanhoBudy(int tamanhoBytes) {
        int tamanho = MIN_BLOCO_BYTES;
        while (tamanho < tamanhoBytes) tamanho *= 2;
        return (tamanho > MEMORIA_TOTAL_BYTES) ? MEMORIA_TOTAL_BYTES : tamanho;
    }

    private int calcularNivel(int tamanhoBytes) {
        int nivel = 0;
        int tamanhoAtual = MEMORIA_TOTAL_BYTES;
        while (tamanhoAtual > tamanhoBytes) {
            tamanhoAtual /= 2;
            nivel++;
        }
        return nivel;
    }

    private int calcularNivelDoIndice(int indice) {
        int nivel = 0;
        while ((1 << nivel) - 1 <= indice) {
            nivel++;
        }
        return nivel - 1;
    }

    private int calcularTamanhoDoNivel(int nivel) {
        return MEMORIA_TOTAL_BYTES / (1 << nivel);
    }

    private int calcularOffset(int indice) {
        int nivel = calcularNivelDoIndice(indice);
        int tamanhoBloco = calcularTamanhoDoNivel(nivel);
        int primeiroIndiceNivel = (1 << nivel) - 1;
        int posicaoNoNivel = indice - primeiroIndiceNivel;
        return posicaoNoNivel * tamanhoBloco;
    }

    // --- Geração de Relatório ---

    public void gerarRelatorio() {
        System.out.println("\n--- Relatório Final do Alocador Budy ---");
        System.out.println("Memória Total: " + (MEMORIA_TOTAL_BYTES / 1024) + " KB");
        System.out.println("Memória Livre Total: " + (memoriaLivreBytes / 1024) + " KB");

        System.out.println("\nProgramas Alocados:");
        System.out.println("Rótulo | Tam. Real (KB) | Tam. Bloco (KB) | Índice Árvore | Posição Memória (Byte)");
        System.out.println("---------------------------------------------------------------------------------");
        for (int i = 0; i < numProgramasAlocados; i++) {
            int indice = indicesArvore[i];
            int nivel = calcularNivelDoIndice(indice);
            int tamanhoBlocoKB = calcularTamanhoDoNivel(nivel) / 1024;
            int offset = calcularOffset(indice);
            System.out.println(rotulos[i] + "      | " + tamanhosReais[i] + " KB           | " + tamanhoBlocoKB + " KB            | " + indice + "             | " + offset);
        }

        System.out.println("\nEspaços Livres Fragmentados:");
        System.out.println("Índice Árvore | Nível | Tam. Bloco (KB) | Posição Memória (Byte)");
        System.out.println("-----------------------------------------------------------------");
        int numFragmentos = percorrerParaFragmentos(0);
        System.out.println("Total de Fragmentos Livres: " + numFragmentos);
    }

    private int percorrerParaFragmentos(int indiceAtual) {
        if (indiceAtual >= arvore_size) return 0;

        if (arvore[indiceAtual] == 0) {
            int nivel = calcularNivelDoIndice(indiceAtual);
            int tamanhoBlocoKB = calcularTamanhoDoNivel(nivel) / 1024;
            int offset = calcularOffset(indiceAtual);
            System.out.println(indiceAtual + "             | " + nivel + "     | " + tamanhoBlocoKB + " KB            | " + offset);
            return 1;
        }

        if (arvore[indiceAtual] == 2) {
            int filhoEsquerdo = 2 * indiceAtual + 1;
            int filhoDireito = 2 * indiceAtual + 2;
            return percorrerParaFragmentos(filhoEsquerdo) + percorrerParaFragmentos(filhoDireito);
        }

        return 0;
    }

    // --- Funções de Suporte (Isoladas) ---

    public static String[] lerArquivoProgramas(String caminhoArquivo) {
        String[] linhas = new String[MAX_PROGRAMAS];
        int contador = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null && contador < MAX_PROGRAMAS) {
                linhas[contador++] = linha;
            }
        } catch (IOException e) {
            return null;
        }
        String[] resultado = new String[contador];
        for (int i = 0; i < contador; i++) {
            resultado[i] = linhas[i];
        }
        return resultado;
    }

    // --- Função Principal ---

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java AlocadorBudy <caminho_para_arquivo_txt>");
            return;
        }

        String[] programas = lerArquivoProgramas(args[0]);
        if (programas == null) {
            System.out.println("Erro ao ler o arquivo.");
            return;
        }

        AlocadorBudy alocador = new AlocadorBudy();

        for (int i = 0; i < programas.length; i++) {
            String linha = programas[i];
            if (linha == null) continue;
            String[] partes = linha.split(" ");
            if (partes.length == 2) {
                try {
                    char rotulo = partes[0].charAt(0);
                    int tamanhoKB = Integer.parseInt(partes[1]);
                    if (!alocador.alocar(rotulo, tamanhoKB)) {
                        System.out.println("Falha ao alocar programa " + rotulo + " (" + tamanhoKB + " KB).");
                    }
                } catch (NumberFormatException e) {
                    // Ignora linhas mal formatadas
                }
            }
        }

        alocador.gerarRelatorio();

        // Exemplo de liberação
        // System.out.println("\n--- Liberando programa A ---");
        // if (alocador.liberar('A')) {
        //     alocador.gerarRelatorio();
        // }
    }
}
