<h1 align="center" style="font-weight: bold">PERCOLAÇÃO COM DISJOINT-SET UNION (DSU)
    <img src="https://fonts.gstatic.com/s/e/notoemoji/latest/1f30a/512.gif" alt="Ocean emoji" width="32" height="32">
</h1>

<p align="center">
    <a href="#visão-geral">Visão Geral</a> • 
    <a href="#fundamentos-teóricos">Fundamentos Teóricos</a> • 
    <a href="#tecnologias-e-técnicas-utilizadas">Tecnologias e Técnicas Utilizadas</a> • 
    <a href="#estrutura-do-projeto">Estrutura do Projeto</a> • 
    <br>
    <a href="#executando-o-projeto">Executando o Projeto</a> • 
    <a href="#detalhes-de-implementação">Detalhes de Implementação</a> • 
    <a href="#interface-gráfica">Interface Gráfica</a> • 
    <a href="#contribuir-para-o-projeto">Contribuir para o projeto</a>
</p>

<br>

<div align="center">
    <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">
    <img src="https://img.shields.io/badge/swing-%23000000.svg?style=for-the-badge&logo=openjdk&logoColor=white">
    <img src="https://custom-icon-badges.demolab.com/badge/algs4-%23EBE8E2.svg?style=for-the-badge&logo=jar">
    <img src="https://custom-icon-badges.demolab.com/badge/union--find-%232C3E50.svg?style=for-the-badge&logo=graph-dsa&logoColor=white">
    <img src="https://custom-icon-badges.demolab.com/badge/monte%20carlo%20simulation-grey.svg?style=for-the-badge&logo=graph">
</div>

<!-- INSERIR O VÍDEO DE DEMONSTRAÇÃO IGUAL NVIM-JAVA -->

<br>

## VISÃO GERAL

Este projeto implementa um **sistema de percolação** utilizando a estrutura **Disjoint-Set Union (DSU)** para modelar conectividade dinâmica em conjuntos de dados. O código foi desenvolvido como solução para o exercício de percolação do curso **Algorithms, Part I** da **Universidade de Princeton**, respeitando integralmente a metodologia, requisitos e [especificações propostas pelo exercício](https://coursera.cs.princeton.edu/algs4/assignments/percolation/specification.php).

Além das classes principais exigidas pelo curso (`Percolation` e `PercolationStats`), o projeto inclui uma **interface gráfica interativa** construída com **Java Swing**, que permite visualizar o processo de percolação, abrir sites com cliques do mouse e executar a simulação de Monte Carlo em tempo real.

O sistema resolve o clássico **problema de backwash** através de duas estruturas union‑find independentes e estima o limiar de percolação com intervalos de confiança de 95% usando o método de Monte Carlo.

<br>

## FUNDAMENTOS TEÓRICOS

### Percolação

Percolação é um modelo da física estatística que estuda a **conectividade em meios aleatórios**, possuindo aplicações em sistemas físicos que dependem de conexões entre **pontos isolados** ou de **difícil fluxo**. No modelo de grade N×N, cada **ponto** (também chamado de **site** ou **célula**) pode estar **bloqueado** ou **aberto**. Dizemos que o sistema **percola** quando existe um caminho de pontos abertos **conectando o topo à base** da grade.

O fenômeno exibe uma transição de fase: abaixo de uma densidade crítica de sites abertos (o **limiar de percolação**), a probabilidade de percolar é praticamente zero; acima dela, a probabilidade se aproxima de 1. Estimar esse limiar é o objetivo central do exercício.

### Disjoint‑Set Union (Union‑Find)

A estrutura DSU gerencia um conjunto de elementos particionados em subconjuntos disjuntos, podendo ser implementada utilizando **árvores**, **listas encadeadas**, dentre outras estruturas. Em sua implementação com árvores, suporta duas principais operações:

- **Union(p, q):** conecta os dois elementos, unindo seus conjuntos.
- **Find( p ):** retorna o nó identificador (raiz) do conjunto ao qual p pertence.

Nesse projeto, a implementação escolhida foi a utilização de **árvores balanceadas**, tratando-se de uma das mais eficientes. Através da classe `WeightedQuickUnionUF`, é possível: empregar **união por peso** (sempre anexa a árvore menor à maior) e **compressão de caminho** (achata a árvore durante as buscas), resultando em um algoritmo com **complexidade de tempo quase constante amortizada** ou **$\mathcal{O}(\alpha(n))$** (função inversa de Ackermann).

Além disso, deve se levar em consideração que, para um sistema percolar, deve haver um caminho aberto que conecte o topo à base do sistema. Entretanto, verificar diretamente a conexão entre cada site seria **força bruta** e **não-escalável**. Para contornar isso, dois **nós virtuais** são adicionados: um no topo (`vTop`) e um na base (`vBottom`). Todos os sites da primeira linha são unidos ao **nó virtual do topo**; já os sites da última linha, são unidos ao **nó virtual da base**. Desta forma, para percolar com sucesso, é necessário que **apenas os nós do topo e da base se conectem**, ou seja, `find(vTop) == find(vBottom)`.

### Backwash

Mesmo com essa abordagem, ainda pode existir uma cilada: quando um único DSU é usado, sites abertos que **não** estão conectados ao topo podem ser erroneamente marcados como "cheios" (full) se estiverem conectados ao fundo, ocasionando a percolação por meio de falsos positivos. Esse efeito indesejado é chamado de **backwash**.

A solução implementada nesse projeto consiste em manter **duas estruturas DSU**:

- `uf` – conecta topo e base, usado para verificar se o sistema **percola com sucesso** (`percolates()`).
- `backwash` – conecta **apenas o topo virtual**, usada exclusivamente no método `isFull()`. Dessa forma, um site `i` só é considerado cheio se realmente estiver conectado ao topo virtual. Isso é verificado comparando o **seu nó raíz com o topo virtual** em `backwash.find(i) == backwash.find(vTop)`. Isso garante que apenas sites efetivamente conectados ao topo sejam marcados como cheios, eliminando o backwash.

### Simulação de Monte Carlo e Limiar Estatístico

Para estimar o limiar de percolação, realizamos múltiplos experimentos independentes: abrimos sites aleatoriamente até o sistema percolar e registramos a fração de sites abertos no momento da percolação. Com base em *T* trials, calculamos:

- **Média amostral** (x̄)
- **Desvio padrão amostral** (*s*)
- **Intervalo de confiança de 95%**: IC = x̄ ± 1.96 · (s / √T)

O valor 1.96 corresponde ao quantil da distribuição normal para 95% de confiança. Para *T* = 1, o desvio padrão é indefinido e retornamos `Double.NaN`.

<br>

## TECNOLOGIAS E TÉCNICAS UTILIZADAS

- **Java 8+** (compatível com a versão utilizada no curso)
- **Biblioteca algs4.jar** – fornecida por Princeton, contém `WeightedQuickUnionUF`, `StdRandom` e `StdStats`
- **Java Swing** – para a interface gráfica interativa
- **Método de Monte Carlo** – para simulação probabilística
- **Estatística descritiva** – cálculo de média, desvio padrão e intervalo de confiança

<br>

## ESTRUTURA DO PROJETO 

| Classe | Descrição |
|--------|-----------|
| `Percolation` | Modela o sistema de percolação N×N com operações de abertura, consulta e verificação de percolação. Utiliza duas DSUs para prevenir backwash. |
| `PercolationStats` | Executa múltiplas simulações de Monte Carlo e calcula estatísticas do limiar de percolação. |
| `PercolationVisualizer` | Interface gráfica Swing que exibe o grid, permite interação manual e simulação automática visual. |

<br>

## EXECUTANDO O PROJETO 

### Pré‑requisitos

- **JDK 8** ou superior instalado.
- **Biblioteca algs4.jar** (já disponível no [repositório](https://github.com/CarlosSilva-DEV/percolation-dsu-assignment/tree/main/lib) ou download no [site oficial](https://algs4.cs.princeton.edu/code/)).

### 1. Clonar o repositório

Em seu computador, navegue até um diretório de sua preferência para armazenar o projeto. Execute um dos comandos abaixo para clonar o repositório remoto para seu computador: 

```bash
# Clonar utilizando URL do repositório remoto
git clone https://github.com/CarlosSilva-DEV/percolation-dsu-assignment.git

# Clonar utilizando chave SSH
git clone git@github.com:CarlosSilva-DEV/percolation-dsu-assignment.git
```

### 2. Compilação

Caso esteja utilizando uma IDE, inclua a biblioteca algs4.jar no **classpath**. Para compilar o projeto através do terminal, navegue até a pasta raiz do projeto e execute os seguintes comandos:

```bash
# Cria o diretório bin/ na raíz do projeto para armazenar as classes compiladas
mkdir bin/

# Compila as classes do diretório src/ incluindo algs4.jar ao classpath e as armazena no diretório bin/ 
javac -cp src:lib/algs4.jar -d bin/ src/Percolation.java src/PercolationStats.java src/PercolationVisualizer.java
```

### 3. Execução

**PercolationStats (linha de comando)**:

```bash
# Executa PercolationStats incluindo algs4.jar ao classpath e passando argumentos no terminal
java -cp lib/algs4.jar:bin PercolationStats 200 100
```

Parâmetros: `<n>` (tamanho da grade) e `<trials>` (número de experimentos).

**Saída esperada**:

```text
mean                    = 0.592...
stddev                  = 0.008...
95% confidence interval = [0.590..., 0.593...]
```

**Percolation (teste simples com entrada via console)**:

```bash
# Executa Percolation incluindo algs4.jar ao classpath
java -cp lib/algs4.jar:bin Percolation
```

Digite o valor de `n` quando solicitado. O programa demonstrará testes de conectividade dinâmica com dados e objetos mockados.

**PercolationVisualizer (interface gráfica)**:

```bash
# Executa PercolationVisualizer incluindo algs4.jar ao classpath
java -cp lib/algs4.jar:bin PercolationVisualizer
```

Abre uma janela onde o usuário pode definir o **tamanho do grid** (1 a 300), interagir clicando nos sites para **abri‑los** e usar os botões para **iniciar/parar a simulação de Monte Carlo visual**.

<br>

## DETALHES DE IMPLEMENTAÇÃO

### Mapeamento 2D → 1D

A grade N×N é armazenada linearmente em um array de tamanho `n*n`. O índice 1D é obtido por: 
<br>
`(row - 1) * n + (col - 1)`.
<br>
Os sites virtuais ocupam as posições `n*n` (topo) e `n*n + 1` (base).

### Abertura de sites (método `open()`)

Quando um site é aberto, ele é conectado a **todos os vizinhos adjacentes** (cima, baixo, esquerda, direita) que também estejam abertos, em ambas as estruturas DSU. A conexão com os sites virtuais ocorre apenas se o site estiver na primeira ou última linha.

### Estatísticas e Simulação de Monte Carlo

O laço principal de `PercolationStats` cria uma nova instância de `Percolation` e sorteia coordenadas aleatórias (com `StdRandom.uniformInt`) até `percolates()` retornar `true`. O limiar daquela rodada é a razão entre `numberOfOpenSites()` e `n*n`. Ao final, `StdStats.mean` e `StdStats.stddev` são utilizados para calcular as estatísticas. A constante `1.96` é usada para o intervalo de confiança de 95%.

### Validação de argumentos

Todas as classes validam os parâmetros de entrada (índices dentro do intervalo, `n > 0`, `trials > 0`) e lançam `IllegalArgumentException` em caso de violação, conforme exigido pela especificação.

<br>

## INTERFACE GRÁFICA

A classe `PercolationVisualizer` oferece uma experiência interativa:

- **Grade desenhada** com células coloridas:
    - Preto → bloqueado
    - Branco → aberto (não conectado ao topo)
    - Azul → cheio (conectado ao topo)
- **Clique do mouse**: abre um site bloqueado.
- **Botão "Open random site"**: abre um site aleatório ainda fechado.
- **Botão "Simulate percolation"**: inicia/para uma simulação automática que abre sites aleatórios a cada 25 ms até que o sistema percole ou todos os sites estejam abertos.
- **Botão "Generate grid"**: permite criar um novo grid com outro valor de N (entre 1 e 300).
- **Painel de status**: exibe contagem de sites abertos, porcentagem e se o sistema percola.

A interface é responsiva e usa `JScrollPane` para grades grandes, garantindo que o canvas mínimo seja de 640 px e que cada célula tenha ao menos 2 px.

<br>

## CONTRIBUIR PARA O PROJETO

Caso queira contribuir de alguma forma para o projeto, sinta-se a vontade para seguir esses passos:

1. Clone esse repositório para a sua máquina utilizando o comando: `git clone git@github.com:CarlosSilva-DEV/percolation-dsu-assignment.git`
2. Crie uma branch específica para promover suas alterações.
3. Abra um Pull Request neste repositório explicando sobre as alterações propostas. Em caso de alterações visuais da aplicação, anexe capturas de telas e aguarde a revisão.

### Documentações auxiliares:

[📝 Como criar um Pull Request?](https://www.atlassian.com/br/git/tutorials/making-a-pull-request)

[💾 Padrões de commits](https://gist.github.com/joshbuchea/6f47e86d2510bce28f8e7f42ae84c716)

### Referências

- [Especificação oficial do exercício (Princeton)](https://coursera.cs.princeton.edu/algs4/assignments/percolation/specification.php)
- [Livro Algorithms, 4th Edition – Robert Sedgewick, Kevin Wayne](https://algs4.cs.princeton.edu/home/)
- [Union‑Find e análise de complexidade](https://algs4.cs.princeton.edu/15uf/)
- [Modelo de percolação – Wikipedia](https://en.wikipedia.org/wiki/Percolation_theory)
