package com.karianaquran.reader.security

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.VisibleForTesting
import com.karianaquran.reader.BuildConfig
import java.io.File

/**
 * ডিভাইস সিকিউরিটি ও টেম্পার স্টেট নির্ধারণ:
 * রুট, এমুলেটর, পিট্রেস (ptrace) ডিবাগার, USB ডিবাগিং ও হুকিং ফ্রেমওয়ার্ক (Frida/Xposed)।
 */
object SecurityGuard {

    enum class TamperState {
        Clean,
        RootedDevice,
        SecurityRiskOrDevEnvironment
    }

    // ব্যাকওয়ার্ড কম্প্যাটিবিলিটির জন্য রেজাল্ট ডেটা ক্লাস রাখা হয়েছে
    data class Result(
        val rooted: Boolean,
        val emulator: Boolean,
        val adb: Boolean,
        val hooking: Boolean,
        val devOptions: Boolean = false,
        val ptrace: Boolean = false,
        val state: TamperState = TamperState.Clean,
    ) {
        val violation: Boolean get() = rooted || emulator || hooking || devOptions || ptrace
        val clean: Boolean get() = state == TamperState.Clean
        
        // অভ্যন্তরীণ নিরীক্ষার জন্য reasons() রাখা হয়েছে, কিন্তু ব্যবহারকারীকে দেখানো হবে না
        fun reasons(): List<String> = buildList {
            if (rooted) add("রুটেড ডিভাইস")
            if (emulator) add("এমুলেটর")
            if (adb) add("USB ডিবাগিং চালু")
            if (devOptions) add("ডেভেলপার অপশন চালু")
            if (ptrace) add("ডিবাগার সংযুক্ত")
            if (hooking) add("হুকিং টুল শনাক্ত")
        }
    }

    // টেস্টিং ওভাররাইড হুকস (ইউনিট টেস্টের জন্য)
    @VisibleForTesting @Volatile var testOverrideState: TamperState? = null
    @VisibleForTesting @Volatile var testOverrideRoot: Boolean? = null
    @VisibleForTesting @Volatile var testOverrideDevRisk: Boolean? = null
    @VisibleForTesting @Volatile var testOverrideTracerPid: Int? = null
    @VisibleForTesting @Volatile var testOverrideAdb: Boolean? = null
    @VisibleForTesting @Volatile var testOverrideDevOptions: Boolean? = null
    @VisibleForTesting @Volatile var testOverrideSuInPath: Boolean? = null
    @VisibleForTesting @Volatile var testOverrideMounts: Boolean? = null
    @VisibleForTesting @Volatile var testOverrideHooking: Boolean? = null
    @VisibleForTesting @Volatile var testOverrideEmulator: Boolean? = null

    fun resetTestOverrides() {
        testOverrideState = null
        testOverrideRoot = null
        testOverrideDevRisk = null
        testOverrideTracerPid = null
        testOverrideAdb = null
        testOverrideDevOptions = null
        testOverrideSuInPath = null
        testOverrideMounts = null
        testOverrideHooking = null
        testOverrideEmulator = null
    }

    fun evaluate(ctx: Context): Result {
        val rooted = isRooted(ctx)
        val emulator = isEmulator()
        val adb = isAdbEnabled(ctx)
        val devOptions = isDevOptionsEnabled(ctx)
        val ptrace = isPtraceTraced()
        val hooking = isHooked(ctx)
        val state = checkReaderAccess(ctx)

        return Result(
            rooted = rooted,
            emulator = emulator,
            adb = adb,
            hooking = hooking,
            devOptions = devOptions,
            ptrace = ptrace,
            state = state
        )
    }

    /** সম্পূর্ণ অ্যাপ ক্র্যাশ বা লক করা হয় না, সাধারণ স্ক্রিন সবসময় উন্মুক্ত থাকে */
    fun shouldBlock(result: Result): Boolean = false

    private val SU_PATHS = arrayOf(
        "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
        "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
        "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su", "/system/xbin/daemonsu",
        "/magisk/.core/bin/su", "/sbin/.magisk", "/data/adb/magisk",
        "/data/adb/ksu", "/data/adb/ap"
    )

    private val ROOT_PACKAGES = arrayOf(
        "com.topjohnwu.magisk", "eu.chainfire.supersu", "com.noshufou.android.su",
        "com.koushikdutta.superuser", "com.thirdparty.superuser", "com.yellowes.su",
        "me.weishu.kernelsu", "io.github.vvb2060.magisk",
    )

    private val HOOK_PACKAGES = arrayOf(
        "de.robv.android.xposed.installer", "org.lsposed.manager", "com.saurik.substrate",
    )

    /**
     * রুট ডিটেকশন:
     * ১. SU_PATHS ফাইল চেক
     * ২. PATH এনভায়রনমেন্ট ভ্যারিয়েবলে su বাইনারি স্ক্যান
     * ৩. /proc/mounts-এ magisk বা সুপারইউজার মাউন্ট চেক
     * ৪. Build.TAGS test-keys চেক (এমুলেটর ছাড়া)
     * ৫. রুট প্যাকেজ যাচাই
     */
    fun isRooted(ctx: Context? = null): Boolean {
        testOverrideRoot?.let { return it }

        // ১. Build.TAGS চেক
        val isEmu = isEmulator()
        if (!isEmu && runCatching { Build.TAGS?.contains("test-keys") == true }.getOrDefault(false)) {
            return true
        }

        // ২. স্ট্যাটিক su পাথ চেক
        if (SU_PATHS.any { runCatching { File(it).exists() }.getOrDefault(false) }) {
            return true
        }

        // ৩. PATH স্ক্যান
        if (isSuInPath()) {
            return true
        }

        // ৪. /proc/mounts চেক
        if (isSuspiciousMountPresent()) {
            return true
        }

        // ৫. ইনস্টলড প্যাকেজ চেক (যদি Context থাকে)
        if (ctx != null) {
            val pm = runCatching { ctx.packageManager }.getOrNull()
            if (pm != null && ROOT_PACKAGES.any { pkgInstalled(pm, it) }) {
                return true
            }
        }

        return false
    }

    /** PATH এনভায়রনমেন্টে su বাইনারি অনুসন্ধান */
    fun isSuInPath(): Boolean {
        testOverrideSuInPath?.let { return it }
        val pathEnv = runCatching { System.getenv("PATH") }.getOrNull() ?: return false
        val separator = if (pathEnv.contains(";")) ";" else ":"
        return pathEnv.split(separator).any { dir ->
            if (dir.isBlank()) false
            else runCatching { File(dir.trim(), "su").exists() }.getOrDefault(false)
        }
    }

    /** /proc/mounts ফাইলে রুটের মাউন্ট পয়েন্ট শনাক্তকরণ */
    fun isSuspiciousMountPresent(): Boolean {
        testOverrideMounts?.let { return it }
        val mountsFile = File("/proc/mounts")
        if (!mountsFile.exists() || !mountsFile.canRead()) return false
        val rootMountKeywords = listOf("magisk", "core/mirror", "core/img", "supersu", ".magisk")
        return runCatching {
            mountsFile.useLines { lines ->
                lines.any { line ->
                    val lower = line.lowercase()
                    rootMountKeywords.any { kw -> lower.contains(kw) }
                }
            }
        }.getOrDefault(false)
    }

    /**
     * /proc/self/status থেকে TracerPid পরিদর্শন:
     * TracerPid > 0 হলে কোনো নেটিভ ডিবাগার (GDB, LLDB, Frida attach) প্রসেস ট্রেস করছে
     */
    fun isPtraceTraced(): Boolean {
        testOverrideTracerPid?.let { return it > 0 }
        val statusFile = File("/proc/self/status")
        if (!statusFile.exists() || !statusFile.canRead()) return false
        return runCatching {
            statusFile.useLines { lines ->
                for (line in lines) {
                    if (line.startsWith("TracerPid:", ignoreCase = true)) {
                        val pid = line.substringAfter(":").trim().toIntOrNull() ?: 0
                        return@useLines pid > 0
                    }
                }
                false
            }
        }.getOrDefault(false)
    }

    fun isAdbEnabled(ctx: Context?): Boolean {
        testOverrideAdb?.let { return it }
        if (ctx == null) return false
        return runCatching {
            val cr = ctx.contentResolver ?: return false
            Settings.Global.getInt(cr, Settings.Global.ADB_ENABLED, 0) == 1
        }.getOrDefault(false)
    }

    fun isDevOptionsEnabled(ctx: Context?): Boolean {
        testOverrideDevOptions?.let { return it }
        if (ctx == null) return false
        return runCatching {
            val cr = ctx.contentResolver ?: return false
            Settings.Global.getInt(cr, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1
        }.getOrDefault(false)
    }

    fun isDevOptionsOrAdbEnabled(ctx: Context?): Boolean {
        return isAdbEnabled(ctx) || isDevOptionsEnabled(ctx)
    }

    fun isDebuggerAttached(): Boolean {
        return runCatching {
            android.os.Debug.isDebuggerConnected() || android.os.Debug.waitingForDebugger()
        }.getOrDefault(false)
    }

    fun isEmulator(): Boolean {
        testOverrideEmulator?.let { return it }
        val fp = runCatching { Build.FINGERPRINT.lowercase() }.getOrDefault("")
        val model = runCatching { Build.MODEL.lowercase() }.getOrDefault("")
        val manu = runCatching { Build.MANUFACTURER.lowercase() }.getOrDefault("")
        val hw = runCatching { Build.HARDWARE.lowercase() }.getOrDefault("")
        val prod = runCatching { Build.PRODUCT.lowercase() }.getOrDefault("")
        val brand = runCatching { Build.BRAND.lowercase() }.getOrDefault("")
        val device = runCatching { Build.DEVICE.lowercase() }.getOrDefault("")
        val board = runCatching { Build.BOARD.lowercase() }.getOrDefault("")

        return fp.startsWith("generic") || fp.startsWith("unknown") || fp.contains("emulator") ||
            model.contains("google_sdk") || model.contains("emulator") || model.contains("android sdk built for") ||
            manu.contains("genymotion") || hw.contains("goldfish") || hw.contains("ranchu") ||
            prod.contains("sdk_gphone") || (prod.contains("sdk") && prod.contains("google")) ||
            (brand.startsWith("generic") && device.startsWith("generic")) ||
            (board.contains("unknown") && hw.contains("vbox"))
    }

    /** Frida / Xposed হুকের লক্ষণ: প্রসেস ম্যাপে সন্দেহজনক লাইব্রেরি বা ডিবাগার সংযোগ */
    fun isHooked(ctx: Context? = null): Boolean {
        testOverrideHooking?.let { return it }
        if (isDebuggerAttached() || isPtraceTraced()) return true
        val markers = listOf("frida", "gum-js-loop", "gmain", "linjector", "XposedBridge", "substrate", "libfrida", "gadget")
        val mapsFile = File("/proc/self/maps")
        val hit = if (mapsFile.exists() && mapsFile.canRead()) {
            runCatching {
                mapsFile.useLines { lines ->
                    lines.any { line -> markers.any { m -> line.contains(m, ignoreCase = true) && !line.contains("gmain-") } }
                }
            }.getOrDefault(false)
        } else false
        if (hit) return true
        if (ctx != null) {
            val pm = runCatching { ctx.packageManager }.getOrNull()
            if (pm != null && HOOK_PACKAGES.any { pkgInstalled(pm, it) }) return true
        }
        return false
    }

    private fun pkgInstalled(pm: android.content.pm.PackageManager, pkg: String): Boolean = try {
        @Suppress("DEPRECATION") pm.getPackageInfo(pkg, 0); true
    } catch (_: Exception) { false }

    /**
     * কুরআন রিডার অ্যাক্সেস পারমিশন মূল্যায়ন:
     * ১. রুট থাকলে -> TamperState.RootedDevice
     * ২. USB ডিবাগিং, ডেভেলপার অপশন, পিট্রেস বা হুকিং থাকলে -> TamperState.SecurityRiskOrDevEnvironment
     * ৩. নিরাপদ ডিভাইস -> TamperState.Clean
     */
    fun checkReaderAccess(ctx: Context?): TamperState {
        testOverrideState?.let { return it }

        if (!BuildConfig.ENFORCE_DEVICE_SECURITY) {
            return TamperState.Clean
        }

        if (isRooted(ctx)) {
            return TamperState.RootedDevice
        }

        testOverrideDevRisk?.let { if (it) return TamperState.SecurityRiskOrDevEnvironment }

        val devOrAdb = isDevOptionsOrAdbEnabled(ctx)
        val ptrace = isPtraceTraced()
        val hooked = isHooked(ctx)
        val emulator = isEmulator()
        val debugger = isDebuggerAttached()

        if (devOrAdb || ptrace || hooked || emulator || debugger) {
            return TamperState.SecurityRiskOrDevEnvironment
        }

        return TamperState.Clean
    }

    /**
     * ডেটা ডিক্রিপশনের অনুমতি যাচাই:
     * শুধুমাত্র Clean স্টেটে ডিক্রিপশন চাবি ও প্লেইনটেক্সট অনুমোদিত।
     */
    fun isAllowedToDecrypt(ctx: Context?): Boolean {
        return checkReaderAccess(ctx) == TamperState.Clean
    }
}
