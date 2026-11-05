package com.karianaquran.reader.service

/**
 * ক্বারীয়ানা কুরআন - তিলাওয়াতকারী (কারী) মেটাডাটা ও সিডিএন বিল্ডার
 */
data class QuranReciter(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val isDefault: Boolean = false,
    val audioUrlBuilder: (surah: Int, ayah: Int) -> String
) {
    companion object {
        /** ব্যবহারকারীর নিজস্ব ক্লাউড হোস্টিং — ডিফল্ট তিলাওয়াত (৬,২৩৬টি আয়াত সম্পূর্ণ হোস্ট করা) */
        val DEFAULT_RECITER = QuranReciter(
            id = "kariana_api_subdomain",
            nameBn = "ক্বারীয়ানা অফিসিয়াল সিডিএন (ডিফল্ট)",
            nameEn = "Qariana Official CDN (Default)",
            isDefault = true,
            audioUrlBuilder = { surah, ayah ->
                "https://api.karianaquran.com/verse-audio-alquranbd/$surah-$ayah.mp3"
            }
        )

        /** সকল কপিরাইট-ফ্রি প্রখ্যাত কারীগণের তালিকা */
        val ALL_RECITERS = listOf(
            DEFAULT_RECITER,
            QuranReciter(
                id = "kariana_main_domain",
                nameBn = "ক্বারীয়ানা মেইন ডোমেইন",
                nameEn = "Qariana Main Domain",
                audioUrlBuilder = { surah, ayah ->
                    "https://karianaquran.com/verse-audio-alquranbd/$surah-$ayah.mp3"
                }
            ),
            QuranReciter(
                id = "alquranbd_7",
                nameBn = "আল-কুরআন বিডি ব্যাকআপ সিডিএন",
                nameEn = "AlQuranBD Backup CDN",
                audioUrlBuilder = { surah, ayah ->
                    "https://cdn.alquranbd.org/verse-audio/7/$surah-$ayah.mp3"
                }
            ),
            QuranReciter(
                id = "alafasy_128",
                nameBn = "মিশারী রাশিদ আল-আফাসী",
                nameEn = "Mishary Rashid Alafasy",
                audioUrlBuilder = { surah, ayah ->
                    "https://everyayah.com/data/Alafasy_128kbps/%03d%03d.mp3".format(surah, ayah)
                }
            ),
            QuranReciter(
                id = "abdul_basit",
                nameBn = "আব্দুল বাসিত আব্দুল সামাদ (মুরাত্তাল)",
                nameEn = "Abdul Basit Murattal",
                audioUrlBuilder = { surah, ayah ->
                    "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/%03d%03d.mp3".format(surah, ayah)
                }
            ),
            QuranReciter(
                id = "husary",
                nameBn = "মাহমুদ খলিল আল-হুসারী (শিক্ষক পদ্ধতি)",
                nameEn = "Mahmoud Khalil Al-Husary",
                audioUrlBuilder = { surah, ayah ->
                    "https://everyayah.com/data/Husary_128kbps/%03d%03d.mp3".format(surah, ayah)
                }
            ),
            QuranReciter(
                id = "shuraim",
                nameBn = "সাউদ আশ-শুরাইম (মসজিদুল হারাম)",
                nameEn = "Saud Al-Shuraim",
                audioUrlBuilder = { surah, ayah ->
                    "https://everyayah.com/data/Saood_ash-Shuraym_128kbps/%03d%03d.mp3".format(surah, ayah)
                }
            ),
            QuranReciter(
                id = "muaiqly",
                nameBn = "মাহের আল-মুয়াইকিলী",
                nameEn = "Maher Al-Muaiqly",
                audioUrlBuilder = { surah, ayah ->
                    "https://everyayah.com/data/MaherAlMuaiqly128kbps/%03d%03d.mp3".format(surah, ayah)
                }
            )
        )

        fun getById(id: String?): QuranReciter =
            ALL_RECITERS.firstOrNull { it.id == id } ?: DEFAULT_RECITER
    }
}
