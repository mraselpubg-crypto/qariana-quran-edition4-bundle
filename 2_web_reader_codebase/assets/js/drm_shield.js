/**
 * ক্বারীয়ানা কুরআন - সুরক্ষিত ডিআরএম ও অ্যান্টি-এক্সট্রাকশন গার্ড
 */

(function () {
    'use strict';

    // ১. কনটেক্সট মেনু ও রাইট ক্লিক ব্লক
    document.addEventListener('contextmenu', function (e) {
        e.preventDefault();
        return false;
    }, { capture: true });

    // ২. ড্র্যাগ অ্যান্ড ড্রপ ও টেক্সট সিলেকশন ব্লক
    document.addEventListener('dragstart', function (e) {
        e.preventDefault();
        return false;
    });

    document.addEventListener('selectstart', function (e) {
        e.preventDefault();
        return false;
    });

    // ৩. সেভ, প্রিন্ট ও ডেভটুলস কীবোর্ড শর্টকাট ব্লক
    window.addEventListener('keydown', function (e) {
        // F12
        if (e.key === 'F12' || e.keyCode === 123) {
            e.preventDefault();
            e.stopPropagation();
            return false;
        }

        // Ctrl + S (Save), Ctrl + P (Print), Ctrl + U (Source)
        if (e.ctrlKey && (e.key === 's' || e.key === 'S' || e.key === 'p' || e.key === 'P' || e.key === 'u' || e.key === 'U')) {
            e.preventDefault();
            e.stopPropagation();
            return false;
        }

        // Ctrl + Shift + I / J / C (Inspect)
        if (e.ctrlKey && e.shiftKey && (e.key === 'i' || e.key === 'I' || e.key === 'j' || e.key === 'J' || e.key === 'c' || e.key === 'C')) {
            e.preventDefault();
            e.stopPropagation();
            return false;
        }
    }, { capture: true });

    // ৪. ক্যানভাস ইমেজ কপি ও হাইজ্যাক প্রতিরোধ
    window.addEventListener('load', function () {
        const mc = document.getElementById('mushafCanvas');
        if (mc) {
            mc.addEventListener('contextmenu', function (e) { e.preventDefault(); return false; });
            mc.style.pointerEvents = 'auto';
        }
    });

    // ৫. ডিভাইস ওয়াটারমার্ক ইনিশিয়ালাইজেশন
    window.KQ_DRM = {
        applyWatermark: function (canvas, pageNum) {
            if (!canvas) return;
            const ctx = canvas.getContext('2d');
            if (!ctx) return;

            const w = canvas.width;
            const h = canvas.height;

            ctx.save();
            ctx.font = 'bold ' + Math.max(14, Math.floor(w * 0.022)) + 'px sans-serif';
            ctx.fillStyle = 'rgba(125, 125, 125, 0.12)';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';

            const text = 'ক্বারীয়ানা কুরআন • পৃষ্ঠা ' + pageNum + ' • অফিসিয়াল প্রিভিউ';

            ctx.translate(w / 2, h / 2);
            ctx.rotate(-25 * Math.PI / 180);
            ctx.fillText(text, 0, 0);
            ctx.restore();
        }
    };
})();
