# OrbitClear

> Marketplace de remoção de lixo espacial — o "Uber" pra limpar a órbita da Terra.
> Global Solution 2026/1 · FIAP Engenharia de Software · Grupo 89

## O problema

A órbita baixa da Terra está entulhada de lixo: satélites mortos, estágios de foguete e milhões de fragmentos viajando a 28.000 km/h. Uma única colisão gera milhares de novos detritos — o efeito cascata conhecido como **síndrome de Kessler** — ameaçando a infraestrutura espacial da qual a Terra inteira depende: **GPS, previsão do tempo, comunicações, TV e monitoramento agrícola por satélite**. Hoje não existe uma forma organizada de contratar a remoção desses objetos.

## A solução

OrbitClear é um **marketplace** que conecta:

- **Agências e empresas** donas de objetos em órbita (quem precisa remover);
- **operadores de missão** especializados em captura de detritos (quem executa);

fechando **contratos de remoção** com data, preço e status — como pedir uma corrida, só que pra limpar o espaço.

## Por que importa pra Terra

Limpar a órbita não é "problema do espaço" — é manter funcionando o que a Terra usa todos os dias. Cada objeto cadastrado exibe um selo de **Impacto na Terra** (ex.: risco alto = ameaça rotas de GPS e satélites de meteorologia).

## Tecnologias

- **Java** (POO, aplicação de console) — lógica do sistema
- **SQL** (`CREATE TABLE`) + **diagrama ER** — modelagem de dados
- **HTML + CSS + JavaScript** (protótipo web — em breve neste repositório)

> Projeto acadêmico: tudo é **simulado**, sem integração entre as partes e sem backend real.

## Estrutura

```
orbitclear/
├── java/             # aplicação Java POO (console)
├── banco/            # script .sql do banco de dados
├── diagramas/        # diagrama de classes (UML) e ER, em Mermaid
├── web/              # protótipo HTML/CSS/JS (4 telas)
└── INTEGRANTES.txt   # equipe + link do vídeo pitch
```

## Como rodar (Java)

```bash
cd java
javac *.java
java Main
```

A aplicação sobe um menu no console com cadastro, listagem, busca e atualização de objetos, operadores, clientes e contratos de remoção.

## Banco de dados

```bash
# em qualquer SGBD compatível com SQL ANSI
sqlite3 orbitclear.db ".read banco/orbitclear.sql"
```

O script cria 4 tabelas relacionadas (`cliente_operador`, `operador_missao`, `objeto_orbital`, `contrato_remocao`) e já popula dados de exemplo com detritos e empresas reais (Envisat, Ariane-5, Cosmos-2251, ClearSpace, Astroscale).

## Diagramas

> Renderizam automaticamente aqui no GitHub. Fontes em `diagramas/*.mmd`.

### Diagrama de Classes (UML)

```mermaid
classDiagram
    class ClienteOperador {
        +int id
        +String nomeAgencia
        +String paisOrigem
        +String contatoEmail
    }
    class ObjetoOrbital {
        +int id
        +String nome
        +TipoObjeto tipo
        +int altitudeKm
        +double massaKg
        +NivelRisco nivelRiscoColisao
        +StatusRemocao statusRemocao
        +ClienteOperador dono
        +String impactoNaTerra()
    }
    class OperadorMissao {
        +int id
        +String nomeEmpresa
        +String pais
        +TecnologiaCaptura tecnologiaCaptura
        +double capacidadeKgPorMissao
        +double precoBaseUSD
    }
    class ContratoRemocao {
        +int id
        +ObjetoOrbital objeto
        +OperadorMissao operador
        +LocalDate dataJanela
        +double valorUSD
        +StatusContrato status
    }
    class OrbitClearService {
        -List~ClienteOperador~ clientes
        -List~ObjetoOrbital~ objetos
        -List~OperadorMissao~ operadores
        -List~ContratoRemocao~ contratos
        +cadastrarObjeto(...) ObjetoOrbital
        +listarObjetos() List
        +buscarObjetoPorId(int) ObjetoOrbital
        +atualizarStatusObjeto(int, StatusRemocao) boolean
        +agendarContrato(int, int, LocalDate, double) ContratoRemocao
        +concluirContrato(int) boolean
    }
    class TipoObjeto {
        <<enumeration>>
        SATELITE_MORTO
        ESTAGIO_FOGUETE
        FRAGMENTO
    }
    class NivelRisco {
        <<enumeration>>
        BAIXO
        MEDIO
        ALTO
        CRITICO
    }
    class StatusRemocao {
        <<enumeration>>
        CATALOGADO
        EM_NEGOCIACAO
        AGENDADO
        REMOVIDO
    }
    class StatusContrato {
        <<enumeration>>
        PROPOSTO
        AGENDADO
        CONCLUIDO
        CANCELADO
    }
    class TecnologiaCaptura {
        <<enumeration>>
        ARPAO
        REDE
        BRACO_ROBOTICO
        LASER
    }
    ClienteOperador "1" --> "N" ObjetoOrbital : possui
    ObjetoOrbital "1" --> "N" ContratoRemocao : alvo de
    OperadorMissao "1" --> "N" ContratoRemocao : executa
    OrbitClearService --> ObjetoOrbital : gerencia
    OrbitClearService --> OperadorMissao : gerencia
    OrbitClearService --> ClienteOperador : gerencia
    OrbitClearService --> ContratoRemocao : gerencia
```

### Diagrama Entidade-Relacionamento (ER)

4 tabelas, 3 relacionamentos. `contrato_remocao` é a tabela associativa (N:N) entre objeto e operador.

```mermaid
erDiagram
    cliente_operador ||--o{ objeto_orbital : possui
    objeto_orbital   ||--o{ contrato_remocao : "alvo de"
    operador_missao  ||--o{ contrato_remocao : executa

    cliente_operador {
        INT id PK
        VARCHAR nome_agencia "NOT NULL"
        VARCHAR pais_origem
        VARCHAR contato_email
    }
    operador_missao {
        INT id PK
        VARCHAR nome_empresa "NOT NULL"
        VARCHAR pais
        VARCHAR tecnologia_captura "ARPAO | REDE | BRACO_ROBOTICO | LASER"
        DECIMAL capacidade_kg_por_missao
        DECIMAL preco_base_usd
    }
    objeto_orbital {
        INT id PK
        VARCHAR nome "NOT NULL"
        VARCHAR tipo "SATELITE_MORTO | ESTAGIO_FOGUETE | FRAGMENTO"
        INT altitude_km
        DECIMAL massa_kg
        VARCHAR nivel_risco_colisao "BAIXO | MEDIO | ALTO | CRITICO"
        VARCHAR impacto_terra "selo Impacto na Terra"
        VARCHAR status_remocao "CATALOGADO | EM_NEGOCIACAO | AGENDADO | REMOVIDO"
        INT cliente_id FK
    }
    contrato_remocao {
        INT id PK
        INT objeto_orbital_id FK "NOT NULL"
        INT operador_missao_id FK "NOT NULL"
        DATE data_janela
        DECIMAL valor_usd
        VARCHAR status "PROPOSTO | AGENDADO | CONCLUIDO | CANCELADO"
    }
```

## Equipe — Grupo 89

| Nome | RM |
|------|-----|
| Luiz Felipe Rodrigues Machado | RM32738 |
| Vitor Gomes | RM561317 |
| Pedro Sato | RM564859 |
| Felipe Wunder | RM561366 |
