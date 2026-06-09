/* ============================================================
   OrbitClear — Marketplace de Operadores (operadores.html)
   Galeria das empresas caca-detritos + botao "Contratar janela".
   "Contratar" leva pra tela de contratos com o operador pre-selecionado.
   ============================================================ */

/* maior capacidade do catalogo, pra dimensionar a barra de capacidade */
const CAP_MAX = Math.max(...OPERADORES.map((o) => o.capacidadeKgPorMissao));

function cardOperador(op) {
  const tec    = LABEL_TECNOLOGIA[op.tecnologiaCaptura];
  const icoTec = ICONE_TECNOLOGIA[op.tecnologiaCaptura];
  const pct    = Math.round((op.capacidadeKgPorMissao / CAP_MAX) * 100);

  return `
    <article class="card">
      <div class="card__rail" style="background:linear-gradient(90deg,var(--neon-cyan),var(--neon-violet));box-shadow:0 0 14px var(--neon-violet)"></div>
      <div class="card__body">

        <div class="op-card__head">
          <div class="op-logo">${OrbitClear.iniciais(op.nomeEmpresa)}</div>
          <div>
            <h3 class="card__title">${op.nomeEmpresa}</h3>
            <div class="op-flag">${op.bandeira} ${op.pais}</div>
          </div>
        </div>

        <div>
          <span class="tech-tag">${icoTec} ${tec}</span>
        </div>

        <p class="earth-seal__desc" style="margin:0">${op.descricao}</p>

        <div class="op-stats">
          <div class="op-stat">
            <div class="op-stat__k">Capacidade / missao</div>
            <div class="op-stat__v">${op.capacidadeKgPorMissao.toLocaleString('pt-BR')} kg</div>
            <div class="cap-bar"><span style="width:${pct}%"></span></div>
          </div>
          <div class="op-stat">
            <div class="op-stat__k">Preco base</div>
            <div class="op-stat__v price">${OrbitClear.usd(op.precoBaseUSD)}</div>
          </div>
        </div>

        <a class="btn btn--primary btn--block" href="contratos.html?operador=${op.id}">
          📡 Contratar janela
        </a>

      </div>
    </article>`;
}

function renderOperadores() {
  const grid = document.getElementById('grid-operadores');
  grid.innerHTML = OPERADORES.map(cardOperador).join('');
}

document.addEventListener('DOMContentLoaded', renderOperadores);
