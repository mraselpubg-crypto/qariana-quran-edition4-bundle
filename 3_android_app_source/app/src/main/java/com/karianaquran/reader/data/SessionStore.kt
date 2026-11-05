package com.karianaquran.reader.data

import com.karianaquran.reader.data.crypto.AssetVault
import com.karianaquran.reader.data.model.Session
import org.json.JSONObject

/** লগইন সেশন এনক্রিপ্টেড ভল্টে রাখা হয় */
class SessionStore(private val vault: AssetVault) {
    private val name = "session"

    fun load(): Session? = runCatching {
        val j = JSONObject(vault.getString(name) ?: return null)
        Session(j.getString("mobile"), j.getString("license"), j.getString("device"), j.getLong("at"))
    }.getOrNull()

    fun save(s: Session) {
        vault.putString(
            name,
            JSONObject().put("mobile", s.mobile).put("license", s.licenseKey)
                .put("device", s.deviceId).put("at", s.loginAt).toString()
        )
    }

    fun clear() = vault.delete(name)
}
