package com.karianaquran.reader.data.remote

import com.karianaquran.reader.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class AppUpdateInfo(
    val latestVersionCode: Int,
    val latestVersionName: String,
    val isMandatory: Boolean,
    val releaseNotesBn: String,
    val downloadUrl: String,
    val fileSizeMb: Double = 41.9,
    val isPlayStoreAvailable: Boolean = false, // প্লে স্টোরে প্রকাশের পর true হবে, বর্তমানে ইন-অ্যাপ আপডেট সক্রিয়
)

sealed interface UpdateCheckResult {
    data class UpdateAvailable(val info: AppUpdateInfo) : UpdateCheckResult
    data class UpToDate(val currentVersion: String) : UpdateCheckResult
    data class Failed(val message: String) : UpdateCheckResult
}

/**
 * ইন-অ্যাপ আপডেট সিস্টেম:
 * সার্ভার/এপিআই থেকে লেটেস্ট অ্যাপ ভার্সন এবং রিলিজ নোট চেক করে।
 * ব্যবহারকারীর কড়া নির্দেশনা অনুযায়ী: নতুন কোনো আপডেট রিলিজ হলেই ব্যবহারকারীকে বাধ্যতামূলক
 * আপডেট দিতে হবে, অন্যথায় অ্যাপ ব্যবহার করা যাবে না।
 */
class UpdateRepository {

    suspend fun checkForUpdate(): UpdateCheckResult = withContext(Dispatchers.IO) {
        val endpoints = listOf(
            "https://karianaquran.com/download/app-version.json",
            "https://karianaquran.com/app-version.json",
            "${BuildConfig.PAY_API_BASE}/app-version.json",
            "https://karianaquran.com/reader/api/app_version.php"
        )
        for (url in endpoints) {
            try {
                val resp = Http.request(url = url, timeoutMs = 7_000)
                if (resp.ok && resp.body.isNotBlank()) {
                    val json = runCatching { JSONObject(resp.body) }.getOrNull() ?: continue
                    val latestCode = json.optInt("versionCode", BuildConfig.VERSION_CODE)
                    val latestName = json.optString("versionName", BuildConfig.VERSION_NAME)
                    
                    // যে কোনো নতুন ভার্সন এলে সেটি বাধ্যতামূলক (ফোর্সড) আপডেট হবে
                    val forceFlag = json.optBoolean("forceUpdate", true)
                    val mandatoryFlag = json.optBoolean("mandatory", true)
                    val isMandatory = (latestCode > BuildConfig.VERSION_CODE) && (forceFlag || mandatoryFlag)

                    val notes = if (json.has("releaseNotes")) {
                        val rn = json.opt("releaseNotes")
                        if (rn is JSONObject) rn.optString("bn", "ক্বারীয়ানা কুরআনের নতুন আপডেট প্রকাশিত হয়েছে।")
                        else rn.toString()
                    } else {
                        "ক্বারীয়ানা কুরআনের নতুন আপডেট উপলব্ধ রয়েছে। বাগ সংশোধন ও পারফরম্যান্স উন্নত করা হয়েছে।"
                    }
                    val downloadUrl = json.optString("downloadUrl", "https://karianaquran.com/download/qariana_quran.apk")
                    val fileSize = json.optDouble("fileSizeMb", 41.9)

                    if (latestCode > BuildConfig.VERSION_CODE) {
                        return@withContext UpdateCheckResult.UpdateAvailable(
                            AppUpdateInfo(
                                latestVersionCode = latestCode,
                                latestVersionName = latestName,
                                isMandatory = isMandatory,
                                releaseNotesBn = notes,
                                downloadUrl = downloadUrl,
                                fileSizeMb = fileSize,
                                isPlayStoreAvailable = false // প্লে স্টোর লিংক বর্তমানে হাইড
                            )
                        )
                    } else {
                        return@withContext UpdateCheckResult.UpToDate(BuildConfig.VERSION_NAME)
                    }
                }
            } catch (ignored: Exception) {
                // Try next endpoint
            }
        }
        UpdateCheckResult.UpToDate(BuildConfig.VERSION_NAME)
    }
}
