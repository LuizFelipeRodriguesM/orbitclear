/* OrbitClear — Globo de pontos (Canvas 2D vanilla, zero dependência).
   Substitui o SVG wireframe do tile de órbita e reproduz o look do globo
   pontilhado (estilo "cobe") SEM framework nem biblioteca: esfera amostrada
   por sequência de Fibonacci, máscara de continentes embutida e rotação 3D
   feita na mão. Canvas/WebGL-2D é API nativa do navegador — continua "vanilla".
   Markers em ember = sítios de lançamento reais (de onde nasce o lixo orbital),
   reforçando a conexão com a Terra. */
(function () {
  'use strict';

  var canvas = document.getElementById('ed-orbit-canvas');
  if (!canvas || !canvas.getContext) return;
  var ctx = canvas.getContext('2d');

  /* --- Máscara de continentes: união de elipses [lat, lon, raioLat, raioLon] em
     graus. Aproxima as massas de terra o suficiente pra serem reconhecíveis
     girando (não precisa de textura externa). --- */
  var LAND = [
    [54, -100, 26, 34], [40, -100, 16, 26], [62, -150, 12, 22], [25, -103, 10, 12], [72, -42, 14, 24], // Am. Norte + Groenlândia
    [-8, -60, 15, 16], [-23, -58, 13, 12], [-40, -67, 11, 8], [5, -73, 7, 7],                            // Am. Sul
    [50, 14, 12, 22], [62, 18, 10, 16], [42, -3, 8, 10],                                                 // Europa
    [14, 8, 16, 24], [-8, 22, 18, 19], [30, 28, 11, 15], [-29, 25, 9, 11],                               // África
    [58, 92, 20, 52], [40, 108, 15, 22], [30, 80, 16, 26], [22, 79, 9, 9], [12, 104, 9, 11], [26, 46, 11, 15], // Ásia
    [-25, 134, 10, 19], [-6, 140, 6, 11]                                                                 // Oceania
  ];

  function isLand(lat, lon) {
    if (lat < -62) return true; // Antártica (coroa inferior)
    for (var i = 0; i < LAND.length; i++) {
      var b = LAND[i];
      var dLat = (lat - b[0]) / b[2];
      var dl = lon - b[1];
      if (dl > 180) dl -= 360; else if (dl < -180) dl += 360;
      var dLon = dl / b[3];
      if (dLat * dLat + dLon * dLon <= 1) return true;
    }
    return false;
  }

  /* --- Pontos da esfera via sequência de Fibonacci (distribuição uniforme) --- */
  var N = 7000;
  var pts = [];
  var GA = Math.PI * (3 - Math.sqrt(5)); // ângulo áureo
  var DEG = 180 / Math.PI;
  for (var i = 0; i < N; i++) {
    var y = 1 - (i / (N - 1)) * 2;
    var rr = Math.sqrt(Math.max(0, 1 - y * y));
    var th = GA * i;
    var x = Math.cos(th) * rr;
    var z = Math.sin(th) * rr;
    pts.push({
      x: x, y: y, z: z,
      land: isLand(Math.asin(y) * DEG, Math.atan2(z, x) * DEG)
    });
  }

  /* --- Markers: sítios de lançamento reais (de onde nasce o lixo orbital) --- */
  var SITES = [
    [28.5, -80.6], [34.7, -120.6], [5.2, -52.8], [45.9, 63.3], [62.9, 40.6],
    [40.9, 100.3], [13.7, 80.2], [30.4, 131.0], [19.6, 110.9]
  ];
  var marks = SITES.map(function (s) {
    var la = s[0] / DEG, lo = s[1] / DEG, c = Math.cos(la);
    return { x: c * Math.cos(lo), y: Math.sin(la), z: c * Math.sin(lo) };
  });

  /* --- Estado --- */
  var TILT = -0.30, cosT = Math.cos(TILT), sinT = Math.sin(TILT);
  var dpr = 1, cx = 0, cy = 0, R = 0;
  var phi = -1.2;                     // rotação automática acumulada
  var dragOff = 0, dragTarget = 0;    // rotação manual (arrasto) + alvo suavizado
  var dragging = false, lastX = 0;
  var clock = 0, raf = 0;
  var reduce = !!(window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches);

  function clamp01(v) { return v < 0 ? 0 : v > 1 ? 1 : v; }

  function resize() {
    var w = canvas.getBoundingClientRect().width || 420;
    dpr = Math.min(window.devicePixelRatio || 1, 2);
    canvas.width = Math.round(w * dpr);
    canvas.height = Math.round(w * dpr);
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    cx = w / 2; cy = w / 2; R = w * 0.46;
  }

  function draw() {
    var w = canvas.width / dpr;
    ctx.clearRect(0, 0, w, w);

    // brilho de fundo (a Terra "iluminada")
    var bg = ctx.createRadialGradient(cx, cy - R * 0.15, R * 0.1, cx, cy, R * 1.35);
    bg.addColorStop(0, 'rgba(120,140,205,0.13)');
    bg.addColorStop(1, 'rgba(0,0,0,0)');
    ctx.fillStyle = bg;
    ctx.fillRect(0, 0, w, w);

    if (!reduce && !dragging) phi += 0.0024;
    dragOff += (dragTarget - dragOff) * (reduce ? 1 : 0.12);
    var a = phi + dragOff, cosA = Math.cos(a), sinA = Math.sin(a);
    clock += 1;
    var pulse = reduce ? 1 : 0.55 + 0.45 * Math.sin(clock * 0.06);

    // pontos do globo
    for (var i = 0; i < pts.length; i++) {
      var p = pts[i];
      var xr = p.x * cosA + p.z * sinA;          // rotação no eixo Y (longitude)
      var zr = -p.x * sinA + p.z * cosA;
      var yr = p.y * cosT - zr * sinT;           // inclinação no eixo X
      var zt = p.y * sinT + zr * cosT;
      if (zt < -0.05) continue;                  // esconde o hemisfério de trás
      var f = clamp01((zt + 0.1) / 0.7);         // profundidade -> brilho/tamanho
      var sx = cx + xr * R, sy = cy - yr * R;
      var size = (0.55 + 0.7 * f) * (p.land ? 1 : 0.85);
      ctx.fillStyle = p.land
        ? 'rgba(226,233,247,' + (0.22 + 0.72 * f) + ')'
        : 'rgba(150,170,212,' + (0.03 + 0.10 * f) + ')';
      ctx.fillRect(sx - size / 2, sy - size / 2, size, size);
    }

    // markers (ember, com glow e pulsação)
    for (var m = 0; m < marks.length; m++) {
      var q = marks[m];
      var mxr = q.x * cosA + q.z * sinA;
      var mzr = -q.x * sinA + q.z * cosA;
      var myr = q.y * cosT - mzr * sinT;
      var mzt = q.y * sinT + mzr * cosT;
      if (mzt < 0.02) continue;
      var mf = clamp01((mzt + 0.1) / 0.8);
      ctx.save();
      ctx.shadowColor = 'rgba(255,90,0,0.9)';
      ctx.shadowBlur = 10 * mf;
      ctx.fillStyle = 'rgba(255,90,0,' + (0.55 + 0.45 * mf) + ')';
      ctx.beginPath();
      ctx.arc(cx + mxr * R, cy - myr * R, (1.6 + 1.7 * mf) * (0.7 + 0.5 * pulse), 0, Math.PI * 2);
      ctx.fill();
      ctx.restore();
    }

    // atmosfera (anel sutil na borda)
    var atm = ctx.createRadialGradient(cx, cy, R * 0.9, cx, cy, R * 1.06);
    atm.addColorStop(0, 'rgba(180,200,255,0)');
    atm.addColorStop(0.7, 'rgba(180,200,255,0.10)');
    atm.addColorStop(1, 'rgba(180,200,255,0)');
    ctx.fillStyle = atm;
    ctx.fillRect(0, 0, w, w);
  }

  function tick() { raf = requestAnimationFrame(tick); draw(); }
  function start() { if (reduce) { draw(); return; } if (!raf) raf = requestAnimationFrame(tick); }
  function stop() { if (raf) { cancelAnimationFrame(raf); raf = 0; } }

  /* --- interação: arrastar pra girar --- */
  canvas.addEventListener('pointerdown', function (e) {
    dragging = true; lastX = e.clientX; canvas.style.cursor = 'grabbing';
  });
  window.addEventListener('pointermove', function (e) {
    if (!dragging) return;
    dragTarget += (e.clientX - lastX) * 0.005;
    lastX = e.clientX;
    if (reduce) draw();           // scroll vertical fica com o browser (touch-action: pan-y)
  });
  window.addEventListener('pointerup', function () {
    dragging = false; canvas.style.cursor = 'grab';
  });

  /* pausa quando fora da tela (economia de CPU) */
  if ('IntersectionObserver' in window) {
    new IntersectionObserver(function (es) {
      if (es[0].isIntersecting) start(); else stop();
    }, { threshold: 0.01 }).observe(canvas);
  }

  var rt;
  window.addEventListener('resize', function () {
    clearTimeout(rt);
    rt = setTimeout(function () { resize(); if (reduce) draw(); }, 120);
  });

  resize();
  start();
})();
