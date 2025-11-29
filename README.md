Alocador de Memória Buddy Binário

Este projeto implementa um simulador completo do algoritmo Buddy System, utilizando apenas tipos primitivos e arrays simples, conforme a restrição proposta.

Ele gerencia uma memória de 4 MB realizando alocação, divisão de blocos, fusão de buddies e geração de relatório final.

📌 Requisitos de Implementação

Memória Total: 4 MB (4.194.304 bytes)

Tamanho Mínimo do Bloco: 1 KB (1024 bytes)

Lógica: Buddy Binário (blocos sempre em potência de 2, com divisão e fusão)

Estratégia de Alocação: First Fit

Restrições:

Apenas int[], char[] e arrays simples

Sem ArrayList, HashMap, StringBuilder, ou arrays multidimensionais

Uso de try/catch permitido apenas para leitura de arquivo

🛠️ Decisões de Projeto

A árvore de memória é representada como um array linear, simulando uma árvore binária completa.

Os estados possíveis de um bloco são:

0 – Livre

1 – Ocupado

2 – Dividido

-1 – Inválido (filhos após fusão)

Todos os cálculos de nível, tamanho de bloco e posição em memória foram implementados manualmente.

O relatório final exibe:

Programas alocados

Tamanho real vs tamanho do bloco Buddy

Índice da árvore

Offset dentro da memória

Blocos livres (fragmentação)

⚙️ Como Compilar e Executar
📍 Compilar

No terminal, dentro do diretório onde está o arquivo AlocadorBudy.java, execute:

javac AlocadorBudy.java

▶️ Executar

O programa espera o caminho do arquivo de entrada (.txt) como argumento.

Exemplo:

java AlocadorBudy contador.txt

📄 Formato do Arquivo de Entrada

O arquivo .txt deve conter uma linha por programa, no formato:

<letra> <tamanho_em_KB>


Exemplo:

A 512
B 1024
C 256

🖥️ Demonstração — Exemplo de Saída

A seguir, um exemplo real de saída gerada pelo programa:

--- Relatório Final do Alocador Budy ---
Memória Total: 4096 KB
Memória Livre Total: 383 KB

Programas Alocados:
Rótulo | Tam. Real (KB) | Tam. Bloco (KB) | Índice Árvore | Posição Memória (Byte)
---------------------------------------------------------------------------------
A      | 512 KB           | 512 KB            | 7             | 0
B      | 1024 KB           | 1024 KB            | 4             | 1048576
C      | 256 KB           | 256 KB            | 17             | 524288
D      | 300 KB           | 512 KB            | 11             | 2097152
F      | 1 KB           | 1 KB            | 4863             | 786432
H      | 1000 KB           | 1024 KB            | 6             | 3145728
I      | 150 KB           | 256 KB            | 25             | 2621440
L      | 128 KB           | 128 KB            | 38             | 917504

Espaços Livres Fragmentados:
Índice Árvore | Nível | Tam. Bloco (KB) | Posição Memória (Byte)
-----------------------------------------------------------------
4864             | 12     | 1 KB            | 787456
2432             | 11     | 2 KB            | 788480
1216             | 10     | 4 KB            | 790528
608             | 9     | 8 KB            | 794624
304             | 8     | 16 KB            | 802816
152             | 7     | 32 KB            | 819200
76             | 6     | 64 KB            | 851968
26             | 4     | 256 KB            | 2883584
Total de Fragmentos Livres: 8

🎥 Link do Vídeo (Simulação)

Vídeo no YouTube – Demonstração da Lógica

(Link ilustrativo — o vídeo real deve ser gravado pelos integrantes do grupo.)
