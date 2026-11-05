/**
 * ক্বারীয়ানা কুরআন - ইন-মেমোরি ডিক্রিপশন ও ক্যানভাস রেন্ডারিং ইঞ্জিন
 * (ন্যাটিভ ইন-অ্যাপ ও স্ট্যান্ডঅ্যালোন ডুয়াল সাপোর্টেড)
 */

class MushafCanvasEngine {
    constructor(canvasId, containerId) {
        this.canvas = document.getElementById(canvasId);
        this.container = document.getElementById(containerId);
        this.ctx = this.canvas.getContext('2d');
        this.xorKey = new TextEncoder().encode('QARIANA_SECRET_KEY_9981');
        
        this.currentPage = null;
        this.pdfDoc = null;
        this.zoomLevel = 1.0;
        this.panX = 0;
        this.panY = 0;
        this.isPanning = false;
        this.startX = 0;
        this.startY = 0;

        if (window.pdfjsLib) {
            window.pdfjsLib.GlobalWorkerOptions.workerSrc = 'assets/js/vendor/pdf.worker.js';
        }

        this.initPinchAndPan();
    }

    decryptXor(encryptedBuffer) {
        const u8 = new Uint8Array(encryptedBuffer);
        const len = u8.length;
        const keyLen = this.xorKey.length;
        const decrypted = new Uint8Array(len);

        for (let i = 0; i < len; i++) {
            decrypted[i] = u8[i] ^ this.xorKey[i % keyLen];
        }
        return decrypted;
    }

    async loadPage(pageNum, themeId = 'light', onReady = null, onLocked = null) {
        this.currentPage = pageNum;
        const loader = document.getElementById('loaderSpinner');
        if (loader) loader.style.display = 'block';

        const existingErr = document.getElementById('renderErrorMsg');
        if (existingErr) existingErr.remove();

        try {
            const res = await fetch(`api/stream_service.php?page=${pageNum}`);
            if (res.status === 403) {
                const json = await res.json();
                if (loader) loader.style.display = 'none';
                if (typeof onLocked === 'function') onLocked(json);
                return;
            }

            if (!res.ok) throw new Error('সার্ভার থেকে পৃষ্ঠা পাওয়া যায়নি (HTTP ' + res.status + ')');

            const encryptedBuffer = await res.arrayBuffer();
            const decryptedBytes = this.decryptXor(encryptedBuffer);

            if (!window.pdfjsLib) {
                throw new Error('PDF রেন্ডারিং ইঞ্জিন সক্রিয় নেই');
            }

            window.pdfjsLib.GlobalWorkerOptions.workerSrc = 'assets/js/vendor/pdf.worker.js';
            const loadingTask = window.pdfjsLib.getDocument({ data: decryptedBytes });
            this.pdfDoc = await loadingTask.promise;

            const page = await this.pdfDoc.getPage(1);
            await this.renderPageToCanvas(page, themeId);

            if (loader) loader.style.display = 'none';
            if (typeof onReady === 'function') onReady(pageNum);
        } catch (err) {
            console.error('[CanvasEngine] Error:', err);
            if (loader) loader.style.display = 'none';

            const errDiv = document.createElement('div');
            errDiv.id = 'renderErrorMsg';
            errDiv.style.cssText = 'position:absolute; top:45%; left:50%; transform:translate(-50%, -50%); text-align:center; padding:18px 20px; background:var(--bg-surface); border:1px solid var(--border-color); border-radius:14px; width:85%; max-width:320px; box-shadow:0 6px 16px rgba(0,0,0,0.1); z-index:30;';
            errDiv.innerHTML = `
                <div style="font-size:22px; margin-bottom:6px;">⚠️</div>
                <div style="font-weight:bold; font-size:13.5px; color:var(--text-primary); margin-bottom:4px;">পৃষ্ঠাটি লোড হতে সমস্যা হয়েছে</div>
                <div style="font-size:11.5px; color:var(--text-secondary); margin-bottom:10px;">${err.message || 'পুনরায় চেষ্টা করুন'}</div>
                <button onclick="location.reload()" style="background:var(--primary-brand); color:#fff; border:none; padding:7px 15px; border-radius:8px; font-size:12px; font-weight:bold; cursor:pointer;">পুনরায় লোড</button>
            `;
            this.container.appendChild(errDiv);
        }
    }

    async renderPageToCanvas(page, themeId) {
        const dpr = Math.min(window.devicePixelRatio || 2.0, 3.0);
        const containerW = this.container.clientWidth || window.innerWidth;
        const containerH = this.container.clientHeight || window.innerHeight;
        
        const unscaledViewport = page.getViewport({ scale: 1.0 });

        // স্ক্রিন অনুযায়ী পারফেক্ট অটো-ফিট
        const scaleW = (containerW - 8) / unscaledViewport.width;
        const scaleH = (containerH - 8) / unscaledViewport.height;
        const fitScale = Math.min(scaleW, scaleH) * dpr;

        const viewport = page.getViewport({ scale: fitScale });

        this.canvas.width = Math.floor(viewport.width);
        this.canvas.height = Math.floor(viewport.height);
        
        const cssW = Math.round(viewport.width / dpr);
        const cssH = Math.round(viewport.height / dpr);
        this.canvas.style.width = `${cssW}px`;
        this.canvas.style.height = `${cssH}px`;
        this.canvas.style.display = 'block';

        const ctx = this.canvas.getContext('2d');
        ctx.fillStyle = '#FFFFFF';
        ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);

        const renderContext = {
            canvasContext: ctx,
            viewport: viewport
        };

        await page.render(renderContext).promise;

        if (window.KQ_DRM && typeof window.KQ_DRM.applyWatermark === 'function') {
            window.KQ_DRM.applyWatermark(this.canvas, this.currentPage);
        }

        this.applyThemeFilter(themeId);
    }

    applyThemeFilter(themeId) {
        switch (themeId) {
            case 'sepia':
                this.canvas.style.filter = 'sepia(0.65) contrast(1.15) brightness(0.96)';
                break;
            case 'cream':
                this.canvas.style.filter = 'sepia(0.20) contrast(1.08) brightness(1.01)';
                break;
            case 'emerald':
                this.canvas.style.filter = 'invert(0.93) hue-rotate(110deg) contrast(1.3) brightness(0.95)';
                break;
            case 'dark':
                this.canvas.style.filter = 'invert(0.92) hue-rotate(180deg) contrast(1.25) brightness(0.98)';
                break;
            case 'midnight':
                this.canvas.style.filter = 'invert(0.95) hue-rotate(200deg) contrast(1.3) brightness(0.9)';
                break;
            default: // light
                this.canvas.style.filter = 'contrast(1.05) brightness(1.0)';
                break;
        }
    }

    initPinchAndPan() {
        const self = this;
        let initialDistance = null;
        let currentScale = 1.0;

        this.container.addEventListener('touchstart', (e) => {
            if (e.touches.length === 2) {
                initialDistance = Math.hypot(
                    e.touches[0].pageX - e.touches[1].pageX,
                    e.touches[0].pageY - e.touches[1].pageY
                );
            } else if (e.touches.length === 1 && currentScale > 1.0) {
                self.isPanning = true;
                self.startX = e.touches[0].pageX - self.panX;
                self.startY = e.touches[0].pageY - self.panY;
            }
        }, { passive: true });

        this.container.addEventListener('touchmove', (e) => {
            if (e.touches.length === 2 && initialDistance) {
                const newDist = Math.hypot(
                    e.touches[0].pageX - e.touches[1].pageX,
                    e.touches[0].pageY - e.touches[1].pageY
                );
                const factor = newDist / initialDistance;
                currentScale = Math.min(Math.max(1.0, currentScale * factor), 3.0);
                self.updateTransform(currentScale);
                initialDistance = newDist;
            } else if (e.touches.length === 1 && self.isPanning) {
                self.panX = e.touches[0].pageX - self.startX;
                self.panY = e.touches[0].pageY - self.startY;
                self.updateTransform(currentScale);
            }
        }, { passive: true });

        this.container.addEventListener('touchend', (e) => {
            if (e.touches.length < 2) initialDistance = null;
            if (e.touches.length === 0) self.isPanning = false;
        }, { passive: true });

        let lastTap = 0;
        this.container.addEventListener('touchend', (e) => {
            const currentTime = new Date().getTime();
            const tapLength = currentTime - lastTap;
            if (tapLength < 300 && tapLength > 0) {
                currentScale = (currentScale > 1.2) ? 1.0 : 1.8;
                self.panX = 0;
                self.panY = 0;
                self.updateTransform(currentScale);
            }
            lastTap = currentTime;
        });
    }

    updateTransform(scale) {
        this.zoomLevel = scale;
        if (scale <= 1.0) {
            this.panX = 0;
            this.panY = 0;
        }
        this.canvas.style.transform = `translate(${this.panX}px, ${this.panY}px) scale(${scale})`;
    }
}
