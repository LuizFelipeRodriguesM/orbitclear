-- ============================================================
-- OrbitClear - Script de criacao do banco de dados
-- Global Solution 2026/1 - FIAP Engenharia de Software - Grupo 89
-- Marketplace de remocao de lixo espacial
-- ============================================================
--
-- Modelo: 4 tabelas, 3 relacionamentos.
--   cliente_operador 1:N objeto_orbital
--   objeto_orbital   1:N contrato_remocao
--   operador_missao  1:N contrato_remocao
--   (contrato_remocao e a tabela associativa entre objeto e operador)
--
-- Ordem de criacao respeita as chaves estrangeiras.
-- ============================================================

-- Recria do zero a cada execucao (ordem inversa das FKs).
DROP TABLE IF EXISTS contrato_remocao;
DROP TABLE IF EXISTS objeto_orbital;
DROP TABLE IF EXISTS operador_missao;
DROP TABLE IF EXISTS cliente_operador;

-- Agencia / empresa dona de um objeto em orbita (quem quer remover)
CREATE TABLE cliente_operador (
    id              INT          PRIMARY KEY,
    nome_agencia    VARCHAR(100) NOT NULL,
    pais_origem     VARCHAR(60),
    contato_email   VARCHAR(120)
);

-- Empresa caca-detritos que executa a remocao
CREATE TABLE operador_missao (
    id                        INT          PRIMARY KEY,
    nome_empresa              VARCHAR(100) NOT NULL,
    pais                      VARCHAR(60),
    tecnologia_captura        VARCHAR(30),   -- ARPAO | REDE | BRACO_ROBOTICO | LASER
    capacidade_kg_por_missao  DECIMAL(10,2),
    preco_base_usd            DECIMAL(12,2),
    CHECK (tecnologia_captura IN ('ARPAO', 'REDE', 'BRACO_ROBOTICO', 'LASER'))
);

-- O lixo espacial em si
CREATE TABLE objeto_orbital (
    id                    INT          PRIMARY KEY,
    nome                  VARCHAR(120) NOT NULL,
    tipo                  VARCHAR(30),    -- SATELITE_MORTO | ESTAGIO_FOGUETE | FRAGMENTO
    altitude_km           INT,
    massa_kg              DECIMAL(10,2),
    nivel_risco_colisao   VARCHAR(20),    -- BAIXO | MEDIO | ALTO | CRITICO
    impacto_terra         VARCHAR(150),   -- selo "Impacto na Terra" (texto derivado do nivel_risco_colisao)
    status_remocao        VARCHAR(20)  NOT NULL,   -- CATALOGADO | EM_NEGOCIACAO | AGENDADO | REMOVIDO
    cliente_id            INT          NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES cliente_operador (id),
    CHECK (tipo IN ('SATELITE_MORTO', 'ESTAGIO_FOGUETE', 'FRAGMENTO')),
    CHECK (nivel_risco_colisao IN ('BAIXO', 'MEDIO', 'ALTO', 'CRITICO')),
    CHECK (status_remocao IN ('CATALOGADO', 'EM_NEGOCIACAO', 'AGENDADO', 'REMOVIDO'))
);

-- O "pedido" do marketplace: liga um objeto a um operador
CREATE TABLE contrato_remocao (
    id                  INT          PRIMARY KEY,
    objeto_orbital_id   INT          NOT NULL,
    operador_missao_id  INT          NOT NULL,
    data_janela         DATE,
    valor_usd           DECIMAL(12,2),
    status              VARCHAR(20)  NOT NULL,   -- PROPOSTO | AGENDADO | CONCLUIDO | CANCELADO
    FOREIGN KEY (objeto_orbital_id)  REFERENCES objeto_orbital (id),
    FOREIGN KEY (operador_missao_id) REFERENCES operador_missao (id),
    CHECK (status IN ('PROPOSTO', 'AGENDADO', 'CONCLUIDO', 'CANCELADO'))
);

-- ============================================================
-- Dados de exemplo (detritos e empresas reais, pra demonstracao)
-- ============================================================

INSERT INTO cliente_operador (id, nome_agencia, pais_origem, contato_email) VALUES
 (1, 'NASA', 'EUA',    'orbital@nasa.gov'),
 (2, 'ESA',  'Europa', 'debris@esa.int'),
 (3, 'INPE', 'Brasil', 'contato@inpe.br');

-- Capacidades plausiveis: cobrem a massa dos objetos sob contrato
-- (Envisat 8211 kg cabe em ClearSpace; CBERS-1 1450 kg cabe em Orbital Cleaners).
INSERT INTO operador_missao (id, nome_empresa, pais, tecnologia_captura, capacidade_kg_por_missao, preco_base_usd) VALUES
 (1, 'ClearSpace',       'Suica',  'BRACO_ROBOTICO', 9000.00, 1200000.00),
 (2, 'Astroscale',       'Japao',  'REDE',           3000.00,  850000.00),
 (3, 'Orbital Cleaners', 'Brasil', 'ARPAO',          2000.00,  600000.00);

-- impacto_terra: texto do selo "Impacto na Terra" derivado de nivel_risco_colisao
-- (fonte da verdade: java/NivelRisco.java).
INSERT INTO objeto_orbital (id, nome, tipo, altitude_km, massa_kg, nivel_risco_colisao, impacto_terra, status_remocao, cliente_id) VALUES
 (1, 'Estagio superior Ariane-5 R/B', 'ESTAGIO_FOGUETE', 780, 1200.00, 'CRITICO', 'Ameaca direta a satelites de GPS e meteorologia',                       'CATALOGADO',    2),
 (2, 'Satelite Envisat (inativo)',    'SATELITE_MORTO',  770, 8211.00, 'CRITICO', 'Ameaca direta a satelites de GPS e meteorologia',                       'EM_NEGOCIACAO', 2),
 (3, 'Fragmento Cosmos-2251',         'FRAGMENTO',       790,    4.50, 'ALTO',    'Risco a comunicacoes e a observacao da Terra',                          'CATALOGADO',    1),
 (4, 'CBERS-1 (desativado)',          'SATELITE_MORTO',  740, 1450.00, 'MEDIO',   'Pode interferir em rotas de satelites de imagem e no agro por satelite', 'AGENDADO',      3);

INSERT INTO contrato_remocao (id, objeto_orbital_id, operador_missao_id, data_janela, valor_usd, status) VALUES
 (1, 4, 3, '2026-08-15',  600000.00, 'AGENDADO'),
 (2, 2, 1, '2026-09-01', 1500000.00, 'PROPOSTO');

-- ============================================================
-- Marketplace: contrato -> objeto -> operador -> dono do objeto.
-- Mostra, por contrato, o detrito, o selo "Impacto na Terra",
-- quem executa a remocao e a agencia dona, confirmando que a
-- capacidade do operador cobre a massa do objeto.
-- ============================================================
-- SELECT
--     c.id                          AS contrato,
--     o.nome                        AS objeto,
--     o.massa_kg,
--     o.nivel_risco_colisao,
--     o.impacto_terra,
--     op.nome_empresa               AS operador,
--     op.capacidade_kg_por_missao,
--     cli.nome_agencia              AS dono_objeto,
--     c.status                      AS status_contrato
-- FROM contrato_remocao c
-- JOIN objeto_orbital   o   ON o.id   = c.objeto_orbital_id
-- JOIN operador_missao  op  ON op.id  = c.operador_missao_id
-- JOIN cliente_operador cli ON cli.id = o.cliente_id
-- ORDER BY c.id;
