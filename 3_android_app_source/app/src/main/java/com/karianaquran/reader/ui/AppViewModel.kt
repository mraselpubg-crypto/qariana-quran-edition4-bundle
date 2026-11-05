package com.karianaquran.reader.ui



import android.app.Application



import androidx.compose.runtime.getValue



import androidx.compose.runtime.mutableStateListOf



import androidx.compose.runtime.mutableStateOf



import androidx.compose.runtime.setValue



import androidx.lifecycle.AndroidViewModel



import androidx.lifecycle.viewModelScope



import com.karianaquran.reader.BuildConfig



import com.karianaquran.reader.QarianaApp



import com.karianaquran.reader.data.ReaderSettings



import com.karianaquran.reader.data.ReadingMode
import com.karianaquran.reader.ui.components.toBn



import com.karianaquran.reader.data.UserData



import com.karianaquran.reader.data.PageResult



import com.karianaquran.reader.data.model.Publications



import com.karianaquran.reader.data.model.Session



import com.karianaquran.reader.data.model.WordMarkType



import com.karianaquran.reader.data.remote.LoginResult



import com.karianaquran.reader.data.remote.PaymentOrder



import com.karianaquran.reader.data.remote.PaymentStatus



import com.karianaquran.reader.data.remote.ValidationResult



import com.karianaquran.reader.security.SecurityGuard



import com.karianaquran.reader.ui.components.QuranMetadata



import com.karianaquran.reader.ui.theme.Palettes



import kotlinx.coroutines.Dispatchers



import kotlinx.coroutines.Job



import kotlinx.coroutines.delay



import kotlinx.coroutines.flow.SharingStarted



import kotlinx.coroutines.flow.StateFlow



import kotlinx.coroutines.flow.stateIn



import kotlinx.coroutines.launch

import kotlinx.coroutines.withTimeoutOrNull



import kotlinx.coroutines.tasks.await



sealed interface Screen {



    data object Home : Screen



    data object Library : Screen



    data class SurahIndex(val initialTab: Int = 0) : Screen

    /** ৩টি সংস্করণের ফুল-স্ক্রিন পরিচিতি (বটমশিটের পরিবর্তে)। */
    data class EditionDetail(val initialMode: ReadingMode = ReadingMode.CLASSIC) : Screen

    /** মেম্বারশিপ প্যাকেজসমূহের ফুল-স্ক্রিন (বটমশিটের পরিবর্তে)। */
    data object MembershipPackages : Screen



    data object Profile : Screen



    data object Notifications : Screen



    data class QuranOverview(val resumePage: Int = 1) : Screen



    data class Reader(val startPage: Int) : Screen



    data object Login : Screen



    data object Pay : Screen



    data object Namaz : Screen



    data object Tasbih : Screen



    data object Dua : Screen



    data object Hadith : Screen



    data object Qibla : Screen



    data object Names : Screen



    data object Pillars : Screen



    data object Ramadan : Screen



    data object Settings : Screen



    data object MoreFeatures : Screen



    data object NamazTracker : Screen

    data object EditProfile : Screen

    data class InAppBrowser(val url: String, val title: String = "ক্বারীয়ানা") : Screen

}



data class LoginUi(val loading: Boolean = false, val error: String? = null)

/** কোন কনটেক্সট থেকে পেমেন্ট শুরু হচ্ছে। */
enum class PayOrigin { READER_UNLOCK, LIBRARY_BOOK, PACKAGE_PURCHASE }



sealed interface PayUi {



    data object Idle : PayUi



    data class Creating(val packageId: String) : PayUi



    data class Checkout(
        val order: PaymentOrder,
        val packageId: String,
        val packageNameBn: String
    ) : PayUi



    data class Error(val message: String, val packageId: String? = null) : PayUi

    /** পেমেন্ট সফল — কনফার্মেশন দেখানোর সুযোগ। */
    data class Success(val packageId: String, val licenseKey: String) : PayUi

}



class AppViewModel(app: Application) : AndroidViewModel(app) {



    val c = (app as QarianaApp).container



    val fonts get() = c.fonts



    private val fontCache = mutableMapOf<String, androidx.compose.ui.text.font.FontFamily>()



    fun getDynamicFonts(



        banglaFont: String,



        fontBold: Boolean,



        englishFont: String = "default",



        appLanguage: String = "bn"



    ): com.karianaquran.reader.ui.theme.AppFonts {



        val arabicFont = c.fonts.arabic



        val normalized = banglaFont.lowercase().replace("_", "").replace("-", "").trim()



        val banglaFontFamily = fontCache.getOrPut("${normalized}_$fontBold") {



            val assetName = when (normalized) {



                "hindsiliguri" -> if (fontBold) "mushaf/fonts/HindSiliguri-Bold.ttf" else "mushaf/fonts/HindSiliguri-Regular.ttf"



                "firasans" -> if (fontBold) "mushaf/fonts/FiraSans-Bold.ttf" else "mushaf/fonts/FiraSans-Regular.ttf"



                else -> if (fontBold) "mushaf/fonts/Kalpurush-Bold.ttf" else "mushaf/fonts/kalpurush.ttf"



            }



            runCatching {



                val tf = android.graphics.Typeface.createFromAsset(getApplication<Application>().assets, assetName)



                androidx.compose.ui.text.font.FontFamily(androidx.compose.ui.text.font.Typeface(tf))



            }.getOrDefault(c.fonts.bangla)



        }



        val englishFontFamily = fontCache.getOrPut("en_${englishFont}_$fontBold") {



            runCatching {



                val baseTf = when (englishFont.lowercase()) {



                    "serif" -> android.graphics.Typeface.SERIF



                    "monospace" -> android.graphics.Typeface.MONOSPACE



                    else -> android.graphics.Typeface.SANS_SERIF



                }



                val weight = if (fontBold) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL



                val tf = android.graphics.Typeface.create(baseTf, weight)



                androidx.compose.ui.text.font.FontFamily(androidx.compose.ui.text.font.Typeface(tf))



            }.getOrDefault(androidx.compose.ui.text.font.FontFamily.Default)



        }



        return com.karianaquran.reader.ui.theme.AppFonts(



            arabic = arabicFont,



            bangla = banglaFontFamily,



            english = englishFontFamily



        )



    }



    val settings: StateFlow<ReaderSettings> =



        c.settings.settings.stateIn(viewModelScope, SharingStarted.Eagerly, ReaderSettings())



    val userData: StateFlow<UserData> =



        c.settings.userData.stateIn(viewModelScope, SharingStarted.Eagerly, UserData())



    // ---- সিকিউরিটি ----



    val security: SecurityGuard.Result = SecurityGuard.evaluate(app)



    val blocked: Boolean = SecurityGuard.shouldBlock(security)



    var captureWarning by mutableStateOf(false)



        private set



    // ---- অফলাইন ডাউনলোড ----



    val offlineDownloadState: StateFlow<com.karianaquran.reader.data.remote.OfflineDownloadState> = c.offline.state



    fun startOfflineDownload(mode: ReadingMode = settings.value.readingMode) {
        val s = session ?: return
        c.offline.startDownloadForMode(
            session = s,
            isUnlocked = isUnlocked,
            mode = mode
        )
    }

    fun getClassicDownloadedCount(): Int = c.offline.getClassicDownloadedCount()

    fun isClassicFullyDownloaded(): Boolean = c.offline.isClassicFullyDownloaded()

    fun deleteClassicPages(preservePreview: Boolean = true) {
        c.offline.deleteClassicPages(preservePreview)
    }



    fun cancelOfflineDownload() {



        c.offline.cancelDownload()



    }



    fun isOfflineReady(): Boolean = c.offline.isFullyDownloaded()

    /** অফলাইন ডেটা মুছে ফেলা (ফ্রি প্রিভিউ ১–১০ রেখে বাকি সব)। */
    fun deleteOfflinePages(preservePreview: Boolean = true) {
        c.offline.deleteOfflinePages(preservePreview)
    }



    // ---- সেশন ও নেভিগেশন ----



    var session by mutableStateOf<Session?>(c.sessions.load())



        private set



    val stack = mutableStateListOf<Screen>(Screen.Home)



    var notice by mutableStateOf<String?>(null)



    var loginUi by mutableStateOf(LoginUi())



        private set



    var payUi by mutableStateOf<PayUi>(PayUi.Idle)



        private set

    /**
     * লগইন-ফার্স্ট পারচেজ ক্যারিয়ার।
     * ফ্রি ইউজার প্যাকেজ বেছে নিয়ে লগইন না থাকলে এখানে ধরে রাখা হয়;
     * লগইন/OTP সফল হওয়ার সাথে সাথে এই ID-তে পেমেন্ট রিজিউম হয়।
     */
    var pendingPackageId by mutableStateOf<String?>(null)

        private set

    /** পেমেন্ট কোন কনটেক্সট থেকে শুরু (রিডার আনলক / প্যাকেজ ক্রয়)। */
    var payOrigin by mutableStateOf(PayOrigin.PACKAGE_PURCHASE)

        private set

    /** ইউজার বর্তমানে লগইন করা কিনা। */
    val isLoggedIn: Boolean get() = session != null



    val current: Screen get() = stack.last()



    val VIP_PHONES = setOf(

        "01906655779", "1906655779", "8801906655779",

        "01717056816", "1717056816", "8801717056816",

        "01305898919", "1305898919", "8801305898919"

    )



    fun isVipPhone(mobile: String?): Boolean {

        val norm = mobile?.replace("+88", "")?.replace(" ", "")?.replace("-", "") ?: return false

        return norm in VIP_PHONES || VIP_PHONES.any { norm.endsWith(it) } || norm.contains("1906655779") || norm.contains("1717056816") || norm.contains("1305898919")

    }



    val isUnlocked: Boolean get() {

        val s = session ?: return false

        val norm = s.mobile.replace("+88", "").replace(" ", "").replace("-", "")

        if (isVipPhone(norm)) return true

        val key = s.licenseKey

        if (key.startsWith("KQ-USER-") || key.startsWith("KQ-FREE-") || key.isBlank()) {

            return false

        }

        return true

    }



    /**

     * প্রকাশনাভিত্তিক পৃথক আনলক স্ট্যাটাস:

     * মূল কুরআনের জন্য সাবস্ক্রিপশন (Basic, Standard, VIP) প্রযোজ্য।

     * লাইব্রেরির অন্যান্য স্বতন্ত্র বইয়ের জন্য নিজস্ব পারচেজ/আনলক আইডি যাচাই করা হয়।

     */

    fun isBookUnlocked(bookId: String): Boolean {

        if (bookId == com.karianaquran.reader.data.model.Publications.ORTHO_QURAN_ID || bookId == "ortho_sara") {

            return isUnlocked

        }

        return userData.value.unlockedBooks.contains(bookId)

    }



    fun unlockBook(bookId: String) {

        viewModelScope.launch {

            c.settings.unlockBook(bookId)

        }

    }



    private var syncJob: Job? = null



    private var pushJob: Job? = null



    private var payJob: Job? = null



    var updateState by mutableStateOf<com.karianaquran.reader.data.remote.UpdateCheckResult?>(null)



        private set



    var isCheckingUpdate by mutableStateOf(false)



        private set



    init {



        if (security.violation) viewModelScope.launch {



            c.licenses.logViolation("device_" + security.reasons().joinToString("+"), session?.licenseKey, session?.mobile)



        }



        // ব্যাকগ্রাউন্ডে নতুন ভার্সন চেক



        checkAppUpdate(manual = false)
        viewModelScope.launch { c.settings.applyV133DefaultsOnce() }



        session?.let { s ->



            startSync(s)



            viewModelScope.launch {



                if (c.licenses.validate(s) == ValidationResult.Revoked) {



                    logout()



                    notice = "এই লাইসেন্সটি এই ডিভাইসে আর সক্রিয় নেই। অনুগ্রহ করে পুনরায় লগইন করুন বা সাপোর্টে যোগাযোগ করুন।"



                }



            }



        }



    }



    fun checkAppUpdate(manual: Boolean = false) {



        if (isCheckingUpdate) return



        isCheckingUpdate = true



        viewModelScope.launch {



            try {



                val res = c.updates.checkForUpdate()



                updateState = res



                if (manual && res is com.karianaquran.reader.data.remote.UpdateCheckResult.UpToDate) {



                    notice = "আপনার ক্বারীয়ানা কুরআন অ্যাপটি সম্পূর্ণ আপ-টু-ডেট রয়েছে (v${BuildConfig.VERSION_NAME})"



                }



            } catch (e: Exception) {



                if (manual) notice = "আপডেট চেক করা যায়নি। ইন্টারনেট সংযোগ নিশ্চিত করুন।"



            } finally {



                isCheckingUpdate = false



            }



        }



    }



    fun dismissUpdateDialog() {



        updateState = null



    }



    fun navigate(s: Screen) { stack.add(s) }



    fun openInAppBrowser(url: String, title: String = "ক্বারীয়ানা") {

        navigate(Screen.InAppBrowser(url = url, title = title))

    }



    fun openLibrary() { navigate(Screen.Library) }



    fun openIndex(tab: Int = 1) { navigate(Screen.SurahIndex(initialTab = tab)) }



    fun openProfile() { navigate(Screen.Profile) }



    fun openNotifications() { navigate(Screen.Notifications) }



    fun openNamaz() { navigate(Screen.Namaz) }



    fun openTasbih() { navigate(Screen.Tasbih) }



    fun openDua() { navigate(Screen.Dua) }



    fun openHadith() { navigate(Screen.Hadith) }



    fun openQuranOverview(page: Int? = null) {
        // স্মার্ট ট্যাব ল্যান্ডিং:
        // সাবস্ক্রাইবড/পেইড ইউজার সরাসরি ট্যাব ১ (সূরাসমূহ) -এ ল্যান্ড করবে,
        // আনসাবস্ক্রাইবড/গেস্ট ইউজার ট্যাব ০ (ওভারভিউ/পরিচিতি)-এ ল্যান্ড করবে।
        val initialTab = if (isUnlocked) 1 else 0
        navigate(Screen.SurahIndex(initialTab = initialTab))
    }



    /** true = অ্যাপের ভেতরে পেছনে গেছে; false = অ্যাপ বন্ধ করা উচিত */



    fun back(): Boolean {



        if (stack.size <= 1) return false



        stack.removeAt(stack.lastIndex)



        return true



    }



    fun freePagesLimit(mode: ReadingMode = settings.value.readingMode): Int =



        Publications.freePagesForMode(mode)



    fun openPreview(mode: ReadingMode = settings.value.readingMode) {



        val startPage = if (mode == ReadingMode.CLASSIC) Publications.CLASSIC_START_PAGE else 1



        openReader(startPage)



    }



    fun openSurahOrJuz(startPage: Int, mode: ReadingMode = settings.value.readingMode) {



        val target = QuranMetadata.targetPageForMode(startPage, mode)



        openReader(target)



    }



    fun openReader(page: Int? = null) {
        val mode = settings.value.readingMode
        val isClassic = mode == ReadingMode.CLASSIC
        val offset = if (isClassic) Publications.CLASSIC_PAGE_OFFSET else 0
        val totalPages = Publications.totalPagesForMode(mode) // ৯৫৩ ইন ক্লাসিক, ৬১০ ইন মুসহাফ
        val freeLimit = freePagesLimit(mode) // ১০ পৃষ্ঠা

        val defaultStart = if (isClassic) Publications.CLASSIC_START_PAGE else 1
        val rawInput = page ?: (userData.value.lastPage + offset).coerceIn(defaultStart, totalPages + offset)

        // যদি ইনপুট ১..৯৫৩ এর মতো ভিউয়ার পেজ হয়, তবে স্টোরেজ পেজে কনভার্ট করা
        val rawStart = if (isClassic && rawInput in 1..totalPages && rawInput < Publications.CLASSIC_START_PAGE) {
            rawInput + offset
        } else {
            rawInput
        }

        // ভিউয়ার পৃষ্ঠা হিসেবে যাচাই (১ থেকে ১০ ফ্রি)
        val viewerPage = if (isClassic && rawStart >= Publications.CLASSIC_START_PAGE) {
            rawStart - offset
        } else {
            rawStart
        }

        val target = if (!isUnlocked && viewerPage > freeLimit) {
            notice = "পৃষ্ঠা ${viewerPage.toBn()} প্রিমিয়াম সংস্করণ। ফ্রি প্রিভিউতে ${freeLimit.toBn()} পৃষ্ঠা পর্যন্ত উন্মুক্ত।"
            if (isClassic) (Publications.CLASSIC_START_PAGE + freeLimit - 1) else freeLimit
        } else if (isClassic && rawStart < Publications.CLASSIC_START_PAGE) {
            Publications.CLASSIC_START_PAGE
        } else {
            rawStart.coerceIn(defaultStart, totalPages + offset)
        }

        viewModelScope.launch {
            c.pages.prefetchSurrounding(target, session, mode)
        }

        if (current is Screen.Reader) {
            stack[stack.lastIndex] = Screen.Reader(target)
        } else {
            navigate(Screen.Reader(target))
        }
    }



    fun canRead(page: Int, mode: ReadingMode = settings.value.readingMode): Boolean {
        if (isUnlocked) return true
        val limit = freePagesLimit(mode) // ১০ পৃষ্ঠা ফ্রি
        val isClassic = mode == ReadingMode.CLASSIC
        val viewerPage = if (isClassic && page >= Publications.CLASSIC_START_PAGE) {
            page - Publications.CLASSIC_PAGE_OFFSET
        } else {
            page
        }
        return viewerPage in 1..limit
    }



    suspend fun loadPage(page: Int, mode: ReadingMode = settings.value.readingMode): PageResult =



        c.pages.load(page, session, mode)



    fun peekCachedPage(page: Int, mode: ReadingMode = settings.value.readingMode): PageResult? =



        c.pages.getIfCached(page, session, mode)



    fun prefetchAdjacent(centerPage: Int) {



        viewModelScope.launch(Dispatchers.IO) {



            c.pages.prefetchSurrounding(centerPage, session, settings.value.readingMode)



        }



    }



    // ---- স্ক্রিন ক্যাপচার ----



    fun onScreenCaptureDetected() {



        captureWarning = true



        viewModelScope.launch {



            c.licenses.logViolation("screen_capture_attempt", session?.licenseKey, session?.mobile)



        }



    }



    fun dismissCaptureWarning() { captureWarning = false }



    // ---- লগইন / লগআউট ----



    fun login(mobile: String, key: String) {



        if (loginUi.loading) return



        loginUi = LoginUi(loading = true)



        viewModelScope.launch {



            when (val r = c.licenses.login(mobile, key)) {



                is LoginResult.Success -> {



                    c.sessions.save(r.session)



                    session = r.session



                    loginUi = LoginUi()



                    startSync(r.session)



                    // লগইন/পেমেন্ট স্ক্রিন সরিয়ে নেভিগেশন-সিদ্ধান্ত (Login-First)
                    onAuthConfirmed()



                }



                LoginResult.Invalid -> loginUi = LoginUi(error = "মোবাইল নম্বর বা লাইসেন্স কি সঠিক নয়।")



                LoginResult.Blocked -> loginUi = LoginUi(error = "এই লাইসেন্সটি ব্লক করা আছে। সাপোর্টে যোগাযোগ করুন।")



                LoginResult.DeviceMismatch -> loginUi = LoginUi(



                    error = "এই লাইসেন্স অন্য একটি ডিভাইসে লক করা আছে। ফোন বদলালে অ্যাডমিনের কাছে ডিভাইস রিসেটের অনুরোধ করুন।"



                )



                is LoginResult.Error -> loginUi = LoginUi(error = r.message)



            }



        }



    }



    fun clearLoginError() { loginUi = loginUi.copy(error = null) }



    fun loginWithPhoneOrGoogle(mobileRaw: String, name: String? = null, email: String? = null, onComplete: () -> Unit = {}) {



        if (loginUi.loading) return



        loginUi = LoginUi(loading = true)



        viewModelScope.launch {



            val mobile = com.karianaquran.reader.data.remote.LicenseRepository.normalizeMobile(mobileRaw)



            val demo = com.karianaquran.reader.data.remote.LicenseRepository.DEMO_ACCOUNTS[mobile]



            var resolvedKey = demo?.second ?: "KQ-USER-${if (mobile.length >= 4) mobile.takeLast(4) else "0000"}"



            if (c.firebase.enabled) {

                withTimeoutOrNull(2500L) {

                    try {

                        c.firebase.ensureSignedIn()

                        val db = c.firebase.db

                        if (db != null) {

                            val licDocs = db.collection("licenses").whereEqualTo("mobile", mobile).limit(1).get().await()

                            if (!licDocs.isEmpty) {

                                resolvedKey = licDocs.documents.first().id

                            }

                            val userDoc = mapOf(

                                "mobile" to mobile,

                                "name" to (name ?: "ক্বারীয়ানা পাঠক"),

                                "email" to (email ?: ""),

                                "license_key" to resolvedKey,

                                "device_id" to c.deviceId,

                                "last_login_at" to com.google.firebase.firestore.FieldValue.serverTimestamp()

                            )

                            db.collection("users").document(mobile).set(userDoc, com.google.firebase.firestore.SetOptions.merge()).await()

                        }

                    } catch (e: Exception) {

                        c.firebase.record(e)

                    }

                }

            }



            val newSession = Session(



                mobile = mobile,



                licenseKey = resolvedKey,



                deviceId = c.deviceId,



                loginAt = System.currentTimeMillis()



            )



            c.sessions.save(newSession)



            session = newSession



            loginUi = LoginUi()



            startSync(newSession)



            // সেভ প্রোফাইল



            com.karianaquran.reader.ui.profile.UserProfileHelper.saveProfile(



                context = getApplication(),



                name = name ?: (if (!email.isNullOrBlank()) email.substringBefore("@") else "ক্বারীয়ানা পাঠক"),



                email = email ?: ""



            )



            // লগইন স্ক্রিন পপ



            onAuthConfirmed()



            onComplete()



        }



    }



    fun logout() {



        syncJob?.cancel()



        c.sessions.clear()



        c.pages.purgePaidCache()



        c.firebase.signOut()



        session = null



        viewModelScope.launch { c.settings.clearUserData() }



        // রিডারে থাকলে ফ্রি পৃষ্ঠার সীমায় ফেরা



        val top = current



        val limit = freePagesLimit()



        if (top is Screen.Reader && top.startPage > limit) {



            stack[stack.lastIndex] = Screen.Reader(limit)



        }



    }



    // ---- পেমেন্ট ----



    fun startPayment(mobileRaw: String) {



        val mobile = com.karianaquran.reader.data.remote.LicenseRepository.normalizeMobile(mobileRaw)



        if (!com.karianaquran.reader.data.remote.LicenseRepository.MOBILE_REGEX.matches(mobile)) {



            payUi = PayUi.Error("সঠিক মোবাইল নম্বর দিন (01XXXXXXXXX)।", pendingPackageId); return



        }



        payUi = PayUi.Creating(pendingPackageId ?: "standard")



        viewModelScope.launch {



            c.payments.createOrder(mobile, paymentBookIdFor(pendingPackageId), c.deviceId)



                .onSuccess { order ->



                    val pkg = packageById(pendingPackageId)

                    payUi = PayUi.Checkout(order, pkg?.id ?: "standard", pkg?.nameBn ?: "স্ট্যান্ডার্ড লাইসেন্স")



                    pollPayment(order)



                }



                .onFailure { payUi = PayUi.Error(it.message ?: "পেমেন্ট শুরু করা যায়নি।", pendingPackageId) }



        }



    }

    /** প্যাকেজ আইডি থেকে সার্ভার-সাইড বুকিং আইডি (pricing tier)। */
    fun paymentBookIdFor(packageId: String?): String =
        com.karianaquran.reader.data.model.Publications.ORTHO_QURAN_ID

    private fun packageById(id: String?): com.karianaquran.reader.ui.pay.MembershipPackage? =
        com.karianaquran.reader.ui.pay.QARIANA_PACKAGES.firstOrNull { it.id == id }

    /**
     * প্যাকেজ সিলেকশন শিটের একক এন্ট্রি-পয়েন্ট।
     * - লগইন থাকলে → সরাসরি পেমেন্ট (সেশন মোবাইল দিয়ে)।
     * - লগইন না থাকলে → pendingPackageId ধরে Login স্ক্রিনে প্রেরণ (Login-First)।
     */
    fun selectPackageAndProceed(packageId: String) {
        pendingPackageId = packageId
        payOrigin = PayOrigin.PACKAGE_PURCHASE
        if (session != null) {
            startPayment(session!!.mobile)
        } else {
            navigate(Screen.Login)
        }
    }

    /** পেমেন্ট/আনলক সফল বা বাতিলে pending প্যাকেজ ক্লিয়ার। */
    fun clearPendingPackage() { pendingPackageId = null }

    /**
     * অথ নিশ্চিত হওয়ার পর একমাত্র নেভিগেশন-সিদ্ধান্ত-বিন্দু।
     * pendingPackageId থাকলে → Pay স্ক্রিন (নির্বাচিত প্যাকেজসহ), নইলে Home-এ স্থির।
     */
    fun onAuthConfirmed() {
        // পেমেন্ট সফল হলে Pay স্ক্রিনে Success UI দেখানো হচ্ছে — সেটি পপ করা যাবে না।
        if (payUi is PayUi.Success) return
        while (stack.size > 1 && (current is Screen.Login || current is Screen.Pay)) {
            stack.removeAt(stack.lastIndex)
        }
        if (stack.isEmpty()) stack.add(Screen.Home)
        val pkgId = pendingPackageId
        if (pkgId != null) {
            navigate(Screen.Pay)
            val pkg = packageById(pkgId)
            notice = if (pkg != null)
                "লগইন সফল। '${pkg.nameBn}' প্যাকেজের পেমেন্ট সম্পন্ন করুন।"
            else "লগইন সফল হয়েছে।"
        } else {
            notice = "লগইন সফল হয়েছে। সম্পূর্ণ বই আনলক হয়েছে।"
        }
    }



    private fun pollPayment(order: PaymentOrder) {



        payJob?.cancel()



        payJob = viewModelScope.launch {



            repeat(180) { // ~১২ মিনিট



                delay(4000)



                when (val s = c.payments.status(order.orderId)) {


                    is PaymentStatus.Paid -> {



                        val donePkg = pendingPackageId ?: "standard"

                        payUi = PayUi.Success(donePkg, s.licenseKey)

                        login(s.mobile, s.licenseKey)

                        pendingPackageId = null



                        return@launch



                    }




                    is PaymentStatus.Failed -> {



                        payUi = PayUi.Error("পেমেন্ট সম্পন্ন হয়নি (${s.reason})।", pendingPackageId); return@launch



                    }



                    PaymentStatus.Pending -> Unit



                }



            }



            payUi = PayUi.Error("পেমেন্টের সময় শেষ। পেমেন্ট করে থাকলে লগইন করুন।")



        }



    }



    fun resetPay() { payJob?.cancel(); payUi = PayUi.Idle }



    // ---- রিডার সেটিংস ----



    fun updateSettings(block: (ReaderSettings) -> ReaderSettings) {



        viewModelScope.launch { c.settings.update(block) }



    }



    fun cycleTheme() = updateSettings {



        it.copy(themeId = Palettes.next(it.themeId).id, customBg = null, customArabic = null, customBangla = null)



    }



    fun setReadingMode(m: ReadingMode) = updateSettings { it.copy(readingMode = m) }



    fun toggleMeaning() = updateSettings { it.copy(showMeaning = !it.showMeaning) }



    fun toggleCropMargins() = updateSettings { it.copy(cropMargins = !it.cropMargins) }



    fun toggleRamadanCountdown() = updateSettings { it.copy(showRamadanCountdown = !it.showRamadanCountdown) }



    fun toggleAzanSound() = updateSettings { it.copy(azanSoundEnabled = !it.azanSoundEnabled) }



    // ---- বুকমার্ক / শব্দ-মার্ক / সর্বশেষ পৃষ্ঠা ----



    fun onPageSettled(page: Int) {



        viewModelScope.launch {



            c.settings.setLastPage(page)



            c.pages.prefetchSurrounding(page, session)



        }



        val s = session ?: return



        pushJob?.cancel()



        pushJob = viewModelScope.launch {



            delay(2500)



            c.sync.push(s.licenseKey, lastPage = page)



        }



    }



    fun toggleBookmark(page: Int) {



        val next = userData.value.bookmarks.toMutableSet().apply { if (!add(page)) remove(page) }



        viewModelScope.launch {



            c.settings.setBookmarks(next)



            session?.let { c.sync.push(it.licenseKey, bookmarks = next) }



        }



    }



    fun setWordMark(id: String, type: WordMarkType?) {



        val next = userData.value.wordMarks.toMutableMap().apply {



            if (type == null || get(id) == type.key) remove(id) else put(id, type.key)



        }



        viewModelScope.launch {



            c.settings.setWordMarks(next)



            session?.let { c.sync.push(it.licenseKey, marks = next) }



        }



    }



    fun setWordMarkColor(id: String, color: String) {



        val next = userData.value.wordMarks.toMutableMap().apply {



            if (color == "clear" || color.isBlank()) {



                remove(id)



            } else {



                put(id, color)



            }



        }



        viewModelScope.launch {



            c.settings.setWordMarks(next)



            session?.let { c.sync.push(it.licenseKey, marks = next) }



        }



    }



    private fun startSync(s: Session) {



        syncJob?.cancel()



        if (!c.firebase.enabled) return



        syncJob = viewModelScope.launch {



            c.sync.observe(s.licenseKey).collect { r ->



                c.settings.applyRemote(r.lastReadPage, r.bookmarks, r.wordMarks)



            }



        }



    }



}

