/* OrbitClear — dados estáticos das 4 entidades (simulação em memória, sem backend).
   Espelha banco/orbitclear.sql, expandido para um catálogo mais rico.
   Cada tela lê estes arrays globais e renderiza no cliente (JS vanilla). */

window.CLIENTES = [
  { id: 1, nomeAgencia: 'NASA', paisOrigem: 'EUA',    contatoEmail: 'orbital@nasa.gov' },
  { id: 2, nomeAgencia: 'ESA',  paisOrigem: 'Europa', contatoEmail: 'debris@esa.int' },
  { id: 3, nomeAgencia: 'INPE', paisOrigem: 'Brasil', contatoEmail: 'contato@inpe.br' },
];

window.OPERADORES = [
  { id: 1, nomeEmpresa: 'ClearSpace',       pais: 'Suíça',       tecnologiaCaptura: 'BRACO_ROBOTICO', capacidadeKgPorMissao: 500, precoBaseUSD: 1200000 },
  { id: 2, nomeEmpresa: 'Astroscale',       pais: 'Japão',       tecnologiaCaptura: 'REDE',           capacidadeKgPorMissao: 300, precoBaseUSD: 850000 },
  { id: 3, nomeEmpresa: 'Orbital Cleaners', pais: 'Brasil',      tecnologiaCaptura: 'ARPAO',          capacidadeKgPorMissao: 200, precoBaseUSD: 600000 },
  { id: 4, nomeEmpresa: 'D-Orbit',          pais: 'Itália',      tecnologiaCaptura: 'BRACO_ROBOTICO', capacidadeKgPorMissao: 450, precoBaseUSD: 1050000 },
  { id: 5, nomeEmpresa: 'Skyrora Sweep',    pais: 'Reino Unido', tecnologiaCaptura: 'LASER',          capacidadeKgPorMissao: 150, precoBaseUSD: 720000 },
];

window.OBJETOS = [
  { id: 1, nome: 'Estágio superior Ariane-5 R/B', tipo: 'ESTAGIO_FOGUETE', altitudeKm: 780,  massaKg: 1200,  nivelRiscoColisao: 'CRITICO', statusRemocao: 'CATALOGADO',    clienteId: 2 },
  { id: 2, nome: 'Satélite Envisat (inativo)',    tipo: 'SATELITE_MORTO',  altitudeKm: 770,  massaKg: 8211,  nivelRiscoColisao: 'CRITICO', statusRemocao: 'EM_NEGOCIACAO', clienteId: 2 },
  { id: 3, nome: 'Fragmento Cosmos-2251',         tipo: 'FRAGMENTO',       altitudeKm: 790,  massaKg: 4.5,   nivelRiscoColisao: 'ALTO',    statusRemocao: 'CATALOGADO',    clienteId: 1 },
  { id: 4, nome: 'CBERS-1 (desativado)',          tipo: 'SATELITE_MORTO',  altitudeKm: 740,  massaKg: 1450,  nivelRiscoColisao: 'MEDIO',   statusRemocao: 'AGENDADO',      clienteId: 3 },
  { id: 5, nome: 'Fragmento Fengyun-1C',          tipo: 'FRAGMENTO',       altitudeKm: 865,  massaKg: 3.2,   nivelRiscoColisao: 'ALTO',    statusRemocao: 'CATALOGADO',    clienteId: 1 },
  { id: 6, nome: 'Estágio SL-16 Zenit-2 R/B',     tipo: 'ESTAGIO_FOGUETE', altitudeKm: 850,  massaKg: 9000,  nivelRiscoColisao: 'CRITICO', statusRemocao: 'CATALOGADO',    clienteId: 2 },
  { id: 7, nome: 'Destroços Iridium-33',          tipo: 'FRAGMENTO',       altitudeKm: 780,  massaKg: 2.1,   nivelRiscoColisao: 'MEDIO',   statusRemocao: 'CATALOGADO',    clienteId: 1 },
  { id: 8, nome: 'Microssatélite Oscar-7',        tipo: 'SATELITE_MORTO',  altitudeKm: 1450, massaKg: 28.8,  nivelRiscoColisao: 'BAIXO',   statusRemocao: 'CATALOGADO',    clienteId: 1 },
];

window.CONTRATOS = [
  { id: 1, objetoOrbitalId: 4, operadorMissaoId: 3, dataJanela: '2026-08-15', valorUSD: 600000,  status: 'AGENDADO' },
  { id: 2, objetoOrbitalId: 2, operadorMissaoId: 1, dataJanela: '2026-09-01', valorUSD: 1500000, status: 'PROPOSTO' },
];

/* Diferencial "Impacto na Terra": liga o risco orbital à infraestrutura terrestre. */
window.IMPACTO_TERRA = {
  CRITICO: 'Ameaça direta a rotas de GPS e satélites de meteorologia — risco à navegação e à previsão do tempo.',
  ALTO:    'Risco a constelações de comunicação e TV — possíveis interrupções de sinal e internet.',
  MEDIO:   'Pressão sobre o tráfego orbital — eleva o custo de manobras de satélites ativos.',
  BAIXO:   'Monitorado — contribui para a densidade de detritos de longo prazo na órbita baixa.',
};

/* Rótulos legíveis para os enums do domínio. */
window.LABELS = {
  tipo:       { SATELITE_MORTO: 'Satélite morto', ESTAGIO_FOGUETE: 'Estágio de foguete', FRAGMENTO: 'Fragmento' },
  status:     { CATALOGADO: 'Catalogado', EM_NEGOCIACAO: 'Em negociação', AGENDADO: 'Agendado', REMOVIDO: 'Removido' },
  contrato:   { PROPOSTO: 'Proposto', AGENDADO: 'Agendado', CONCLUIDO: 'Concluído', CANCELADO: 'Cancelado' },
  tecnologia: { ARPAO: 'Arpão', REDE: 'Rede', BRACO_ROBOTICO: 'Braço robótico', LASER: 'Laser' },
  risco:      { CRITICO: 'Crítico', ALTO: 'Alto', MEDIO: 'Médio', BAIXO: 'Baixo' },
};

/* Helpers de formatação e lookup. */
window.fmtUSD = (v) => '$' + Number(v).toLocaleString('en-US');
window.fmtKg  = (v) => Number(v).toLocaleString('pt-BR') + ' kg';
window.fmtKm  = (v) => Number(v).toLocaleString('pt-BR') + ' km';
window.clientePorId  = (id) => (window.CLIENTES.find((c) => c.id === id) || {}).nomeAgencia || '—';
window.objetoPorId   = (id) => window.OBJETOS.find((o) => o.id === id) || null;
window.operadorPorId = (id) => window.OPERADORES.find((o) => o.id === id) || null;
