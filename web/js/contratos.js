/* ============================================================
   OrbitClear — Agendar / Meus Contratos (contratos.html)
   Formulario (objeto + operador + data + valor) e timeline
   interativa em memoria. Avancar status: PROPOSTO -> AGENDADO
   -> CONCLUIDO. Tudo SIMULADO (array JS, sem persistencia).
   ============================================================ */

/* copia dos contratos-semente pra mexer sem alterar o dado original */
let contratos = CONTRATOS_SEED.map((c) => ({ ...c }));
let proximoId = Math.max(...contratos.map((c) => c.id), 0) + 1;

/* ---------- popular selects do formulario ---------- */
function popularSelects() {
  const selObj = document.getElementById('f-objeto');
  const selOp  = document.getElementById('f-operador');

  selObj.innerHTML = '<option value="" disabled selected>Selecione o detrito…</option>' +
    OBJETOS.map((o) =>
      `<option value="${o.id}">${ICONE_TIPO[o.tipo]} ${o.nome} — risco ${o.nivelRiscoColisao}</option>`
    ).join('');

  selOp.innerHTML = '<option value="" disabled selected>Selecione o operador…</option>' +
    OPERADORES.map((o) =>
      `<option value="${o.id}" data-preco="${o.precoBaseUSD}">${o.nomeEmpresa} — ${LABEL_TECNOLOGIA[o.tecnologiaCaptura]}</option>`
    ).join('');
}

/* ---------- estimativa automatica de valor ----------
   Regra (simulada): preco base do operador + acrescimo por risco do objeto.
   Da pra editar a mao no campo valor depois. */
const ACRESCIMO_RISCO = { BAIXO: 1.0, MEDIO: 1.15, ALTO: 1.35, CRITICO: 1.6 };

function estimar() {
  const objId = Number(document.getElementById('f-objeto').value);
  const opId  = Number(document.getElementById('f-operador').value);
  const out   = document.getElementById('estimativa-valor');
  const inpVal = document.getElementById('f-valor');

  if (!objId || !opId) { out.textContent = '—'; return; }

  const obj = OrbitClear.objeto(objId);
  const op  = OrbitClear.operador(opId);
  const fator = ACRESCIMO_RISCO[obj.nivelRiscoColisao] || 1;
  const valor = Math.round(op.precoBaseUSD * fator);

  out.textContent = OrbitClear.usd(valor);
  if (!inpVal.value || inpVal.dataset.auto === '1') {
    inpVal.value = valor;
    inpVal.dataset.auto = '1';
  }
}

/* ---------- stepper visual do status ---------- */
function stepperHTML(status) {
  if (status === 'CANCELADO') {
    return '<div class="stepper"><span class="stepper__node" style="background:var(--st-cancelado);border-color:var(--st-cancelado)"></span></div>';
  }
  const idx = FLUXO_CONTRATO.indexOf(status);
  let html = '<div class="stepper">';
  FLUXO_CONTRATO.forEach((etapa, i) => {
    const done = i <= idx ? 'done' : '';
    if (i > 0) html += `<span class="stepper__bar ${i <= idx ? 'done' : ''}"></span>`;
    html += `<span class="stepper__node ${done}" title="${LABEL_STATUS_CONTRATO[etapa]}"></span>`;
  });
  return html + '</div>';
}

/* texto do botao "avancar status" conforme estado atual */
function proximoPasso(status) {
  if (status === 'PROPOSTO') return { label: 'Agendar →', alvo: 'AGENDADO' };
  if (status === 'AGENDADO') return { label: 'Concluir →', alvo: 'CONCLUIDO' };
  return null; // CONCLUIDO/CANCELADO: fim de fluxo
}

/* ---------- card de um contrato na timeline ---------- */
function cardContrato(c) {
  const obj = OrbitClear.objeto(c.objetoOrbitalId);
  const op  = OrbitClear.operador(c.operadorMissaoId);
  const sc  = 's-' + c.status.toLowerCase();
  const passo = proximoPasso(c.status);

  const btn = passo
    ? `<button class="btn-advance" data-id="${c.id}" data-alvo="${passo.alvo}">${passo.label}</button>`
    : `<span class="status-pill">fim de fluxo</span>`;

  return `
    <article class="contract ${sc}">
      <div class="contract__icon">${obj ? ICONE_TIPO[obj.tipo] : '🛰️'}</div>

      <div class="contract__main">
        <div class="contract__route">
          <span>${obj ? obj.nome : 'Objeto #' + c.objetoOrbitalId}</span>
          <span class="arrow">⟶</span>
          <span>${op ? op.nomeEmpresa : 'Operador #' + c.operadorMissaoId}</span>
        </div>
        <div class="contract__meta">
          <span>📅 ${OrbitClear.data(c.dataJanela)}</span>
          <span>🛰️ ${op ? LABEL_TECNOLOGIA[op.tecnologiaCaptura] : '—'}</span>
          <span>#${String(c.id).padStart(3, '0')}</span>
          ${obj ? `<span>🌍 ${OrbitClear.impacto(obj.nivelRiscoColisao).rotulo}</span>` : ''}
        </div>
        ${stepperHTML(c.status)}
      </div>

      <div class="contract__side">
        <span class="tag-status"><span class="dot"></span>${LABEL_STATUS_CONTRATO[c.status]}</span>
        <span class="contract__value">${OrbitClear.usd(c.valorUSD)}</span>
        ${btn}
      </div>
    </article>`;
}

/* ---------- render da lista + contadores ---------- */
function renderContratos() {
  const wrap = document.getElementById('lista-contratos');

  if (contratos.length === 0) {
    wrap.innerHTML = `
      <div class="empty">
        <div class="empty__icon">📡</div>
        <p>Nenhum contrato ainda. Agende uma janela de desorbita no formulario ao lado.</p>
      </div>`;
  } else {
    // mais recentes primeiro
    wrap.innerHTML = contratos.slice().reverse().map(cardContrato).join('');
    ligarBotoesAvancar();
  }

  document.getElementById('cnt-total').textContent     = contratos.length;
  document.getElementById('cnt-agendados').textContent =
    contratos.filter((c) => c.status === 'AGENDADO').length;
  document.getElementById('cnt-concluidos').textContent =
    contratos.filter((c) => c.status === 'CONCLUIDO').length;
}

/* ---------- avancar status de um contrato ---------- */
function ligarBotoesAvancar() {
  document.querySelectorAll('.btn-advance').forEach((b) => {
    b.addEventListener('click', () => {
      const id   = Number(b.getAttribute('data-id'));
      const alvo = b.getAttribute('data-alvo');
      const c = contratos.find((x) => x.id === id);
      if (!c) return;
      c.status = alvo;
      renderContratos();
      toast(`Contrato #${String(id).padStart(3, '0')} agora esta ${LABEL_STATUS_CONTRATO[alvo].toUpperCase()}`);
    });
  });
}

/* ---------- submit do formulario ---------- */
function ligarFormulario() {
  const form = document.getElementById('form-contrato');

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const objId = Number(document.getElementById('f-objeto').value);
    const opId  = Number(document.getElementById('f-operador').value);
    const data  = document.getElementById('f-data').value;
    const valor = Number(document.getElementById('f-valor').value);

    if (!objId || !opId || !data || !valor) {
      toast('Preencha objeto, operador, data e valor.');
      return;
    }

    contratos.push({
      id: proximoId++,
      objetoOrbitalId: objId,
      operadorMissaoId: opId,
      dataJanela: data,
      valorUSD: valor,
      status: 'PROPOSTO'   // todo contrato novo nasce PROPOSTO
    });

    renderContratos();
    form.reset();
    document.getElementById('estimativa-valor').textContent = '—';
    document.getElementById('f-valor').dataset.auto = '1';
    toast('Contrato proposto e adicionado a timeline ✦');

    // rola pra lista no mobile
    document.getElementById('lista-contratos').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  });

  // recalcula estimativa ao trocar objeto/operador
  document.getElementById('f-objeto').addEventListener('change', estimar);
  document.getElementById('f-operador').addEventListener('change', estimar);

  // se o usuario editar o valor a mao, paramos de sobrescrever
  document.getElementById('f-valor').addEventListener('input', (e) => {
    e.target.dataset.auto = '0';
  });
}

/* ---------- pre-selecao vinda do marketplace (?operador=ID) ---------- */
function aplicarPreSelecao() {
  const params = new URLSearchParams(location.search);
  const opId = params.get('operador');
  if (!opId) return;
  const sel = document.getElementById('f-operador');
  if ([...sel.options].some((o) => o.value === opId)) {
    sel.value = opId;
    estimar();
    const op = OrbitClear.operador(Number(opId));
    if (op) toast(`Operador ${op.nomeEmpresa} pre-selecionado`);
  }
}

/* ---------- data minima = hoje ---------- */
function definirDataMinima() {
  const hoje = new Date().toISOString().split('T')[0];
  document.getElementById('f-data').min = hoje;
}

document.addEventListener('DOMContentLoaded', () => {
  popularSelects();
  definirDataMinima();
  ligarFormulario();
  renderContratos();
  aplicarPreSelecao();
});
