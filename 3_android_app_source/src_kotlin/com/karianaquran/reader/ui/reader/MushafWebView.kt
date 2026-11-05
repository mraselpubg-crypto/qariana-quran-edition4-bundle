package com.karianaquran.reader.ui.reader

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import com.karianaquran.reader.data.model.PageParser
import com.karianaquran.reader.data.model.QuranPage
import org.json.JSONObject

class AndroidMushafBridge(
    private val context: android.content.Context,
    private val onWordClicked: (wordId: String, arabic: String, translit: String, meaning: String, ayahRef: String, surahName: String) -> Unit,
    private val onWordMarked: (wordId: String, color: String) -> Unit,
    private val onBookmarkSet: (wordId: String, page: Int, isBookmarked: Boolean) -> Unit,
    private val onSwipeNext: () -> Unit,
    private val onSwipePrev: () -> Unit,
    private val onToggleImmersive: () -> Unit,
    private val onPageReady: () -> Unit = {},
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    @Volatile var currentPageJson: String = ""
    @Volatile var currentInitConfigJson: String = ""

    @JavascriptInterface
    fun getPageJson(): String = currentPageJson

    @JavascriptInterface
    fun getInitConfig(): String = currentInitConfigJson

    companion object {
        @Volatile
        private var cachedSvgTemplate: String? = null

        fun prefetchSvgTemplate(context: android.content.Context) {
            if (cachedSvgTemplate == null) {
                try {
                    cachedSvgTemplate = context.assets.open("mushaf/quran_ortho_template.svg")
                        .bufferedReader(Charsets.UTF_8).use { it.readText() }
                } catch (_: Exception) {}
            }
        }
    }

    @JavascriptInterface
    fun getSvgTemplate(): String {
        cachedSvgTemplate?.let { return it }
        return try {
            val s = context.assets.open("mushaf/quran_ortho_template.svg").bufferedReader(Charsets.UTF_8).use { it.readText() }
            cachedSvgTemplate = s
            s
        } catch (e: Exception) {
            ""
        }
    }

    @JavascriptInterface
    fun getClassicSvg(pageNum: Int): String {
        return try {
            val vault = (context.applicationContext as? com.karianaquran.reader.QarianaApp)?.container?.vault
            val vaultSvg = vault?.getString("classic_svg_$pageNum")
            if (!vaultSvg.isNullOrEmpty()) return vaultSvg
            val pStr = String.format("%03d", pageNum)
            try {
                context.assets.open("mushaf/classic_pages/p_$pStr.svg").bufferedReader(Charsets.UTF_8).use { it.readText() }
            } catch (e: Exception) {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    @JavascriptInterface
    fun onWordClicked(wordId: String, arabic: String, translit: String, meaning: String, ayahRef: String, surahName: String) {
        mainHandler.post {
            onWordClicked(wordId, arabic, translit, meaning, ayahRef, surahName)
        }
    }

    @JavascriptInterface
    fun onPlayWordAudio(surah: Int, ayah: Int, wordIndex: Int, wordId: String) {
        mainHandler.post {
            com.karianaquran.reader.service.QuranAudioPlayer.playWordPronunciation(surah, ayah, wordIndex, wordId)
        }
    }

    @JavascriptInterface
    fun onPlayAyahAudio(surah: Int, ayah: Int, surahNameBn: String = "") {
        mainHandler.post {
            com.karianaquran.reader.service.QuranAudioPlayer.playAyah(surah, ayah, surahNameBn)
        }
    }

    @JavascriptInterface
    fun onWordMarked(wordId: String, color: String) {
        mainHandler.post {
            onWordMarked(wordId, color)
        }
    }

    @JavascriptInterface
    fun onBookmarkSet(wordId: String, page: Int, isBookmarked: Boolean) {
        mainHandler.post {
            onBookmarkSet(wordId, page, isBookmarked)
        }
    }

    @JavascriptInterface
    fun onSwipeNext() {
        mainHandler.post { onSwipeNext() }
    }

    @JavascriptInterface
    fun onSwipePrev() {
        mainHandler.post { onSwipePrev() }
    }

    @JavascriptInterface
    fun onToggleImmersive() {
        mainHandler.post { onToggleImmersive() }
    }

    @JavascriptInterface
    fun onReady() {
        mainHandler.post { onPageReady() }
    }

    var onZoomChanged: ((Boolean) -> Unit)? = null

    @JavascriptInterface
    fun setZoomed(isZoomed: Boolean) {
        mainHandler.post { onZoomChanged?.invoke(isZoomed) }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MushafWebView(
    page: QuranPage,
    themeId: String,
    showMeaning: Boolean = true,
    cropMargins: Boolean = false,
    isClassic: Boolean = false,
    pageZoom: Int = 100,
    arabicScale: Float = 1f,
    banglaScale: Float = 1f,
    customBg: String? = null,
    customArabic: String? = null,
    customBangla: String? = null,
    wordMarks: Map<String, String> = emptyMap(),
    activeBookmark: String? = null,
    onWordClicked: (wordId: String, arabic: String, translit: String, meaning: String, ayahRef: String, surahName: String) -> Unit,
    onWordMarked: (wordId: String, color: String) -> Unit,
    onBookmarkSet: (wordId: String, page: Int, isBookmarked: Boolean) -> Unit,
    onSwipeNext: () -> Unit,
    onSwipePrev: () -> Unit,
    onToggleImmersive: () -> Unit,
    modifier: Modifier = Modifier,
    shimmerContent: (@Composable () -> Unit)? = null,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AndroidMushafBridge.prefetchSvgTemplate(context)
    }
    var isWebReady by remember { mutableStateOf(false) }

    val assetLoader = remember(context) {
        WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()
    }

    val bridge = remember(onWordClicked, onWordMarked, onBookmarkSet, onSwipeNext, onSwipePrev, onToggleImmersive) {
        AndroidMushafBridge(
            context = context,
            onWordClicked = onWordClicked,
            onWordMarked = onWordMarked,
            onBookmarkSet = onBookmarkSet,
            onSwipeNext = onSwipeNext,
            onSwipePrev = onSwipePrev,
            onToggleImmersive = onToggleImmersive,
            onPageReady = { isWebReady = true }
        )
    }

    // প্রতিবার পেজ ও থিম পরিবর্তনের সাথে সাথে ব্রিজ অবজেক্টে লেটেস্ট পেজ ডাটা ও কনফিগ সিঙ্ক
    val pageJsonSnapshot = remember(page) { PageParser.toJson(page) }
    bridge.currentPageJson = pageJsonSnapshot

    val initConfigSnapshot = remember(
        themeId, isClassic, page.page, showMeaning, cropMargins,
        arabicScale, banglaScale, customBg, customArabic, customBangla,
        wordMarks, activeBookmark
    ) {
        JSONObject().apply {
            put("themeId", themeId)
            put("isClassic", isClassic)
            put("pageNum", page.page)
            put("showMeaning", showMeaning)
            put("cropMargins", cropMargins)
            put("arabicScale", arabicScale.toDouble())
            put("banglaScale", banglaScale.toDouble())
            if (customBg != null) put("customBg", customBg)
            if (customArabic != null) {
                put("customAr", customArabic)
                put("customArabic", customArabic)
            }
            if (customBangla != null) {
                put("customBn", customBangla)
                put("customBangla", customBangla)
            }
            put("wordMarks", JSONObject(wordMarks))
            if (activeBookmark != null) {
                put("activeBookmark", JSONObject().apply {
                    put("wordId", activeBookmark)
                    put("page", page.page)
                })
            } else {
                put("activeBookmark", JSONObject.NULL)
            }
        }.toString()
    }
    bridge.currentInitConfigJson = initConfigSnapshot

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(Color.TRANSPARENT)
                    val isVirtualDevice = android.os.Build.FINGERPRINT.contains("generic") || android.os.Build.MODEL.contains("google_sdk") || android.os.Build.MODEL.contains("Emulator")
                    if (isVirtualDevice) {
                        setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                    } else {
                        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                    }

                    WebView.setWebContentsDebuggingEnabled(com.karianaquran.reader.BuildConfig.DEBUG)
                    android.util.Log.d("MushafWebView", "WebView factory creating instance for page ${page.page}")

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        allowFileAccessFromFileURLs = true
                        allowUniversalAccessFromFileURLs = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }

                    webChromeClient = object : android.webkit.WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                            android.util.Log.d("MushafWebView", "[JS] ${consoleMessage?.message()} [line: ${consoleMessage?.lineNumber()}]")
                            return true
                        }
                    }

                    addJavascriptInterface(bridge, "AndroidBridge")

                    webViewClient = object : WebViewClient() {
                        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: android.webkit.WebResourceError?) {
                            android.util.Log.e("MushafWebView", "onReceivedError: ${error?.description} (${error?.errorCode}) for ${request?.url}")
                        }

                        override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                            android.util.Log.e("MushafWebView", "onReceivedHttpError: status ${errorResponse?.statusCode} for ${request?.url}")
                        }

                        override fun shouldInterceptRequest(
                            view: WebView,
                            request: WebResourceRequest
                        ): WebResourceResponse? {
                            val path = request.url.path ?: ""
                            if (path.endsWith(".svg", ignoreCase = true)) {
                                val cleanPath = path.removePrefix("/assets/").removePrefix("/")
                                try {
                                    val stream = context.assets.open(cleanPath)
                                    val headers = mapOf(
                                        "Access-Control-Allow-Origin" to "*",
                                        "Content-Type" to "image/svg+xml; charset=utf-8"
                                    )
                                    return WebResourceResponse("image/svg+xml", "UTF-8", 200, "OK", headers, stream)
                                } catch (e: Exception) {
                                    android.util.Log.w("MushafWebView", "Failed to open svg asset: $cleanPath", e)
                                }
                            }
                            return assetLoader.shouldInterceptRequest(request.url)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            android.util.Log.d("MushafWebView", "onPageFinished: $url")
                            view?.let { wv ->
                                wv.evaluateJavascript(
                                    "if (window.KQApp && window.KQApp.loadFromBridge) { window.KQApp.loadFromBridge(); }",
                                    null
                                )
                                if (pageZoom != 100) {
                                    sendZoom(wv, pageZoom)
                                }
                                // সেফটি ফলব্যাক (মূল onReady আসে এসভিজি মাউন্ট হওয়ার ঠিক পরেই)
                                wv.postDelayed({ isWebReady = true }, 1400)
                            }
                        }
                    }

                    bridge.currentPageJson = PageParser.toJson(page)
                    bridge.onZoomChanged = { isZoomed ->
                        parent?.requestDisallowInterceptTouchEvent(isZoomed)
                    }
                    setOnTouchListener { v, event ->
                        if (event.pointerCount > 1) {
                            v.parent?.requestDisallowInterceptTouchEvent(true)
                        }
                        false
                    }
                    loadUrl("https://appassets.androidplatform.net/assets/mushaf/mushaf_view.html")
                    webViewInstance = this
                }
            },
            update = { wv ->
                webViewInstance = wv
            },
            modifier = Modifier.fillMaxSize(),
        )

        if (!isWebReady) {
            val bgCol = customBg?.let { hex ->
                runCatching { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(hex)) }.getOrNull()
            } ?: when (themeId) {
                "light" -> androidx.compose.ui.graphics.Color(0xFFFFFFFF)
                "cream" -> androidx.compose.ui.graphics.Color(0xFFFFFDF5)
                "sepia" -> androidx.compose.ui.graphics.Color(0xFFF4ECD8)
                "emerald" -> androidx.compose.ui.graphics.Color(0xFF04241C)
                "midnight" -> androidx.compose.ui.graphics.Color(0xFF090D24)
                else -> androidx.compose.ui.graphics.Color(0xFF0B131E)
            }
            if (shimmerContent != null) {
                shimmerContent()
            } else {
                com.karianaquran.reader.ui.components.PageShimmerPlaceholder(
                    pageNum = page.page,
                    isDark = themeId == "dark" || themeId == "midnight",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // প্রথমবার isWebReady হওয়ার সময় loadFromBridge() ইতিমধ্যে সব কনফিগ ও পেজ রেন্ডার করেছে;
    // তাই ডুপ্লিকেট ৮ বার রি-রেন্ডার এড়াতে শুধু পরবর্তী প্রপ পরিবর্তনে আপডেট পাঠানো হবে।
    var syncedSignature by remember { mutableStateOf<String?>(null) }
    var syncedThemeSig by remember { mutableStateOf<String?>(null) }
    var syncedMeaning by remember { mutableStateOf<Boolean?>(null) }
    var syncedCrop by remember { mutableStateOf<Boolean?>(null) }
    var syncedScaleSig by remember { mutableStateOf<String?>(null) }
    var syncedZoom by remember { mutableStateOf<Int?>(null) }
    var syncedMarks by remember { mutableStateOf<Map<String, String>?>(null) }
    var syncedBookmark by remember { mutableStateOf<String?>(null) }
    var initialReadyRecorded by remember { mutableStateOf(false) }

    LaunchedEffect(isWebReady) {
        if (isWebReady && !initialReadyRecorded) {
            syncedSignature = "${isClassic}_${page.page}"
            syncedThemeSig = "${themeId}_${customBg}_${customArabic}_${customBangla}"
            syncedMeaning = showMeaning
            syncedCrop = cropMargins
            syncedScaleSig = "${arabicScale}_${banglaScale}"
            syncedZoom = pageZoom
            syncedMarks = wordMarks
            syncedBookmark = activeBookmark
            initialReadyRecorded = true
        }
    }

    LaunchedEffect(isClassic, page, isWebReady) {
        if (isWebReady && initialReadyRecorded) {
            val sig = "${isClassic}_${page.page}"
            if (syncedSignature != sig) {
                syncedSignature = sig
                webViewInstance?.let { wv ->
                    if (isClassic) {
                        sendClassicMode(wv, true, page.page)
                    } else {
                        sendClassicMode(wv, false, page.page)
                        sendPageData(wv, bridge, page)
                    }
                }
            }
        }
    }

    LaunchedEffect(showMeaning, isWebReady) {
        if (isWebReady && initialReadyRecorded && syncedMeaning != showMeaning) {
            syncedMeaning = showMeaning
            webViewInstance?.let { sendBengaliMeaning(it, showMeaning) }
        }
    }

    LaunchedEffect(pageZoom, isWebReady) {
        if (isWebReady && initialReadyRecorded && syncedZoom != pageZoom) {
            syncedZoom = pageZoom
            webViewInstance?.let { sendZoom(it, pageZoom) }
        }
    }

    LaunchedEffect(cropMargins, isWebReady) {
        if (isWebReady && initialReadyRecorded && syncedCrop != cropMargins) {
            syncedCrop = cropMargins
            webViewInstance?.let { sendCropMargins(it, cropMargins) }
        }
    }

    LaunchedEffect(themeId, customBg, customArabic, customBangla, isWebReady) {
        val sig = "${themeId}_${customBg}_${customArabic}_${customBangla}"
        if (isWebReady && initialReadyRecorded && syncedThemeSig != sig) {
            syncedThemeSig = sig
            webViewInstance?.let { sendTheme(it, themeId, customBg, customArabic, customBangla) }
        }
    }

    LaunchedEffect(arabicScale, banglaScale, isWebReady) {
        val sig = "${arabicScale}_${banglaScale}"
        if (isWebReady && initialReadyRecorded && syncedScaleSig != sig) {
            syncedScaleSig = sig
            webViewInstance?.let { sendFontScale(it, arabicScale, banglaScale) }
        }
    }

    LaunchedEffect(wordMarks, isWebReady) {
        if (isWebReady && initialReadyRecorded && syncedMarks != wordMarks) {
            syncedMarks = wordMarks
            webViewInstance?.let { sendWordMarks(it, wordMarks) }
        }
    }

    LaunchedEffect(activeBookmark, isWebReady) {
        if (isWebReady && initialReadyRecorded && syncedBookmark != activeBookmark) {
            syncedBookmark = activeBookmark
            webViewInstance?.let { sendActiveBookmark(it, activeBookmark, page.page) }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewInstance?.destroy()
        }
    }
}

private fun sendPageData(wv: WebView, bridge: AndroidMushafBridge, page: QuranPage) {
    try {
        val json = PageParser.toJson(page)
        bridge.currentPageJson = json
        val b64 = Base64.encodeToString(json.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        wv.evaluateJavascript(
            "window.KQApp && window.KQApp.setPageData && window.KQApp.setPageData(decodeURIComponent(escape(atob('$b64'))));",
            null
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun sendTheme(
    wv: WebView,
    themeId: String,
    customBg: String?,
    customArabic: String?,
    customBangla: String?,
) {
    val bgArg = customBg?.let { "'$it'" } ?: "null"
    val arArg = customArabic?.let { "'$it'" } ?: "null"
    val bnArg = customBangla?.let { "'$it'" } ?: "null"
    wv.evaluateJavascript("window.KQApp && window.KQApp.setTheme('$themeId', $bgArg, $arArg, $bnArg);", null)
}

private fun sendWordMarks(wv: WebView, wordMarks: Map<String, String>) {
    try {
        val obj = JSONObject(wordMarks).toString()
        val b64 = Base64.encodeToString(obj.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        wv.evaluateJavascript("window.KQApp && window.KQApp.setWordMarks(JSON.parse(decodeURIComponent(escape(atob('$b64')))));", null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun sendActiveBookmark(wv: WebView, activeBookmark: String?, page: Int) {
    try {
        val bmJson = if (activeBookmark != null) {
            JSONObject().apply {
                put("wordId", activeBookmark)
                put("page", page)
            }.toString()
        } else "null"
        wv.evaluateJavascript("window.KQApp && window.KQApp.setActiveBookmark($bmJson);", null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun sendBengaliMeaning(wv: WebView, showMeaning: Boolean) {
    try {
        wv.evaluateJavascript("window.KQApp && window.KQApp.setBengaliMeaning && window.KQApp.setBengaliMeaning($showMeaning);", null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun sendCropMargins(wv: WebView, cropMargins: Boolean) {
    try {
        wv.evaluateJavascript("window.KQApp && window.KQApp.setCropMargins && window.KQApp.setCropMargins($cropMargins);", null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun sendClassicMode(wv: WebView, isClassic: Boolean, pageNum: Int) {
    try {
        wv.evaluateJavascript("window.KQApp && window.KQApp.setClassicMode && window.KQApp.setClassicMode($isClassic, $pageNum);", null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun sendFontScale(wv: WebView, arabicScale: Float, banglaScale: Float) {
    try {
        wv.evaluateJavascript("window.KQApp && window.KQApp.setFontScale && window.KQApp.setFontScale($arabicScale, $banglaScale);", null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun sendZoom(wv: WebView, zoomPercent: Int) {
    val z = zoomPercent.coerceIn(100, 350)
    wv.evaluateJavascript("window.KQApp && window.KQApp.setZoom && window.KQApp.setZoom($z);", null)
}
