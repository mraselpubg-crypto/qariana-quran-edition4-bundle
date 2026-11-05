/**
 * ক্বারীয়ানা কুরআন - ডিজিটাল আয়াত ও শব্দার্থ ভিউ রেন্ডারার
 */

class DigitalAyahEngine {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
    }

    async loadPage(pageNum, onReady = null, onLocked = null) {
        const loader = document.getElementById('loaderSpinner');
        if (loader) loader.style.display = 'block';

        try {
            const res = await fetch(`api/quran_data.php?action=page&page=${pageNum}`);
            if (res.status === 403) {
                const json = await res.json();
                if (loader) loader.style.display = 'none';
                if (typeof onLocked === 'function') onLocked(json);
                return;
            }

            if (!res.ok) throw new Error('ডিজিটাল পেজ লোড ব্যর্থ হয়েছে');

            const pageData = await res.json();
            this.renderPage(pageData);

            if (loader) loader.style.display = 'none';
            if (typeof onReady === 'function') onReady(pageNum);
        } catch (err) {
            console.error('[DigitalEngine] Error:', err);
            if (loader) loader.style.display = 'none';
            this.container.innerHTML = `
                <div style="text-align: center; padding: 40px 20px; color: var(--text-secondary);">
                    <p style="font-size: 15px; font-weight: bold;">এই পৃষ্ঠার ডিজিটাল আয়াত ডেটা প্রক্রিয়াকরণ চলছে।</p>
                    <p style="font-size: 12px; margin-top: 6px;">দয়া করে মুসহাফ পৃষ্ঠা ভিউতে পড়ুন।</p>
                </div>
            `;
        }
    }

    renderPage(data) {
        this.container.innerHTML = '';

        if (!data || !data.verses || data.verses.length === 0) {
            // যদি rows ফরম্যাটে থাকে
            if (data.rows && data.rows.length > 0) {
                data.rows.forEach((row) => {
                    const card = document.createElement('div');
                    card.className = 'ayah-card';
                    card.innerHTML = `
                        <div class="ayah-header">
                            <span>${row.surahName || 'কুরআনুল মাজীদ'}</span>
                            <span class="num-bn">${row.ayahRef || ''}</span>
                        </div>
                        <div class="ayah-arabic font-arabic">${row.arabic || ''}</div>
                        <div class="ayah-translit">${row.translit || ''}</div>
                        <div class="ayah-meaning">${row.meaning || ''}</div>
                    `;
                    this.container.appendChild(card);
                });
                return;
            }

            this.container.innerHTML = '<p style="text-align:center; padding: 20px;">কোনো আয়াত পাওয়া যায়নি।</p>';
            return;
        }

        data.verses.forEach((v) => {
            const card = document.createElement('div');
            card.className = 'ayah-card';
            card.innerHTML = `
                <div class="ayah-header">
                    <span>${data.surahNameBn || 'কুরআনুল মাজীদ'} • আয়াত <span class="num-bn">${this.toBnNum(v.ayah)}</span></span>
                    <span class="num-bn">${v.ayahRef || ''}</span>
                </div>
                <div class="ayah-arabic font-arabic">${v.arabic || ''}</div>
                <div class="ayah-translit">উচ্চারণ: ${v.transliteration || ''}</div>
                <div class="ayah-meaning">${v.translation || ''}</div>
            `;
            this.container.appendChild(card);
        });

        this.container.scrollTop = 0;
    }

    toBnNum(num) {
        const bnDigits = ['০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'];
        return String(num).replace(/[0-9]/g, (d) => bnDigits[d]);
    }
}
