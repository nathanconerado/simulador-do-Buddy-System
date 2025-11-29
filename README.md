Alocador de Memória Buddy Binário

Esse projeto simula um alocador de memória Buddy Binário, um método comum usado por sistemas operacionais para alocar e gerenciar blocos de memória. O código foi implementado do zero, com a limitação de usar apenas tipos primitivos e arrays simples.

Requisitos

Memória total: 4 MB (4.194.304 bytes)

Tamanho mínimo de bloco: 1 KB (1024 bytes)

Lógica de alocação: Buddy Binário (blocos de tamanhos em potências de 2, com divisão e fusão)

Estratégia de alocação: First Fit (Primeiro Encaixe)

Restrições:

Só foi permitido o uso de arrays simples e tipos primitivos (int[], char[], etc.)

Não foi usado nenhum recurso como ArrayList, HashMap, StringBuilder ou arrays multidimensionais.

Decisões de Projeto

A memória é representada por um array simples que simula uma árvore binária completa.

Os blocos de memória podem estar em 4 estados:

0 - Livre

1 - Ocupado

2 - Dividido

-1 - Inválido (após fusão)

O alocador usa uma lógica First Fit, ou seja, ele tenta alocar o primeiro bloco de memória que seja grande o suficiente para o programa.

Toda a estrutura e lógica do alocador foi implementada manualmente, incluindo os cálculos de nível, tamanho de bloco e posição na memória.

O programa gera um relatório detalhado com informações sobre:

Programas alocados (rótulo, tamanho real, bloco alocado, posição na memória)

Fragmentação da memória livre (blocos não alocados)

Como Compilar e Executar
Compilação

Primeiro, compile o arquivo AlocadorBudy.java com o seguinte comando no terminal:

javac AlocadorBudy.java

Execução

O programa espera um arquivo de entrada, que deve ser passado como argumento na linha de comando. O arquivo contém as informações sobre os programas a serem alocados.

Exemplo de execução:

java AlocadorBudy contador.txt

Formato do Arquivo de Entrada

O arquivo de entrada deve ser um arquivo de texto (.txt), onde cada linha contém um programa a ser alocado, no formato:

<letra> <tamanho_em_KB>


Exemplo:

A 512
B 1024
C 256

Demonstração

Aqui está um exemplo de como o programa pode se comportar ao ser executado com um arquivo de entrada. Abaixo está um relatório gerado após a execução:

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
F      | 1 KB             | 1 KB              | 4863          | 786432
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
76              | 6     | 64 KB            | 851968
26              | 4     | 256 KB           | 2883584
Total de Fragmentos Livres: 8

Vídeo 

https://youtu.be/7aF1R67yNFc

