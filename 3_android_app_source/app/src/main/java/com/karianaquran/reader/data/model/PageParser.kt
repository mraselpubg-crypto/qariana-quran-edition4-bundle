package com.karianaquran.reader.data.model

import org.json.JSONArray
import org.json.JSONObject

/** quran_pages.json স্কিমা পার্সার */
object PageParser {

    /** { "1": {...}, "2": {...} } ফরম্যাট পার্স করে */
    fun parseAll(json: String): Map<Int, QuranPage> {
        val root = JSONObject(json)
        val out = LinkedHashMap<Int, QuranPage>()
        val keys = root.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            val n = k.toIntOrNull() ?: continue
            out[n] = parsePage(root.getJSONObject(k), n)
        }
        return out
    }

    /** একক পৃষ্ঠার অবজেক্ট (রিমোট সোর্সের জন্য) */
    fun parseSingle(json: String, fallbackPage: Int): QuranPage {
        val o = JSONObject(json)
        return parsePage(o, o.optInt("page", fallbackPage))
    }

    private fun parsePage(o: JSONObject, page: Int): QuranPage {
        val rows = o.optJSONArray("rows").toList { r ->
            val words = r.optJSONArray("words").toStringList()
            QuranRow(
                index = r.optInt("row_index"),
                arabic = r.optString("arabic"),
                meaning = r.optString("meaning"),
                translit = r.optString("translit"),
                ayahRef = r.optString("ayah_ref"),
                surahName = r.optString("surah_name"),
                words = words.ifEmpty { r.optString("arabic").split(' ').filter { it.isNotBlank() } },
                wordDetails = r.optJSONArray("word_details").toList { d ->
                    WordDetail(d.optString("translit"), d.optString("meaning"))
                },
            )
        }
        val verses = o.optJSONArray("verses").toList { v ->
            Verse(
                ayah = v.optInt("ayah"),
                ayahRef = v.optString("ayah_ref"),
                arabic = v.optString("arabic"),
                transliteration = v.optString("transliteration"),
                translation = v.optString("translation"),
                words = v.optJSONArray("words").toStringList()
                    .ifEmpty { v.optString("arabic").split(' ').filter { it.isNotBlank() } },
                tafsir = v.optString("tafsir").takeIf { it.isNotBlank() },
            )
        }
        return QuranPage(
            page = page,
            juz = o.optInt("juz"),
            surahNameAr = o.optString("surah_name_ar"),
            surahNameBn = o.optString("surah_name_bn"),
            place = o.optString("place"),
            headerLeftSub = o.optString("header_left_sub"),
            rows = rows,
            verses = verses,
        )
    }

    fun toJson(page: QuranPage): String {
        val o = JSONObject()
        o.put("page", page.page)
        o.put("juz", page.juz)
        o.put("surah_name_ar", page.surahNameAr)
        o.put("surah_name_bn", page.surahNameBn)
        o.put("header_center_title", if (page.surahNameBn.isNotBlank()) page.surahNameBn else page.surahNameAr)
        o.put("place", page.place)
        o.put("header_left_sub", page.headerLeftSub)

        val rowsArr = JSONArray()
        for (r in page.rows) {
            val ro = JSONObject()
            ro.put("row_index", r.index)
            ro.put("arabic", r.arabic)
            ro.put("meaning", r.meaning)
            ro.put("translit", r.translit)
            ro.put("ayah_ref", r.ayahRef)
            ro.put("surah_name", r.surahName)
            val wArr = JSONArray()
            for (w in r.words) wArr.put(w)
            ro.put("words", wArr)
            rowsArr.put(ro)
        }
        o.put("rows", rowsArr)

        val vArr = JSONArray()
        for (v in page.verses) {
            val vo = JSONObject()
            vo.put("ayah", v.ayah)
            vo.put("ayah_ref", v.ayahRef)
            vo.put("arabic", v.arabic)
            vo.put("transliteration", v.transliteration)
            vo.put("translation", v.translation)
            val wArr = JSONArray()
            for (w in v.words) wArr.put(w)
            vo.put("words", wArr)
            vo.put("tafsir", v.tafsir ?: "")
            vArr.put(vo)
        }
        o.put("verses", vArr)
        return o.toString()
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return List(length()) { optString(it) }
    }

    private inline fun <T> JSONArray?.toList(transform: (JSONObject) -> T): List<T> {
        if (this == null) return emptyList()
        val list = ArrayList<T>(length())
        for (i in 0 until length()) optJSONObject(i)?.let { list.add(transform(it)) }
        return list
    }
}
