// Qariana Quran Web Reader — Hafezi 15-line (Qudratullah/Emdadia) + 14-line layouts
// Ornate arch SVG on every page: Arabic-only variant + variant with ONE Bengali sub-row under each Arabic line.
// Layout registry: config/layouts.json | Tafsir registry: config/tafsir.json | Agent API: window.QuranApp
// RULE: Quran Arabic / translation / tafsir text is only ever read from data/ packs — never typed from memory.

const ROOT = new URL('../', import.meta.url);

const BN_DIGITS = '০১২৩৪৫৬৭৮৯';
const bn = n => String(n).replace(/\d/g, d => BN_DIGITS[d]);
const toEn = x => String(x).replace(/[০-৯]/g, d => BN_DIGITS.indexOf(d));
const esc = s => String(s).replace(/[&<>"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));
const $ = (s, r = document) => r.querySelector(s);
const json = async p => { const r = await fetch(new URL(p, ROOT)); if (!r.ok) throw new Error('http_' + r.status + ':' + p); return r.json(); };

// ---------- State ----------
const urlParams = new URLSearchParams(location.search);
const saved = (() => { try { return JSON.parse(localStorage.getItem('qr-state') || '{}'); } catch { return {}; } })();

const state = {
  mode: urlParams.get('mode') || saved.mode || 'off',
  tajweed: urlParams.has('tajweed') ? urlParams.get('tajweed') === 'true' : (saved.tajweed || false),
  plan: urlParams.get('plan') || saved.plan || 'free',
  layout: urlParams.get('layout') || saved.layout || 'hafezi15',
  pageBy: saved.pageBy || {},
  page: parseInt(urlParams.get('page'), 10) || 0,
  tafsir: urlParams.get('tafsir') || saved.tafsir || 'bn-translation',
  tfSize: saved.tfSize || 1,
  zoom: parseFloat(urlParams.get('zoom')) || saved.zoom || 1,
  theme: urlParams.get('theme') || saved.theme || 'light',
  bold: urlParams.has('bold') ? urlParams.get('bold') === '1' : !!saved.bold,
  wake: !!saved.wake,
  word: urlParams.get('word') || null,
  ayah: urlParams.get('ayah') || null,
  tfAyah: null
};

const persist = () => {
  try {
    localStorage.setItem('qr-state', JSON.stringify({
      mode: state.mode, tajweed: state.tajweed, plan: state.plan,
      layout: state.layout, pageBy: state.pageBy, tafsir: state.tafsir, tfSize: state.tfSize,
      zoom: state.zoom, theme: state.theme, bold: state.bold, wake: state.wake
    }));
  } catch {}
};

let cfg = {}, manifest, surahs = {}, wbwData = {}, fullTranslations = {}, wordDict = {}, wordLoc = {};
let layouts = [], layout = null, surahPages = {}, juzs = [];
let tafsirSources = [];
const layoutOk = new Map(), tafsirOk = new Map(), tafsirCache = new Map();
let pageCache = new Map(), svgTemplates = new Map(), rendering = Promise.resolve();

const FALLBACK_LAYOUT = {
  id: 'hafezi15', label_bn: '১৫ লাইন', title_bn: 'হাফেজী ১৫ লাইন', lines: 15, pages: 610,
  pages_dir: 'data/qudratullah_pages', surah_pages: 'data/surah-pages.json', juz_file: 'data/juzs.json',
  bg_arabic: 'assets/bg/page-bg-arabic.svg', bg_translation: 'assets/bg/page-bg-translation.svg',
  bg_arch_arabic: 'assets/bg/page-bg-arabic.svg', bg_arch_translation: 'assets/bg/page-bg-translation.svg',
  bg_std_arabic: 'assets/bg/page-bg-standard-15-ar.svg', bg_std_translation: 'assets/bg/page-bg-standard-15-sub.svg',
  info: null
};

function toast(msg) {
  const t = $('#toast'); if (!t) return;
  t.textContent = msg; t.classList.add('show');
  clearTimeout(toast.t); toast.t = setTimeout(() => t.classList.remove('show'), 2800);
}

// ---------- Data loaders ----------
// Word metadata is split per surah (data/words/<s>.json); only the surahs a page needs are fetched.
let shardIdx = null;
const shards = new Map();
function loadShard(s) {
  s = String(s);
  if (!shards.has(s)) shards.set(s, json(`data/words/${s}.json`).then(raw => {
    for (const [loc, w] of Object.entries(raw)) { wordLoc[loc] = w; wordDict[w.id] = { ...w, loc }; }
  }).catch(e => { shards.delete(s); console.warn('word shard error:', s, e); }));
  return shards.get(s);
}
async function getWordDict(pageData) {
  if (!shardIdx) shardIdx = await json('data/words/index.json').catch(() => ({}));
  if (pageData) {
    const need = new Set();
    for (const ln of Object.values(pageData.lines || {})) {
      if (ln?.type !== 'ayah') continue;
      for (const id of [ln.first_word_id, ln.last_word_id])
        for (const [s, [lo, hi]] of Object.entries(shardIdx)) if (id >= lo && id <= hi) need.add(s);
    }
    await Promise.all([...need].map(loadShard));
  }
  return wordDict;
}

async function getPageData(pg) {
  const ck = layout.id + ':' + pg;
  if (pageCache.has(ck)) return pageCache.get(ck);
  try {
    const p = await json(`${layout.pages_dir}/${pg}.json`);
    pageCache.set(ck, p);
    return p;
  } catch (e) {
    console.error('Failed to load page:', pg, e);
    return null;
  }
}

async function getSvg(url) {
  if (svgTemplates.has(url)) return svgTemplates.get(url);
  const text = await fetch(new URL(url, ROOT)).then(r => r.text());
  svgTemplates.set(url, text);
  return text;
}

// A layout is "available" when it has no info file (built-in) or its info.json exists and parses.
function layoutAvailable(L) {
  if (!L.info) return Promise.resolve(true);
  if (!layoutOk.has(L.id)) {
    layoutOk.set(L.id, json(L.info).then(info => {
      if (info && info.pages) L.pages = info.pages;
      return !!(info && info.pages);
    }).catch(() => false));
  }
  return layoutOk.get(L.id);
}

// Loads the surah→page map and juz list of a layout and fills the pickers.
async function activateLayout(L) {
  const [sp, jz] = await Promise.all([
    json(L.surah_pages).catch(() => ({})),
    json(L.juz_file).catch(() => ([]))
  ]);
  layout = L; surahPages = sp; juzs = jz;
  state.layout = L.id;
  const jSel = $('#juzSelect');
  if (jSel) {
    jSel.innerHTML = juzs.map(j => `<option value="${j.id}">পারা ${bn(j.id)}</option>`).join('');
    jSel.hidden = !juzs.length;
  }
  const total = $('#pageTotal'); if (total) total.textContent = bn(L.pages);
  const inp = $('#pageJumpInput'); if (inp) inp.setAttribute('aria-label', `পৃষ্ঠা নম্বর (১ থেকে ${bn(L.pages)})`);
}

// ---------- Rendering helpers ----------
function rectOf(svg, id) {
  const e = svg.querySelector('#' + id); if (!e) return null;
  return { x: +e.getAttribute('x'), y: +e.getAttribute('y'), w: +e.getAttribute('width'), h: +e.getAttribute('height') };
}

function put(el, r, vb) {
  Object.assign(el.style, {
    left: (r.x / vb.width * 100) + '%',
    top: (r.y / vb.height * 100) + '%',
    width: (r.w / vb.width * 100) + '%',
    height: (r.h / vb.height * 100) + '%'
  });
  return el;
}

function mk(tag, cls, html) {
  const e = document.createElement(tag);
  if (cls) e.className = cls;
  if (html != null) e.innerHTML = html;
  return e;
}

// ---------- Tajweed colouring ----------
// Preserves 100% continuous IndoPak calligraphy and multi-character OpenType ligatures.
// Instead of splitting words across inline spans (which destroys HarfBuzz ligatures and
// detaches combining marks like maddah/sukun), we detect the dominant Tajweed rule in the word
// and wrap the entire word in the corresponding Tajweed class.
function colored(text) {
  if (!text) return '';
  if (!state.tajweed) return esc(text);

  // 1. Madd Lazim: maddah with shaddah (e.g. الضَّآلِّیْنَ, الٓمّٓ)
  if (/(?:[\u0653\u06E4].*ّ|ّ.*[\u0653\u06E4])/.test(text)) {
    return `<span class="tj-madd-lazim">${esc(text)}</span>`;
  }
  // 2. Madd (Monfasil / Mottasil): madd sign (e.g. قَالُوْۤا, سَوَآءٌ, بِمَاۤ)
  if (/[\u0653\u06E4]/.test(text)) {
    return `<span class="tj-madd">${esc(text)}</span>`;
  }
  // 3. Ghunnah: Noon or Meem with shaddah (e.g. وَمِمَّا, اِنَّ, مِّنْ)
  if (/[نم]\u0651|ّ[نم]/.test(text)) {
    return `<span class="tj-ghunna">${esc(text)}</span>`;
  }
  // 4. Qalqalah: Qutb-Jad with sukun (e.g. رَزَقْنٰهُمْ, یُبْصِرُوْنَ, یَجْعَلُوْنَ)
  if (/[قطبجد][\u0652\u065Aْ]/.test(text)) {
    return `<span class="tj-qalqalah">${esc(text)}</span>`;
  }
  return esc(text);
}

// Reference ratios (800-unit page width): Arabic font = 46 units; Bengali sub-row font = 18 units.
// The Bengali sub-row is ALWAYS one single line: shrink to fit, and if still too long, cut with "…".
const AR_FONT_U = 46;
// Bengali sub-row font follows CSS clamp(11px, 2.8vw, 15px): never below 11px, never above 15px.
const SUB_MIN_PX = 11, SUB_MAX_PX = 15, SUB_VW = 2.8;
const subFontPx = () => Math.min(SUB_MAX_PX, Math.max(SUB_MIN_PX, window.innerWidth * SUB_VW / 100));

async function renderNow() {
  const stage = $('#stage');
  const pageData = await getPageData(state.page);
  if (!pageData) {
    toast(`পৃষ্ঠা ${bn(state.page)} পাওয়া যায়নি`);
    return;
  }

  const dict = await getWordDict(pageData);
  const withSub = state.mode !== 'off';
  const NL = layout.lines;

  // Only Page 1 (Surah Al-Fatiha) and Page 2 (Surah Al-Baqarah 1-5) use the Ornate Opening Arch frame
  const isOpeningArch = (state.page === 1 || state.page === 2);

  let bgUrl;
  if (isOpeningArch) {
    bgUrl = withSub
      ? (layout.bg_start_translation || 'assets/bg/page-bg-surah-start-15-sub.svg')
      : (layout.bg_start_arabic || 'assets/bg/page-bg-surah-start-15-ar.svg');
  } else {
    bgUrl = withSub
      ? (layout.bg_std_translation || `assets/bg/page-bg-standard-${NL}-sub.svg`)
      : (layout.bg_std_arabic || `assets/bg/page-bg-standard-${NL}-ar.svg`);
  }
  const svgText = await getSvg(bgUrl);

  stage.className = `stage mode-${state.mode}`;
  stage.innerHTML = `<div class="bg">${svgText}</div><div class="overlay"></div>`;
  const svg = $('svg', stage), ov = $('.overlay', stage), vb = svg.viewBox.baseVal;

  // Header computation: first surah/ayah on the page
  let firstSurahId = 1, firstAyahNum = 1;
  for (let l = 1; l <= NL; l++) {
    const ln = pageData.lines?.[String(l)];
    if (ln?.type === 'surah_name' && ln.surah_number) {
      firstSurahId = ln.surah_number; break;
    } else if (ln?.type === 'ayah' && ln.first_word_id && dict[ln.first_word_id]) {
      firstSurahId = parseInt(dict[ln.first_word_id].surah, 10);
      firstAyahNum = parseInt(dict[ln.first_word_id].ayah, 10);
      break;
    }
  }
  state.pageFirstAyah = `${firstSurahId}:${firstAyahNum}`;

  const surahInfo = surahs[firstSurahId] || { ar: 'الفاتحة', bn: 'আল-ফাতিহা', ayahs: 7, type: 'মাক্কী' };
  const juzId = juzs.length ? (juzs.filter(j => j.start_page <= state.page).pop()?.id || 1) : Math.min(30, Math.max(1, Math.floor((state.page - 2) / 20) + 1));
  const juzInfo = juzs.find(j => j.id === juzId) || { id: juzId, ar: 'الجزء', bn: `পারা ${bn(juzId)}` };

  const setSlot = (id, html) => {
    const r = rectOf(svg, id);
    if (r) ov.append(put(mk('div', 'slot hdr', html), r, vb));
  };

  // Printed (Emdadia) page number only exists for the 15-line edition
  const printedNum = layout.id === 'hafezi15' ? (state.page === 1 ? 1 : state.page + 1) : state.page;
  setSlot('slot-header-left', `<span class="hdr-ar">${surahInfo.ar}</span> <span>${bn(firstSurahId)}</span>`);
  setSlot('slot-header-center', `<span class="hdr-pg-num">${bn(state.page)}</span>` + (layout.id === 'hafezi15' ? ` <span class="hdr-pg-ar">(${bn(printedNum)})</span>` : ''));
  setSlot('slot-header-right', `<span class="hdr-ar">${juzInfo.ar}</span> <span>${bn(juzId)}</span>`);
  setSlot('slot-footer-center', `<span class="hdr-pg-ar">${bn(printedNum)}</span>`);

  if (state.tajweed) {
    const rCenter = rectOf(svg, 'slot-footer-center');
    if (rCenter) {
      const rLeft = { x: 75, y: rCenter.y, w: Math.max(140, rCenter.x - 85), h: rCenter.h };
      const rRight = { x: rCenter.x + rCenter.w + 10, y: rCenter.y, w: Math.max(140, vb.width - (rCenter.x + rCenter.w + 85)), h: rCenter.h };

      const rightHtml = `
        <span><i style="background:var(--tj-madd-lazim)"></i>মাদ্দ লাযিম</span>
        <span><i style="background:var(--tj-madd)"></i>মাদ্দ</span>
        <span><i style="background:var(--tj-ghunna)"></i>গুন্না</span>
      `;
      const leftHtml = `
        <span><i style="background:var(--tj-qalqalah)"></i>কলকলা</span>
        <span><i style="background:var(--tj-ikhfa)"></i>ইখফা</span>
        <span><i style="background:var(--tj-idgham)"></i>ঈদগাম</span>
      `;
      ov.append(put(mk('div', 'slot ftr-legend', rightHtml), rRight, vb));
      ov.append(put(mk('div', 'slot ftr-legend', leftHtml), rLeft, vb));
    }
  }

  let missingSub = 0;
  const lineEls = [];

  for (let l = 1; l <= NL; l++) {
    const pad = String(l).padStart(2, '0');
    const ln = pageData.lines?.[String(l)];
    const eLine = svg.querySelector('#line-' + pad), eSub = svg.querySelector('#sub-' + pad);
    const rLine = rectOf(svg, 'line-' + pad);
    const rSub = withSub ? rectOf(svg, 'sub-' + pad) : null;
    if (!rLine) continue;

    // Rows that carry no content on this page (e.g. pages 1-2) are not drawn
    if (!ln) { eLine?.remove(); eSub?.remove(); continue; }

    const rFull = { x: rLine.x, y: rLine.y, w: rLine.w, h: rSub ? (rSub.y + rSub.h - rLine.y) : rLine.h };

    if (ln.type === 'surah_name') {
      eSub?.remove();
      const sNum = ln.surah_number || firstSurahId;
      const s = surahs[sNum] || surahInfo;
      const bannerBox = mk('div', 'line-box');
      put(bannerBox, rFull, vb);
      bannerBox.innerHTML = `
        <div class="surah-banner">
          <div class="sb-box"><span>সূরা</span><b>${bn(sNum)}</b></div>
          <div class="sb-center">
            <span class="sb-title">سُوْرَةُ ${s.ar}</span>
            <span class="sb-type">(${s.type || 'মাক্কী'})</span>
          </div>
          <div class="sb-box"><span>আয়াত</span><b>${bn(s.ayahs)}</b></div>
        </div>`;
      ov.append(bannerBox);
    } else if (ln.type === 'basmallah') {
      eSub?.remove();
      const bismillahBox = mk('div', 'line-box');
      put(bismillahBox, rFull, vb);
      bismillahBox.innerHTML = `
        <div class="basmallah-banner">
          <span class="bismillah-calligraphy">بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِیْمِ ۝</span>
        </div>`;
      ov.append(bismillahBox);
    } else if (ln.type === 'ayah') {
      // One container per row; every word is a column cell: Arabic on top, meaning right underneath.
      const row = mk('div', `line-text ${ln.alignment === 'centered' ? 'centered' : ''}`);
      put(row, rFull, vb);
      row.style.setProperty('--arh', (rLine.h / rFull.h * 100) + '%');

      const totalWords = ln.data?.length || 0;
      for (let i = 0; i < totalWords; i++) {
        const rawText = ln.data[i];
        const wordId = ln.first_word_id + i;
        const wMeta = dict[wordId] || { loc: `${firstSurahId}:${firstAyahNum}:${i + 1}`, ayah: String(firstAyahNum), surah: String(firstSurahId) };
        const loc = wMeta.loc || `${wMeta.surah}:${wMeta.ayah}:${wMeta.word}`;
        const isEnd = rawText.includes('۝') || /[-]/.test(rawText) || wMeta.word === 'end';

        if (isEnd) {
          const cell = mk('span', 'wc end');
          const endEl = mk('span', 'ay-end', `<i>${bn(wMeta.ayah || firstAyahNum)}</i>`);
          endEl.dataset.ayah = `${wMeta.surah || firstSurahId}:${wMeta.ayah || firstAyahNum}`;
          endEl.title = 'তাফসীর দেখতে ট্যাপ করুন';
          cell.append(endEl);
          row.append(cell);
          continue;
        }

        const isSelected = state.word === String(wordId);
        const isAyahActive = state.ayah === `${wMeta.surah}:${wMeta.ayah}`;
        const hl = (isSelected ? ' w-sel selected' : '') + (isAyahActive && !isSelected ? ' ays' : '');

        const wbw = wbwData[loc];
        const meaning = wbw?.m || '';
        const translit = wbw?.t || '';
        const subText = state.mode === 'translation' ? meaning : translit;
        if (withSub && !subText) missingSub++;

        const cell = mk('span', 'wc');
        const setData = el => {
          el.dataset.id = wordId;
          el.dataset.ayah = `${wMeta.surah}:${wMeta.ayah}`;
          el.dataset.ar = rawText;
          el.dataset.m = meaning;
          el.dataset.t = translit;
        };

        const wAr = mk('span', 'w' + hl);
        setData(wAr);
        wAr.innerHTML = colored(rawText);
        cell.append(wAr);

        if (withSub) {
          const wSub = mk('span', 'sub-w' + (state.mode === 'translit' ? ' translit' : '') + hl);
          setData(wSub);
          wSub.append(mk('em', null, esc(subText || '…')));
          cell.append(wSub);
        }
        row.append(cell);
      }

      ov.append(row);
      lineEls.push({ row, rLine, rFull });
    }
  }

  if (missingSub && !renderNow.warned?.[state.mode]) {
    (renderNow.warned ||= {})[state.mode] = true;
    toast(`এই পৃষ্ঠার কিছু শব্দের ${state.mode === 'translation' ? 'অর্থ' : 'উচ্চারণ'} এখনো যুক্ত হয়নি`);
  }

  // Fit: Arabic keeps the reference font ratio (shrinks only if a line is crowded);
  // each Bengali word sits in ONE line under its own Arabic word.
  const fit = () => {
    const u = stage.clientWidth / vb.width;
    stage.style.setProperty('--u', u + 'px');

    for (const { row } of lineEls) {
      let fs = AR_FONT_U * u;
      row.style.fontSize = fs + 'px';
      for (let k = 0; k < 6 && row.scrollWidth > row.clientWidth + 1 && fs > 9; k++) {
        fs *= Math.max(0.8, (row.clientWidth / row.scrollWidth) * 0.995);
        row.style.fontSize = fs + 'px';
      }
      if (!state.mode || state.mode === 'off') continue;

      const cells = [...row.querySelectorAll('.wc')];
      const widths = cells.map(c => c.offsetWidth);
      const free = Math.max(0, row.clientWidth - widths.reduce((a, b) => a + b, 0));
      const gap = row.classList.contains('centered') ? 0 : free / Math.max(1, cells.length - 1);

      cells.forEach((cell, idx) => {
        const sub = cell.querySelector('.sub-w'); if (!sub) return;
        const em = sub.firstElementChild;
        const edge = idx === 0 || idx === cells.length - 1;
        const availW = widths[idx] + gap * (edge ? 0.3 : 0.7);
        sub.classList.remove('wrap2');
        em.style.whiteSpace = 'nowrap'; em.style.width = 'max-content'; em.style.maxWidth = 'none';
        // start at the clamped size; shrink ONLY down to the 11px floor, never below
        let f = subFontPx();
        em.style.fontSize = f + 'px';
        if (em.offsetWidth > availW) {
          f = Math.max(SUB_MIN_PX, f * (availW / em.offsetWidth) * 0.97);
          em.style.fontSize = f + 'px';
          if (em.offsetWidth > availW + 0.5) {
            // still too wide at the floor: wrap onto 2 lines (no "…" truncation).
            // A single long word simply overflows its cell, centred, and stays fully readable.
            sub.classList.add('wrap2');
            em.style.whiteSpace = 'normal'; em.style.width = availW + 'px';
          }
        }
      });
    }
  };

  fit();
  document.fonts?.ready?.then(fit);
  stage._ro?.disconnect();
  stage._ro = new ResizeObserver(fit);
  stage._ro.observe(stage);

  $('#pageTitle').textContent = `পৃষ্ঠা ${bn(state.page)}`;
  const jumpInp = $('#pageJumpInput');
  if (jumpInp && document.activeElement !== jumpInp) jumpInp.value = bn(state.page);
  const sSel = $('#surahSelect');
  if (sSel) sSel.value = firstSurahId;
  const jSel = $('#juzSelect');
  if (jSel) jSel.value = juzId;
}

const render = () => (rendering = rendering.then(renderNow).catch(e => console.error(e)));

// ---------- Navigation ----------
function goPage(p) {
  p = Math.min(layout.pages, Math.max(1, parseInt(toEn(p), 10) || 1));
  if (p === state.page && $('#stage').firstChild) return;
  state.page = p; state.pageBy[layout.id] = p; persist(); render();
  window.scrollTo({ top: 0 });
}

function currentSurahId() {
  const k = state.pageFirstAyah || '1:1';
  return parseInt(k.split(':')[0], 10) || 1;
}

async function setLayout(id, { silent = false } = {}) {
  const L = layouts.find(x => x.id === id);
  if (!L) return { ok: false, error: 'unknown_layout' };
  if (layout && L.id === layout.id) return { ok: true, layout: L.id };
  if (!(await layoutAvailable(L))) {
    if (!silent) toast(`হাফেজী ${L.label_bn}-এর পৃষ্ঠা-ডেটা এখনো যুক্ত হয়নি`);
    return { ok: false, error: 'layout_data_missing' };
  }
  const surahNow = currentSurahId();
  await activateLayout(L);
  state.page = state.pageBy[L.id] || surahPages[surahNow] || 1;
  state.pageBy[L.id] = state.page;
  persist(); syncControls(); render();
  window.scrollTo({ top: 0 });
  return { ok: true, layout: L.id, page: state.page };
}

// ---------- Word sheet ----------
function openWordModal(wEl) {
  const modal = $('#wordModal');
  if (!modal || !wEl) return;
  const ar = wEl.dataset.ar || '';
  const m = wEl.dataset.m || '';
  const t = wEl.dataset.t || '';
  const ayahKey = wEl.dataset.ayah || '';
  const ayahFull = fullTranslations[ayahKey] || '';

  $('#sheetWordAr').textContent = ar;
  $('#sheetWordSub').textContent = m ? `বাংলা অর্থ: ${m}` : 'এই শব্দের অর্থ এখনো যুক্ত হয়নি';
  $('#sheetWordTranslit').textContent = t ? `উচ্চারণ: ${t}` : '';
  $('#sheetAyahRef').textContent = `আয়াত ${bn(ayahKey.replace(':', ' : '))} এর পূর্ণ অনুবাদ:`;
  $('#sheetAyahFull').textContent = ayahFull || 'পূর্ণ অনুবাদ পাওয়া যায়নি';
  state.sheetAyah = ayahKey;
  modal.classList.add('show');
}

// ---------- Tafsir ----------
function tafsirSource(id = state.tafsir) { return tafsirSources.find(s => s.id === id) || tafsirSources[0]; }

function tafsirAvailable(src) {
  if (src.kind === 'translation') return Promise.resolve(true);
  if (!tafsirOk.has(src.id)) {
    tafsirOk.set(src.id, json(`${src.dir}/info.json`).then(i => !!i).catch(() => false));
  }
  return tafsirOk.get(src.id);
}

async function tafsirText(src, key) {
  const [s, a] = key.split(':');
  if (src.kind === 'translation') return fullTranslations[key] || '';
  const ck = `${src.id}:${s}`;
  if (!tafsirCache.has(ck)) tafsirCache.set(ck, json(`${src.dir}/${s}.json`).catch(() => null));
  const data = await tafsirCache.get(ck);
  const v = data?.[a];
  return typeof v === 'string' ? v : (v?.text || '');
}

async function ayahArabic(s, a) {
  await loadShard(s);
  const out = [];
  for (let n = 1; n < 400; n++) {
    const w = wordLoc[`${s}:${a}:${n}`];
    if (!w) break;
    if (/[-]/.test(w.text) || w.text.includes('۝')) continue;
    out.push(w.text);
  }
  return out.join(' ');
}

function nextAyahKey(key, d) {
  let [s, a] = key.split(':').map(Number);
  const count = n => surahs[n]?.ayahs || 0;
  a += d;
  if (a < 1) { if (s <= 1) return null; s -= 1; a = count(s); }
  else if (a > count(s)) { if (s >= 114) return null; s += 1; a = 1; }
  return a ? `${s}:${a}` : null;
}

async function renderTafsir() {
  const key = state.tfAyah; if (!key) return;
  const tok = renderTafsir.tok = (renderTafsir.tok || 0) + 1;
  const [s, a] = key.split(':').map(Number);
  const si = surahs[s] || {};
  const src = tafsirSource();

  $('#tfRef').innerHTML = `<span>সূরা ${esc(si.bn || bn(s))}</span><small>আয়াত ${bn(a)}${si.ayahs ? ' / ' + bn(si.ayahs) : ''}</small>`;
  $('#tfPrev').disabled = !nextAyahKey(key, -1);
  $('#tfNext').disabled = !nextAyahKey(key, 1);
  $('.tafsir-sheet').style.setProperty('--tf', state.tfSize);
  $('#tfSizeVal').textContent = bn(Math.round(state.tfSize * 100)) + '%';

  // source chips
  const oks = await Promise.all(tafsirSources.map(tafsirAvailable));
  if (tok !== renderTafsir.tok) return;
  $('#tfSources').innerHTML = tafsirSources.map((t, i) =>
    `<button class="tf-src${t.id === src.id ? ' on' : ''}${oks[i] ? '' : ' na'}" data-src="${esc(t.id)}" role="radio" aria-checked="${t.id === src.id}">
       <b>${esc(t.title_bn)}</b><small>${oks[i] ? esc(t.author_bn || '') : 'ডেটা যুক্ত হয়নি'}</small></button>`).join('');
  $('#tfSources .on')?.scrollIntoView({ block: 'nearest', inline: 'center' });

  $('#tfTitle').textContent = src.title_bn;
  $('#tfAuthor').textContent = src.author_bn ? `— ${src.author_bn}` : '';
  const arEl = $('#tfArabic'); arEl.textContent = '';
  ayahArabic(s, a).then(t => { if (tok === renderTafsir.tok) arEl.textContent = t; });

  const bodyEl = $('#tfBody');
  const ok = oks[tafsirSources.indexOf(src)];
  if (!ok) {
    bodyEl.innerHTML = `<p class="tf-empty">এই তাফসীরের ডেটা প্যাক এখনো যুক্ত করা হয়নি।<br>প্যাক যুক্ত হলে এখানে প্রতিটি আয়াতের তাফসীর দেখা যাবে। এখনকার জন্য <b>বাংলা অনুবাদ</b> বেছে নিন।</p>`;
    return;
  }
  bodyEl.innerHTML = '<p class="tf-empty">লোড হচ্ছে…</p>';
  const text = await tafsirText(src, key);
  if (tok !== renderTafsir.tok) return;
  bodyEl.innerHTML = text
    ? text.split(/\n{2,}/).map(p => `<p>${esc(p).replace(/\n/g, '<br>')}</p>`).join('')
    : '<p class="tf-empty">এই আয়াতের জন্য এই উৎসে কোনো লেখা পাওয়া যায়নি।</p>';
  $('#tfScroll').scrollTop = 0;
}

function openTafsir(key) {
  if (!/^\d+:\d+$/.test(key || '')) return;
  state.tfAyah = key;
  closeSheet('wordModal');
  openSheet('tafsirModal');
  renderTafsir();
}

function setTafsir(id) {
  const src = tafsirSources.find(s => s.id === id);
  if (!src) return { ok: false, error: 'unknown_tafsir' };
  state.tafsir = src.id; persist(); syncControls();
  if ($('#tafsirModal').classList.contains('show')) renderTafsir();
  return { ok: true, tafsir: src.id };
}

// ---------- Reading UI: zoom, theme, bold, wake-lock ----------
const ZMIN = 0.8, ZMAX = 2.6;
const clampZ = z => Math.min(ZMAX, Math.max(ZMIN, Math.round(z * 100) / 100));
const pct = z => bn(Math.round(z * 100)) + '%';

function applyZoom(z, anchorX) {
  const rd = $('#reader'), stage = $('#stage');
  if (!rd || !stage) return;
  z = clampZ(z);
  const cx = anchorX ?? rd.clientWidth / 2;
  const before = stage.clientWidth || 1;
  const ratio = (rd.scrollLeft + cx) / before;
  state.zoom = z;
  stage.style.width = (z * 100) + '%';
  rd.classList.toggle('zoomed', z > 1.02);
  rd.scrollLeft = Math.max(0, ratio * stage.clientWidth - cx);
  const badge = $('#zoomBadge');
  if (badge) { badge.textContent = pct(z); badge.classList.add('show'); clearTimeout(applyZoom.t); applyZoom.t = setTimeout(() => badge.classList.remove('show'), 900); }
  const zr = $('#zoomRange'); if (zr) zr.value = Math.round(z * 100);
  const zv = $('#zoomVal'); if (zv) zv.textContent = pct(z);
  persist();
}

function applyTheme() {
  document.body.dataset.theme = state.theme;
  document.body.classList.toggle('bold-ar', !!state.bold);
  const meta = document.querySelector('meta[name="theme-color"]');
  if (meta) meta.content = state.theme === 'dark' ? '#0b1411' : state.theme === 'sepia' ? '#8a6a3a' : '#0f5c47';
}

let wakeLock = null;
async function setWake(on) {
  state.wake = on;
  try {
    if (on && 'wakeLock' in navigator) wakeLock = await navigator.wakeLock.request('screen');
    else if (wakeLock) { await wakeLock.release(); wakeLock = null; }
  } catch { toast('এই ডিভাইসে স্ক্রিন জাগিয়ে রাখা যাচ্ছে না'); }
  persist();
}
document.addEventListener('visibilitychange', () => { if (document.visibilityState === 'visible' && state.wake) setWake(true); });

function syncControls() {
  $('#planLabel').textContent = state.plan === 'paid' ? 'পেইড সাবস্ক্রিপশন' : '';
  for (const b of document.querySelectorAll('#modeSeg button')) {
    const on = b.dataset.mode === state.mode;
    b.classList.toggle('on', on); b.setAttribute('aria-selected', on);
  }
  for (const b of document.querySelectorAll('#layoutSeg button')) {
    const on = layout && b.dataset.layout === layout.id;
    b.classList.toggle('on', !!on); b.setAttribute('aria-checked', !!on);
  }
  const src = tafsirSource();
  if (src) $('#tafsirName').textContent = src.title_bn;
  const t = $('#tajweedBtn');
  if (t) { t.classList.toggle('on', state.tajweed); t.setAttribute('aria-pressed', state.tajweed); }
  const lg = $('#legend'); if (lg) lg.hidden = !state.tajweed;
  // Sync desktop side panels
  if (window._syncTjSidePanels) window._syncTjSidePanels();
  for (const b of document.querySelectorAll('#themeRow button')) b.classList.toggle('on', b.dataset.theme === state.theme);
  const bt = $('#boldToggle'); if (bt) bt.checked = !!state.bold;
  const wt = $('#wakeToggle'); if (wt) wt.checked = !!state.wake;
}

const openSheet = id => $('#' + id)?.classList.add('show');
const closeSheet = id => $('#' + id)?.classList.remove('show');

// ---------- Agent API (same commands the UI uses) ----------
const commands = {
  get_state: () => ({
    ok: true, page: state.page, layout: layout.id, lines: layout.lines, total_pages: layout.pages,
    mode: state.mode, tajweed: state.tajweed, plan: state.plan, tafsir: state.tafsir,
    first_ayah_on_page: state.pageFirstAyah, zoom: state.zoom, theme: state.theme
  }),
  set_mode: ({ mode }) => {
    if (!['off', 'translation', 'translit'].includes(mode)) return { ok: false, error: 'bad_mode' };
    state.mode = mode; persist(); syncControls(); render(); return { ok: true, mode };
  },
  set_tajweed: ({ on }) => { state.tajweed = !!on; persist(); syncControls(); render(); return { ok: true, tajweed: state.tajweed }; },
  goto_page: ({ page }) => { goPage(page); return { ok: true, page: state.page }; },
  select_ayah: ({ ayah }) => {
    if (!/^\d+:\d+$/.test(ayah || '')) return { ok: false, error: 'bad_ayah' };
    state.ayah = ayah; state.word = null; render(); return { ok: true, ayah };
  },
  list_layouts: async () => ({
    ok: true, current: layout.id,
    layouts: await Promise.all(layouts.map(async L => ({ id: L.id, label_bn: L.label_bn, lines: L.lines, available: await layoutAvailable(L), pages: L.pages })))
  }),
  set_layout: ({ id }) => setLayout(id),
  list_tafsir: async () => ({
    ok: true, current: state.tafsir,
    sources: await Promise.all(tafsirSources.map(async s => ({ id: s.id, title_bn: s.title_bn, available: await tafsirAvailable(s) })))
  }),
  select_tafsir: ({ id }) => setTafsir(id),
  open_tafsir: ({ ayah }) => { openTafsir(ayah); return { ok: true, ayah }; },
  get_tafsir: async ({ ayah, id }) => {
    if (!/^\d+:\d+$/.test(ayah || '')) return { ok: false, error: 'bad_ayah' };
    const src = tafsirSource(id || state.tafsir);
    if (!(await tafsirAvailable(src))) return { ok: false, error: 'tafsir_data_missing', tafsir: src.id };
    return { ok: true, ayah, tafsir: src.id, text: await tafsirText(src, ayah) };
  },
  list_packs: async () => ({
    ok: true, packs: manifest?.packs || [],
    tafsir: await Promise.all(tafsirSources.map(async s => ({ id: s.id, available: await tafsirAvailable(s) }))),
    layouts: await Promise.all(layouts.map(async L => ({ id: L.id, available: await layoutAvailable(L) })))
  })
};

window.QuranApp = {
  tools: null,
  async exec(name, args = {}) {
    const fn = commands[name];
    if (!fn) return { ok: false, error: 'unknown_command' };
    try { return await fn(args); } catch (e) { return { ok: false, error: String(e.message || e) }; }
  }
};
json('agent/tools.json').then(t => { window.QuranApp.tools = t; }).catch(() => {});

// ---------- Boot ----------
async function boot() {
  try {
    const [ent_, tj, m, surahs_, wbw_, trans_, lay_, taf_] = await Promise.all([
      json('config/entitlements.json').catch(() => ({})),
      json('config/tajweed.json').catch(() => ([])),
      json('data/manifest.json').catch(() => ({ packs: [] })),
      json('data/surahs.json').catch(() => ({})),
      json('data/bn-wbw-full.json').catch(() => ({})),
      json('data/bn-translation.json').catch(() => ({ data: { ayahs: {} } })),
      json('config/layouts.json').catch(() => ({ default: 'hafezi15', layouts: [FALLBACK_LAYOUT] })),
      json('config/tafsir.json').catch(() => ({ sources: [{ id: 'bn-translation', kind: 'translation', title_bn: 'বাংলা অনুবাদ', author_bn: '' }] }))
    ]);

    cfg = { ent: ent_ };
    manifest = m; surahs = surahs_; wbwData = wbw_;
    fullTranslations = trans_.data?.ayahs || {};
    layouts = lay_.layouts?.length ? lay_.layouts : [FALLBACK_LAYOUT];
    tafsirSources = taf_.sources?.length ? taf_.sources : [{ id: 'bn-translation', kind: 'translation', title_bn: 'বাংলা অনুবাদ' }];
    if (!tafsirSources.some(s => s.id === state.tafsir)) state.tafsir = taf_.default || tafsirSources[0].id;

    // Pick a layout that really has data; otherwise fall back to the default one
    let L = layouts.find(x => x.id === state.layout) || layouts.find(x => x.id === lay_.default) || layouts[0];
    if (!(await layoutAvailable(L))) L = layouts.find(x => x.id === lay_.default) || layouts[0];
    await activateLayout(L);
    state.page = Math.min(L.pages, Math.max(1, state.page || state.pageBy[L.id] || 1));
    state.pageBy[L.id] = state.page;

    // Layout buttons (from the registry) + availability badges
    const lSeg = $('#layoutSeg');
    lSeg.innerHTML = layouts.map(x => `<button data-layout="${esc(x.id)}" role="radio"><b>হাফেজী ${esc(x.label_bn)}</b><small hidden>ডেটা যুক্ত হয়নি</small></button>`).join('');
    layouts.forEach(async x => {
      const ok = await layoutAvailable(x);
      const b = lSeg.querySelector(`[data-layout="${x.id}"]`);
      b?.classList.toggle('na', !ok); b?.querySelector('small')?.toggleAttribute('hidden', ok);
    });
    lSeg.onclick = e => { const b = e.target.closest('button[data-layout]'); if (b) setLayout(b.dataset.layout); };

    // Tajweed legend (shown only while colours are on)
    const lg = $('#legend');
    if (lg && Array.isArray(tj)) {
      lg.innerHTML = tj.map(r => `<span><i style="background:var(--tj-${r.cls})"></i>${r.bn}</span>`).join('');
    }

    // Side panel tajweed legend (desktop sidebar — right & left of page)
    // Right: মাদ্দ লাজিম, মাদ্দ, গুন্নাহ   Left: কলকলা, ইখফা, ইদগাম (নীরব)
    if (Array.isArray(tj) && tj.length) {
      const rightClasses = ['madd-lazim', 'madd', 'ghunna'];
      const leftClasses  = ['qalqalah', 'ikhfa', 'idgham', 'silent'];
      const makeItem = r => `<div class="tj-side-item"><span class="tj-side-dot" style="background:var(--tj-${r.cls})"></span>${r.bn}</div>`;
      const rightItems = tj.filter(r => rightClasses.some(c => r.cls === c));
      const leftItems  = tj.filter(r => leftClasses.some(c => r.cls === c));
      const ri = $('#tjSideRightItems'), li = $('#tjSideLeftItems');
      if (ri) ri.innerHTML = rightItems.map(makeItem).join('');
      if (li) li.innerHTML = leftItems.map(makeItem).join('');
    }
    // Sync side panels visibility with tajweed toggle
    function syncTjSidePanels() {
      const on = state.tajweed;
      const r = $('#tjSideRight'), l = $('#tjSideLeft');
      if (r) r.hidden = !on;
      if (l) l.hidden = !on;
    }
    // Store ref for syncControls to call
    window._syncTjSidePanels = syncTjSidePanels;
    syncTjSidePanels();

    // Surah + Juz pickers (exact page maps of the active layout)
    const sSel = $('#surahSelect');
    if (sSel && Object.keys(surahs).length) {
      sSel.innerHTML = Object.values(surahs).map(s => `<option value="${s.id}">${bn(s.id)}. ${s.bn} — ${s.ar}</option>`).join('');
      sSel.onchange = e => goPage(surahPages[e.target.value] || 1);
    }
    $('#juzSelect').onchange = e => goPage(juzs.find(j => String(j.id) === e.target.value)?.start_page || 1);

    syncControls(); applyTheme();
    applyZoom(state.zoom || 1);
    if (state.wake) setWake(true);

    if ('serviceWorker' in navigator && location.protocol !== 'file:') navigator.serviceWorker.register('sw.js').catch(() => {});

    // Page navigation (Arabic book order: "পরের" is on the left)
    const pBtn = $('#prev'), nBtn = $('#next');
    pBtn.onclick = () => goPage(state.page - 1);
    nBtn.onclick = () => goPage(state.page + 1);
    const jInp = $('#pageJumpInput');
    jInp.onchange = () => { goPage(jInp.value); jInp.value = bn(state.page); };
    jInp.onkeydown = e => { if (e.key === 'Enter') { goPage(jInp.value); jInp.blur(); } };
    jInp.onfocus = () => jInp.select();

    // Display mode
    $('#modeSeg').onclick = e => {
      const btn = e.target.closest('button[data-mode]'); if (!btn) return;
      state.mode = btn.dataset.mode; persist(); syncControls(); render();
    };
    $('#tajweedBtn').onclick = () => { state.tajweed = !state.tajweed; persist(); syncControls(); render(); };

    // Tafsir
    $('#tafsirBtn').onclick = () => openTafsir(state.tfAyah || state.ayah || state.pageFirstAyah || '1:1');
    $('#tfSources').onclick = e => { const b = e.target.closest('button[data-src]'); if (b) setTafsir(b.dataset.src); };
    $('#tfPrev').onclick = () => { const k = nextAyahKey(state.tfAyah, -1); if (k) { state.tfAyah = k; renderTafsir(); } };
    $('#tfNext').onclick = () => { const k = nextAyahKey(state.tfAyah, 1); if (k) { state.tfAyah = k; renderTafsir(); } };
    const tfStep = d => { state.tfSize = Math.min(1.8, Math.max(0.8, Math.round((state.tfSize + d) * 10) / 10)); persist(); renderTafsir(); };
    $('#tfSmaller').onclick = () => tfStep(-0.1);
    $('#tfBigger').onclick = () => tfStep(0.1);
    $('#openTafsirFromWord').onclick = () => openTafsir(state.sheetAyah);

    // Zoom controls
    const step = d => applyZoom(state.zoom + d);
    $('#zoomIn').onclick = () => step(0.1);   $('#zoomOut').onclick = () => step(-0.1);
    $('#zoomIn2').onclick = () => step(0.1);  $('#zoomOut2').onclick = () => step(-0.1);
    $('#zoomRange').oninput = e => applyZoom(parseInt(e.target.value, 10) / 100);
    for (const b of document.querySelectorAll('.preset-row button')) b.onclick = () => applyZoom(parseInt(b.dataset.zoom, 10) / 100);

    // Settings sheet
    $('#settingsBtn').onclick = () => openSheet('settingsModal');
    $('#themeRow').onclick = e => { const b = e.target.closest('button[data-theme]'); if (!b) return; state.theme = b.dataset.theme; applyTheme(); syncControls(); persist(); };
    $('#boldToggle').onchange = e => { state.bold = e.target.checked; applyTheme(); persist(); };
    const wr = $('#wakeRow');
    if (!('wakeLock' in navigator)) wr.hidden = true;
    $('#wakeToggle').onchange = e => setWake(e.target.checked);

    // Generic sheet closing
    for (const b of document.querySelectorAll('[data-close]')) b.onclick = () => closeSheet(b.dataset.close);
    for (const id of ['wordModal', 'settingsModal', 'tafsirModal']) {
      const o = $('#' + id); o.onclick = e => { if (e.target === o) closeSheet(id); };
    }

    // Taps: ayah-end medallion → tafsir; word → word sheet
    const stg = $('#stage'), rd = $('#reader');
    stg.onclick = e => {
      const endEl = e.target.closest('.ay-end');
      if (endEl?.dataset.ayah) { state.ayah = endEl.dataset.ayah; openTafsir(endEl.dataset.ayah); return; }
      const wEl = e.target.closest('.w, .sub-w');
      if (!wEl) return;
      state.word = wEl.dataset.id; state.ayah = wEl.dataset.ayah;
      for (const el of stg.querySelectorAll('.w, .sub-w')) {
        const isSel = el.dataset.id === state.word;
        el.classList.toggle('w-sel', isSel);
        el.classList.toggle('selected', isSel);
        el.classList.toggle('ays', el.dataset.ayah === state.ayah && !isSel);
      }
      openWordModal(wEl);
    };

    // Keyboard
    window.addEventListener('keydown', e => {
      if (['INPUT', 'SELECT', 'TEXTAREA'].includes(e.target.tagName)) return;
      if (e.key === 'ArrowLeft') nBtn.click();
      else if (e.key === 'ArrowRight') pBtn.click();
      else if (e.key === '+' || e.key === '=') step(0.1);
      else if (e.key === '-') step(-0.1);
      else if (e.key === '0') applyZoom(1);
      else if (e.key === 'Escape') { closeSheet('wordModal'); closeSheet('settingsModal'); closeSheet('tafsirModal'); }
    });

    // Touch: pinch to zoom (2 fingers) + swipe to flip page (only when not zoomed in)
    const dist = t => Math.hypot(t[0].clientX - t[1].clientX, t[0].clientY - t[1].clientY);
    let pinch = null, sx = 0, sy = 0, multi = false;
    rd.addEventListener('touchstart', e => {
      if (e.touches.length === 2) {
        multi = true;
        pinch = { d: dist(e.touches), z: state.zoom };
      } else if (e.touches.length === 1) {
        multi = false; sx = e.touches[0].clientX; sy = e.touches[0].clientY;
      }
    }, { passive: true });
    rd.addEventListener('touchmove', e => {
      if (e.touches.length === 2 && pinch) {
        e.preventDefault();
        const mid = (e.touches[0].clientX + e.touches[1].clientX) / 2 - rd.getBoundingClientRect().left;
        applyZoom(pinch.z * dist(e.touches) / pinch.d, mid);
      }
    }, { passive: false });
    rd.addEventListener('touchend', e => {
      if (e.touches.length === 0) {
        if (!multi && state.zoom <= 1.05) {
          const dx = e.changedTouches[0].clientX - sx, dy = e.changedTouches[0].clientY - sy;
          if (Math.abs(dx) > 70 && Math.abs(dy) < 45) (dx > 0 ? pBtn : nBtn).click();
        }
        pinch = null; setTimeout(() => { multi = false; }, 60);
      }
    }, { passive: true });

    render();
  } catch (err) {
    console.error('Boot error:', err);
    toast('অ্যাপ লোড করতে সমস্যা হয়েছে');
  }
}

window.addEventListener('DOMContentLoaded', boot);
