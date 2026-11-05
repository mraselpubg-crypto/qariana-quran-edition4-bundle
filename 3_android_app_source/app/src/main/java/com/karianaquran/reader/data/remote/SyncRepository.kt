package com.karianaquran.reader.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class RemoteUserState(
    val lastReadPage: Int?,
    val bookmarks: Set<Int>?,
    val wordMarks: Map<String, String>?,
)

/** Firestore `user_data/{license}`-এ বুকমার্ক, শব্দ-মার্ক ও last_read_page রিয়েল-টাইম সিঙ্ক */
class SyncRepository(private val firebase: FirebaseGate) {

    fun observe(license: String): Flow<RemoteUserState> = callbackFlow {
        val db = firebase.db
        if (db == null) { close(); return@callbackFlow }
        firebase.ensureSignedIn()
        val reg = db.collection("user_data").document(license).addSnapshotListener { snap, _ ->
            // নিজের পেন্ডিং রাইট থেকে আসা স্ন্যাপশট উপেক্ষা (লুপ এড়াতে)
            if (snap == null || !snap.exists() || snap.metadata.hasPendingWrites()) return@addSnapshotListener
            @Suppress("UNCHECKED_CAST")
            val marks = (snap.get("word_marks") as? Map<String, Any?>)
                ?.mapNotNull { (k, v) -> (v as? String)?.let { k to it } }?.toMap()
            val books = (snap.get("bookmarks") as? List<*>)?.mapNotNull { (it as? Number)?.toInt() }?.toSet()
            trySend(RemoteUserState(snap.getLong("last_read_page")?.toInt(), books, marks))
        }
        awaitClose { reg.remove() }
    }

    suspend fun push(license: String, lastPage: Int? = null, bookmarks: Set<Int>? = null, marks: Map<String, String>? = null) {
        val db = firebase.db ?: return
        val data = HashMap<String, Any>()
        lastPage?.let { data["last_read_page"] = it }
        bookmarks?.let { data["bookmarks"] = it.sorted() }
        marks?.let { data["word_marks"] = it }
        if (data.isEmpty()) return
        data["updated_at"] = FieldValue.serverTimestamp()
        runCatching {
            firebase.ensureSignedIn()
            db.collection("user_data").document(license).set(data, SetOptions.merge()).await()
        }.onFailure { firebase.record(it) }
    }
}
