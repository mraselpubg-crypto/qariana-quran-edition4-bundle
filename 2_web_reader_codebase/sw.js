// Offline shell. Bump V whenever SHELL changes. addAll() fails as a whole if ONE file is missing -> keep this list exact.
const V = 'qr-shell-v5';
const SHELL = ['./', 'index.html', 'manifest.webmanifest', 'css/app.css', 'js/app.js', 'assets/icon.svg',
  'config/layouts.json', 'config/tafsir.json', 'config/tajweed.json', 'config/design-tokens.json', 'config/entitlements.json', 'agent/tools.json',
  'assets/bg/page-bg-arabic.svg', 'assets/bg/page-bg-translation.svg', 'assets/bg/page-bg-arabic-14.svg', 'assets/bg/page-bg-translation-14.svg',
  'assets/bg/page-bg-standard-15-ar.svg', 'assets/bg/page-bg-standard-15-sub.svg', 'assets/bg/page-bg-standard-14-ar.svg', 'assets/bg/page-bg-standard-14-sub.svg',
  'assets/fonts/font.woff2', 'assets/fonts/digitalkhatt_indopak.woff2', 'assets/fonts/kalpurush.ttf',
  'data/surahs.json', 'data/juzs.json', 'data/surah-pages.json', 'data/words/index.json', 'data/bn-wbw-full.json', 'data/bn-translation.json'];
self.addEventListener('install', e => e.waitUntil(caches.open(V).then(c => c.addAll(SHELL)).then(() => self.skipWaiting())));
self.addEventListener('activate', e => e.waitUntil(caches.keys().then(ks => Promise.all(ks.filter(k => k !== V).map(k => caches.delete(k)))).then(() => self.clients.claim())));
self.addEventListener('fetch', e => {
  if (e.request.method !== 'GET' || new URL(e.request.url).origin !== location.origin) return;
  e.respondWith(fetch(e.request).then(r => { if (r.ok) { const c = r.clone(); caches.open(V).then(x => x.put(e.request, c)); } return r; })
    .catch(() => caches.match(e.request)));
});
