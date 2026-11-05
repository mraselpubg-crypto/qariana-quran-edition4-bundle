package com.karianaquran.reader.data.crypto

import android.content.Context
import android.graphics.Typeface
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * এনক্রিপ্টেড ভল্ট: কুরআনের পৃষ্ঠা, ফন্ট ও সেশন `filesDir/vault/`-এ
 * Android Keystore-ভিত্তিক AES-256-GCM দিয়ে সংরক্ষিত থাকে।
 * APK-এর ভেতরের বান্ডেল অ্যাসেট XOR-এনকোডেড; প্রথম রানে ভল্টে ট্রান্সফার হয়।
 */
class AssetVault(private val ctx: Context) {

    private val dir = File(ctx.filesDir, "vault").apply { mkdirs() }
    private val xorKey = "QARIANA_SECRET_KEY_9981".toByteArray(Charsets.UTF_8)

    private fun secretKey(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        kg.init(
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return kg.generateKey()
    }

    private fun resetKey() {
        runCatching {
            KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.deleteEntry(ALIAS)
        }
        dir.listFiles()?.forEach { it.delete() }
    }

    fun put(name: String, bytes: ByteArray) {
        val blob = try {
            Crypto.encrypt(secretKey(), bytes)
        } catch (e: Exception) {
            resetKey(); Crypto.encrypt(secretKey(), bytes)
        }
        val tmp = File(dir, "$name.tmp")
        tmp.writeBytes(blob)
        tmp.renameTo(File(dir, "$name.enc"))
    }

    fun get(name: String): ByteArray? {
        val f = File(dir, "$name.enc")
        if (!f.exists()) return null
        return try {
            Crypto.decrypt(secretKey(), f.readBytes())
        } catch (e: Exception) {
            f.delete(); null
        }
    }

    fun has(name: String) = File(dir, "$name.enc").exists()
    fun delete(name: String) { File(dir, "$name.enc").delete() }

    /** ভল্টে থাকা নির্দিষ্ট প্রিফিক্সযুক্ত ফাইলগুলোর নাম তালিকাভুক্ত করে (যেমন: pg_) */
    fun listKeys(prefix: String = ""): List<String> =
        dir.listFiles()
            ?.filter { it.name.startsWith(prefix) && it.name.endsWith(".enc") }
            ?.map { it.name.removeSuffix(".enc") }
            ?: emptyList()

    fun putString(name: String, value: String) = put(name, value.toByteArray(Charsets.UTF_8))
    fun getString(name: String): String? {
        val bytes = get(name)
        if (bytes != null) return bytes.toString(Charsets.UTF_8)
        // যদি এটি একটি বান্ডেল অ্যাসেট হয় (যেমন pv.dat), তাহলে KeyStore এরর হলেও সরাসরি অ্যাসেট থেকে ব্যাকআপ ডিকোড হবে
        if (BUNDLED.contains(name)) {
            return runCatching { bundled(name).toString(Charsets.UTF_8) }.getOrNull()
        }
        return null
    }

    /** বান্ডেল অ্যাসেট (XOR) পড়ে ডিকোড করে */
    private fun bundled(asset: String): ByteArray =
        Crypto.xor(ctx.assets.open("$asset.dat").use { it.readBytes() }, xorKey)

    /** প্রথম রান / ভল্ট রিসেটের পর বান্ডেল অ্যাসেট ভল্টে ইনস্টল করে */
    fun ensureInstalled() {
        for (n in BUNDLED) {
            if (get(n) == null) {
                runCatching { put(n, bundled(n)) }
            }
        }
    }

    /**
     * এনক্রিপ্টেড ফন্ট ডিক্রিপ্ট করে Typeface বানায়।
     * ফাইলটি অ্যাপের ইন্টারনাল প্রাইভেট ফোল্ডারে (filesDir/f) সুরক্ষিত থাকে
     * যাতে Skia/Compose ক্র্যাশ ছাড়া মেমরি-ম্যাপ করতে পারে।
     */
    fun loadTypeface(name: String): Typeface? {
        val fontsDir = File(ctx.filesDir, "f").apply { mkdirs() }
        val fontFile = File(fontsDir, "$name.ttf")
        if (!fontFile.exists() || fontFile.length() == 0L) {
            val bytes = get(name) ?: runCatching { bundled(name) }.getOrNull() ?: return null
            fontFile.writeBytes(bytes)
        }
        return try {
            Typeface.createFromFile(fontFile)
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val ALIAS = "qariana_vault_key_v1"
        const val FONT_ARABIC = "qf"
        const val FONT_BANGLA = "kf"
        const val PREVIEW_PAGES = "pv"
        val BUNDLED = listOf(FONT_ARABIC, FONT_BANGLA, PREVIEW_PAGES)
    }
}
