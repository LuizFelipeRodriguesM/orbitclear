/* ============================================================
   OrbitClear — Dados estaticos (maquete, sem backend)
   Espelham 1:1 o script banco/orbitclear.sql.
   Tudo SIMULADO: arrays JS em memoria, sem fetch, sem API.
   ============================================================ */

/* ---- Agencias donas dos objetos (cliente_operador) ---- */
const CLIENTES = [
  { id: 1, nomeAgencia: 'NASA', paisOrigem: 'EUA',    contatoEmail: 'orbital@nasa.gov' },
  { id: 2, nomeAgencia: 'ESA',  paisOrigem: 'Europa', contatoEmail: 'debris@esa.int' },
  { id: 3, nomeAgencia: 'INPE', paisOrigem: 'Brasil', contatoEmail: 'contato@inpe.br' }
];

/* ---- Empresas caca-detritos (operador_missao) ---- */
const OPERADORES = [
  {
    id: 1, nomeEmpresa: 'ClearSpace', pais: 'Suica', bandeira: '🇨🇭',
    tecnologiaCaptura: 'BRACO_ROBOTICO', capacidadeKgPorMissao: 500, precoBaseUSD: 1200000,
    descricao: 'Braco robotico de precisao para captura de satelites inteiros de grande porte.'
  },
  {
    id: 2, nomeEmpresa: 'Astroscale', pais: 'Japao', bandeira: '🇯🇵',
    tecnologiaCaptura: 'REDE', capacidadeKgPorMissao: 300, precoBaseUSD: 850000,
    descricao: 'Rede de captura magnetica, ideal para detritos em rotacao descontrolada.'
  },
  {
    id: 3, nomeEmpresa: 'Orbital Cleaners', pais: 'Brasil', bandeira: '🇧🇷',
    tecnologiaCaptura: 'ARPAO', capacidadeKgPorMissao: 200, precoBaseUSD: 600000,
    descricao: 'Arpao cinetico de baixo custo para fragmentos e estagios menores.'
  }
];

/* ---- O lixo espacial (objeto_orbital) ---- */
const OBJETOS = [
  {
    id: 1, nome: 'Estagio superior Ariane-5 R/B', tipo: 'ESTAGIO_FOGUETE',
    altitudeKm: 780, massaKg: 1200, nivelRiscoColisao: 'CRITICO',
    statusRemocao: 'CATALOGADO', clienteId: 2
  },
  {
    id: 2, nome: 'Satelite Envisat (inativo)', tipo: 'SATELITE_MORTO',
    altitudeKm: 770, massaKg: 8211, nivelRiscoColisao: 'CRITICO',
    statusRemocao: 'EM_NEGOCIACAO', clienteId: 2
  },
  {
    id: 3, nome: 'Fragmento Cosmos-2251', tipo: 'FRAGMENTO',
    altitudeKm: 790, massaKg: 4.5, nivelRiscoColisao: 'ALTO',
    statusRemocao: 'CATALOGADO', clienteId: 1
  },
  {
    id: 4, nome: 'CBERS-1 (desativado)', tipo: 'SATELITE_MORTO',
    altitudeKm: 740, massaKg: 1450, nivelRiscoColisao: 'MEDIO',
    statusRemocao: 'AGENDADO', clienteId: 3
  }
];

/* ---- Contratos iniciais (contrato_remocao) ---- */
const CONTRATOS_SEED = [
  { id: 1, objetoOrbitalId: 4, operadorMissaoId: 3, dataJanela: '2026-08-15', valorUSD: 600000,  status: 'AGENDADO' },
  { id: 2, objetoOrbitalId: 2, operadorMissaoId: 1, dataJanela: '2026-09-01', valorUSD: 1500000, status: 'PROPOSTO' }
];

/* ============================================================
   Tabelas de apoio (o "diferencial": impacto na Terra)
   ============================================================ */

/* Cada nivel de risco vira um selo de impacto na Terra. */
const IMPACTO_TERRA = {
  CRITICO: {
    rotulo: 'AMEACA CRITICA',
    texto: 'Colisao aqui pode desencadear efeito cascata (sindrome de Kessler) e derrubar GPS, previsao do tempo e comunicacoes globais.',
    classe: 'r-critico'
  },
  ALTO: {
    rotulo: 'AMEACA ALTA',
    texto: 'Fragmento veloz que ameaca satelites de telecom e a banda de internet usada pelo agro por satelite.',
    classe: 'r-alto'
  },
  MEDIO: {
    rotulo: 'AMEACA MODERADA',
    texto: 'Risco a satelites de observacao da Terra que monitoram clima, queimadas e safras.',
    classe: 'r-medio'
  },
  BAIXO: {
    rotulo: 'AMEACA BAIXA',
    texto: 'Monitorado em orbita estavel. Pouco efeito imediato nos servicos da Terra.',
    classe: 'r-baixo'
  }
};

/* Rotulos amigaveis pros enums (codigo guarda enum, tela mostra texto bonito). */
const LABEL_TIPO = {
  SATELITE_MORTO:  'Satelite morto',
  ESTAGIO_FOGUETE: 'Estagio de foguete',
  FRAGMENTO:       'Fragmento'
};

const ICONE_TIPO = {
  SATELITE_MORTO:  '🛰️',
  ESTAGIO_FOGUETE: '🚀',
  FRAGMENTO:       '☄️'
};

const LABEL_TECNOLOGIA = {
  ARPAO:          'Arpao cinetico',
  REDE:           'Rede de captura',
  BRACO_ROBOTICO: 'Braco robotico',
  LASER:          'Laser ablativo'
};

const ICONE_TECNOLOGIA = {
  ARPAO:          '🎯',
  REDE:           '🕸️',
  BRACO_ROBOTICO: '🦾',
  LASER:          '⚡'
};

const LABEL_STATUS_REMOCAO = {
  CATALOGADO:    'Catalogado',
  EM_NEGOCIACAO: 'Em negociacao',
  AGENDADO:      'Agendado',
  REMOVIDO:      'Removido'
};

const LABEL_STATUS_CONTRATO = {
  PROPOSTO:   'Proposto',
  AGENDADO:   'Agendado',
  CONCLUIDO:  'Concluido',
  CANCELADO:  'Cancelado'
};

/* Ordem do fluxo de status (usada no stepper e no botao "avancar"). */
const FLUXO_CONTRATO = ['PROPOSTO', 'AGENDADO', 'CONCLUIDO'];

/* ============================================================
   Helpers compartilhados pelas 3 telas
   ============================================================ */
const OrbitClear = {
  /* lookups */
  cliente:  (id) => CLIENTES.find((c) => c.id === id),
  operador: (id) => OPERADORES.find((o) => o.id === id),
  objeto:   (id) => OBJETOS.find((o) => o.id === id),

  /* selo de impacto na Terra a partir do nivel de risco */
  impacto: (nivelRisco) => IMPACTO_TERRA[nivelRisco] || IMPACTO_TERRA.BAIXO,

  /* formatadores */
  usd: (v) => '$' + Number(v).toLocaleString('en-US'),

  /* "1.2 t" pra massas grandes, "4.5 kg" pras pequenas */
  massa: (kg) => {
    if (kg >= 1000) return (kg / 1000).toLocaleString('pt-BR', { maximumFractionDigits: 1 }) + ' t';
    return kg.toLocaleString('pt-BR', { maximumFractionDigits: 1 }) + ' kg';
  },

  data: (iso) => {
    if (!iso) return '—';
    const [a, m, d] = iso.split('-');
    return `${d}/${m}/${a}`;
  },

  /* iniciais pro "logo" do operador.
     2+ palavras: inicial de cada ("Orbital Cleaners" -> "OC").
     1 palavra: 2 primeiras letras ("ClearSpace" -> "CL", "Astroscale" -> "AS"). */
  iniciais: (nome) => {
    const partes = nome.trim().split(/\s+/).filter(Boolean);
    const letras = partes.length >= 2
      ? partes.map((p) => p[0]).join('')
      : (partes[0] || '').slice(0, 2);
    return letras.slice(0, 2).toUpperCase();
  }
};

/* Marca o link ativo da navbar pela pagina atual (sem precisar editar HTML). */
function marcarNavAtiva() {
  const atual = (location.pathname.split('/').pop() || 'index.html').toLowerCase();
  document.querySelectorAll('.nav-links a[data-page]').forEach((a) => {
    if (a.getAttribute('data-page') === atual) a.classList.add('active');
  });
}

/* Toast simples reutilizavel. */
function toast(msg) {
  let el = document.querySelector('.toast');
  if (!el) {
    el = document.createElement('div');
    el.className = 'toast';
    el.innerHTML = '<span class="toast__icon">✦</span><span class="toast__msg"></span>';
    document.body.appendChild(el);
  }
  el.querySelector('.toast__msg').textContent = msg;
  el.classList.add('show');
  clearTimeout(el._t);
  el._t = setTimeout(() => el.classList.remove('show'), 2600);
}

document.addEventListener('DOMContentLoaded', marcarNavAtiva);
