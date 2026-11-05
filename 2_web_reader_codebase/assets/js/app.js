/**
 * ক্বারীয়ানা কুরআন - গ্লোবাল ওয়েব ও ইন-অ্যাপ জাভাস্ক্রিপ্ট কন্ট্রোলার ব্রিজ
 */

document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const initialPage = parseInt(urlParams.get('page')) || 1;
    const initialTheme = urlParams.get('theme') || 'light';
    const initialMode = urlParams.get('mode') || 'mushaf';
    const isEmbedMode = urlParams.get('embed') === '1' || (navigator.userAgent && navigator.userAgent.includes('QarianaReader'));

    const bnDigits = ['০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'];
    function toBn(num) {
        return String(num).replace(/[0-9]/g, d => bnDigits[d]);
    }

    const state = {
        currentPage: initialPage,
        totalPages: 610,
        freeLimit: 10,
        mode: initialMode,
        theme: initialTheme,
        isEmbed: isEmbedMode
    };

    // ইঞ্জিন ইনস্ট্যান্স
    const canvasEngine = new MushafCanvasEngine('mushafCanvas', 'canvasContainer');
    const digitalEngine = new DigitalAyahEngine('digitalScrollView');

    // ইন-অ্যাপ মোড হলে ওয়েব বারগুলো স্বয়ংক্রিয়ভাবে লুকিয়ে ফেলা
    if (state.isEmbed) {
        document.body.classList.add('embed-mode');
        const tb = document.getElementById('topBar');
        const bb = document.getElementById('bottomBar');
        if (tb) tb.style.display = 'none';
        if (bb) bb.style.display = 'none';
    }

    // এলিমেন্ট রেফারেন্স
    const pageBadge = document.getElementById('pageBadge');
    const pageSlider = document.getElementById('pageSlider');
    const navCenterPill = document.getElementById('navCenterPill');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const modeSwitchBtn = document.getElementById('modeSwitchBtn');
    const themeBtn = document.getElementById('themeBtn');
    const indexBtn = document.getElementById('indexBtn');
    const backBtn = document.getElementById('backBtn');
    const subModal = document.getElementById('subModal');
    const themeModal = document.getElementById('themeModal');
    const indexModal = document.getElementById('indexModal');

    function renderCurrentPage() {
        if (pageBadge) pageBadge.innerText = `পৃষ্ঠা ${toBn(state.currentPage)}`;
        if (pageSlider) pageSlider.value = state.currentPage;
        
        const pct = Math.round((state.currentPage / state.totalPages) * 100);
        if (navCenterPill) navCenterPill.innerText = `পৃষ্ঠা ${toBn(state.currentPage)} / ${toBn(state.totalPages)} (${toBn(pct)}%) 🔍`;

        if (prevBtn) prevBtn.disabled = state.currentPage <= 1;

        if (state.mode === 'mushaf') {
            document.getElementById('canvasContainer').style.display = 'flex';
            document.getElementById('digitalScrollView').style.display = 'none';
            canvasEngine.loadPage(
                state.currentPage,
                state.theme,
                (p) => notifyNativeBridge('onPageReady', p),
                (lockedData) => showSubscriptionSheet(lockedData)
            );
        } else {
            document.getElementById('canvasContainer').style.display = 'none';
            document.getElementById('digitalScrollView').style.display = 'block';
            digitalEngine.loadPage(
                state.currentPage,
                (p) => notifyNativeBridge('onPageReady', p),
                (lockedData) => showSubscriptionSheet(lockedData)
            );
        }
    }

    function showSubscriptionSheet(data) {
        if (window.AndroidBridge && typeof window.AndroidBridge.onSubscriptionRequired === 'function') {
            window.AndroidBridge.onSubscriptionRequired(state.currentPage);
            return;
        }
        if (subModal) subModal.style.display = 'flex';
    }

    function notifyNativeBridge(event, data) {
        if (window.AndroidBridge && typeof window.AndroidBridge[event] === 'function') {
            window.AndroidBridge[event](data);
        }
    }

    function applyTheme(themeId) {
        state.theme = themeId;
        document.documentElement.setAttribute('data-theme', themeId);
        document.getElementById('appFrame').setAttribute('data-theme', themeId);
        canvasEngine.applyThemeFilter(themeId);

        document.querySelectorAll('.theme-chip').forEach(chip => {
            chip.classList.toggle('active', chip.getAttribute('data-theme') === themeId);
        });
    }

    // ==========================================
    // 🌐 গ্লোবাল জাভাস্ক্রিপ্ট API (ইন-অ্যাপ ও নেটিভ কন্ট্রোলের জন্য)
    // ==========================================
    window.KQWebReader = {
        setPage: function (pageNum) {
            const p = parseInt(pageNum);
            if (p >= 1 && p <= state.totalPages) {
                state.currentPage = p;
                renderCurrentPage();
            }
        },
        setTheme: function (themeId) {
            applyTheme(themeId);
        },
        setMode: function (mode) {
            if (mode === 'mushaf' || mode === 'digital') {
                state.mode = mode;
                if (modeSwitchBtn) {
                    modeSwitchBtn.innerText = state.mode === 'mushaf' ? '📖 মুসহাফ' : '🔤 ডিজিটাল';
                }
                renderCurrentPage();
            }
        },
        setZoom: function (zoomPercent) {
            const z = parseFloat(zoomPercent) / 100.0;
            canvasEngine.updateTransform(z);
        },
        getPage: function () {
            return state.currentPage;
        },
        nextPage: function () {
            if (state.currentPage < state.totalPages) {
                state.currentPage++;
                renderCurrentPage();
            }
        },
        prevPage: function () {
            if (state.currentPage > 1) {
                state.currentPage--;
                renderCurrentPage();
            }
        }
    };

    // স্ট্যান্ডঅ্যালোন ওয়েব ইন্টারঅ্যাকশন
    if (modeSwitchBtn) {
        modeSwitchBtn.addEventListener('click', () => {
            const newMode = state.mode === 'mushaf' ? 'digital' : 'mushaf';
            window.KQWebReader.setMode(newMode);
        });
    }

    if (prevBtn) prevBtn.addEventListener('click', () => window.KQWebReader.prevPage());
    if (nextBtn) nextBtn.addEventListener('click', () => window.KQWebReader.nextPage());
    if (pageSlider) pageSlider.addEventListener('input', (e) => window.KQWebReader.setPage(e.target.value));

    if (themeBtn) {
        themeBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            if (themeModal) themeModal.style.display = 'flex';
        });
    }

    document.querySelectorAll('.theme-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            const t = chip.getAttribute('data-theme');
            applyTheme(t);
            setTimeout(() => { if (themeModal) themeModal.style.display = 'none'; }, 200);
        });
    });

    if (indexBtn) {
        indexBtn.addEventListener('click', async (e) => {
            e.stopPropagation();
            if (indexModal) indexModal.style.display = 'flex';
            loadSurahIndex();
        });
    }

    async function loadSurahIndex() {
        const list = document.getElementById('indexList');
        if (!list || list.children.length > 1) return;

        try {
            const res = await fetch('api/quran_data.php?action=surahs');
            const json = await res.json();
            if (json.success && json.data.surahs) {
                list.innerHTML = '';
                json.data.surahs.forEach(s => {
                    const item = document.createElement('div');
                    item.style.cssText = 'display:flex; justify-content:space-between; align-items:center; padding:12px 10px; border-bottom:1px solid var(--border-color); cursor:pointer;';
                    item.innerHTML = `
                        <div>
                            <span style="font-weight:bold; font-size:14px;">${s.num}. ${s.nameBn}</span>
                            <span style="font-size:11px; color:var(--text-secondary); margin-left:6px;">(${s.place})</span>
                        </div>
                        <div style="text-align:right;">
                            <span style="font-size:16px;" class="font-arabic">${s.nameAr}</span>
                            <div style="font-size:11px; color:var(--primary-brand); font-weight:bold;">পৃষ্ঠা ${toBn(s.startPage)}</div>
                        </div>
                    `;
                    item.addEventListener('click', () => {
                        window.KQWebReader.setPage(s.startPage);
                        if (indexModal) indexModal.style.display = 'none';
                    });
                    list.appendChild(item);
                });
            }
        } catch (e) {
            if (list) list.innerHTML = '<p style="text-align:center; padding:20px;">সূচিপত্র লোড করা যায়নি</p>';
        }
    }

    if (backBtn) {
        backBtn.addEventListener('click', () => {
            if (window.history.length > 1) window.history.back();
            else alert('ক্বারীয়ানা অর্থ-কুরআন হোম স্ক্রিনে ফিরে যাচ্ছেন');
        });
    }

    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) overlay.style.display = 'none';
        });
    });

    // ইনিশিয়াল লোড
    applyTheme(state.theme);
    renderCurrentPage();
});
