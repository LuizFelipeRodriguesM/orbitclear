/* ============================================================
   OrbitClear — Dashboard de Risco Orbital (index.html)
   Renderiza contadores + grid de cards dos objetos orbitais.
   Cada card: badge de risco + selo "Impacto na Terra" + dados.
   ============================================================ */

/* classe de cor (--accent) a partir do nivel de risco */
function classeRisco(nivel) {
  return 'r-' + nivel.toLowerCase();
}

/* monta o HTML de um card de objeto orbital */
function cardObjeto(obj) {
  const imp   = OrbitClear.impacto(obj.nivelRiscoColisao);
  const dono  = OrbitClear.cliente(obj.clienteId);
  const rc    = classeRisco(obj.nivelRiscoColisao);
  const icone = ICONE_TIPO[obj.tipo] || '🛰️';

  return `
    <article class="card ${rc}" data-risco="${obj.nivelRiscoColisao}">
      <div class="card__rail"></div>
      <div class="card__body">

        <div class="card__top">
          <div>
            <h3 class="card__title">${icone} ${obj.nome}</h3>
            <div class="card__sub">${LABEL_TIPO[obj.tipo]} · #${String(obj.id).padStart(3, '0')}</div>
          </div>
          <div class="risk-ring" title="Nivel de risco de colisao">${obj.nivelRiscoColisao}</div>
        </div>

        <div>
          <span class="badge"><span class="dot"></span>Risco ${obj.nivelRiscoColisao}</span>
        </div>

        <div class="earth-seal">
          <div class="earth-seal__globe">🌍</div>
          <div class="earth-seal__text">
            <div class="earth-seal__title">Impacto na Terra · ${imp.rotulo}</div>
            <p class="earth-seal__desc">${imp.texto}</p>
          </div>
        </div>

        <div class="specs">
          <div class="spec">
            <div class="spec__k">Altitude</div>
            <div class="spec__v">${obj.altitudeKm}<small> km</small></div>
          </div>
          <div class="spec">
            <div class="spec__k">Massa</div>
            <div class="spec__v">${OrbitClear.massa(obj.massaKg)}</div>
          </div>
          <div class="spec">
            <div class="spec__k">Status</div>
            <div class="spec__v" style="font-size:.78rem">${LABEL_STATUS_REMOCAO[obj.statusRemocao]}</div>
          </div>
        </div>

        <div class="card__owner">
          🛰️ Dono: <b>${dono ? dono.nomeAgencia : '—'}</b>
          <span class="status-pill" style="margin-left:auto">${LABEL_STATUS_REMOCAO[obj.statusRemocao]}</span>
        </div>

      </div>
    </article>`;
}

/* renderiza o grid (com filtro opcional por risco) */
function renderObjetos(filtro = 'TODOS') {
  const grid = document.getElementById('grid-objetos');
  const lista = filtro === 'TODOS'
    ? OBJETOS
    : OBJETOS.filter((o) => o.nivelRiscoColisao === filtro);

  if (lista.length === 0) {
    grid.innerHTML = `
      <div class="empty" style="grid-column:1/-1">
        <div class="empty__icon">🛰️</div>
        <p>Nenhum objeto neste nivel de risco.</p>
      </div>`;
    return;
  }
  grid.innerHTML = lista.map(cardObjeto).join('');
}

/* preenche os contadores do topo */
function renderContadores() {
  const totalKg = OBJETOS.reduce((s, o) => s + o.massaKg, 0);
  const toneladas = (totalKg / 1000).toLocaleString('pt-BR', { maximumFractionDigits: 1 });
  const criticos = OBJETOS.filter((o) => o.nivelRiscoColisao === 'CRITICO').length;
  const emRisco  = OBJETOS.filter((o) => ['ALTO', 'CRITICO'].includes(o.nivelRiscoColisao)).length;

  document.getElementById('stat-toneladas').textContent = toneladas;
  document.getElementById('stat-total').textContent     = OBJETOS.length;
  document.getElementById('stat-criticos').textContent  = criticos;
  document.getElementById('stat-risco').textContent     = emRisco;
}

/* liga os chips de filtro */
function ligarFiltros() {
  document.querySelectorAll('.chip[data-filtro]').forEach((chip) => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('.chip[data-filtro]').forEach((c) => c.classList.remove('active'));
      chip.classList.add('active');
      renderObjetos(chip.getAttribute('data-filtro'));
    });
  });
}

document.addEventListener('DOMContentLoaded', () => {
  renderContadores();
  renderObjetos();
  ligarFiltros();
});
