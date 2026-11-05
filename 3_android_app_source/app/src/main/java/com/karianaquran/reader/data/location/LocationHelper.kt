package com.karianaquran.reader.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class District(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val divisionBn: String,
    val lat: Double,
    val lon: Double
)

object LocationHelper {

    private const val PREFS_NAME = "qariana_location_prefs"
    private const val KEY_DISTRICT_ID = "selected_district_id"
    private const val KEY_PERM_REQUESTED = "perm_requested_flag"

    val defaultDistrict = District("dhaka", "ঢাকা", "Dhaka", "ঢাকা", 23.8103, 90.4125)

    // বাংলাদেশের ৬৪ জেলার পূর্ণাঙ্গ নির্ভুল তালিকা ও স্থানাঙ্ক
    val districts = listOf(
        // ঢাকা বিভাগ
        District("dhaka", "ঢাকা", "Dhaka", "ঢাকা", 23.8103, 90.4125),
        District("gazipur", "গাজীপুর", "Gazipur", "ঢাকা", 24.0023, 90.4264),
        District("narayanganj", "নারায়ণগঞ্জ", "Narayanganj", "ঢাকা", 23.6238, 90.5000),
        District("tangail", "টাঙ্গাইল", "Tangail", "ঢাকা", 24.2513, 89.9167),
        District("faridpur", "ফরিদপুর", "Faridpur", "ঢাকা", 23.6071, 89.8426),
        District("kallanpur", "কিশোরগঞ্জ", "Kishoreganj", "ঢাকা", 24.4449, 90.7766),
        District("manikganj", "মানিকগঞ্জ", "Manikganj", "ঢাকা", 23.8644, 90.0047),
        District("munshiganj", "মুন্সিগঞ্জ", "Munshiganj", "ঢাকা", 23.5422, 90.5305),
        District("narsingdi", "নরসিংদী", "Narsingdi", "ঢাকা", 23.9193, 90.7202),
        District("rajbari", "রাজবাড়ী", "Rajbari", "ঢাকা", 23.7574, 89.6445),
        District("gopalganj", "গোপালগঞ্জ", "Gopalganj", "ঢাকা", 23.0051, 89.8266),
        District("madaripur", "মাদারীপুর", "Madaripur", "ঢাকা", 23.1641, 90.1897),
        District("shariatpur", "শরীয়তপুর", "Shariatpur", "ঢাকা", 23.2423, 90.4348),

        // চট্টগ্রাম বিভাগ
        District("chittagong", "চট্টগ্রাম", "Chittagong", "চট্টগ্রাম", 22.3569, 91.7832),
        District("coxsbazar", "কক্সবাজার", "Cox's Bazar", "চট্টগ্রাম", 21.4272, 92.0058),
        District("comilla", "কুমিল্লা", "Cumilla", "চট্টগ্রাম", 23.4682, 91.1788),
        District("feni", "ফেনী", "Feni", "চট্টগ্রাম", 23.0186, 91.3966),
        District("brahmanbaria", "ব্রাহ্মণবাড়িয়া", "Brahmanbaria", "চট্টগ্রাম", 23.9571, 91.1119),
        District("noakhali", "নোয়াখালী", "Noakhali", "চট্টগ্রাম", 22.8696, 91.0998),
        District("lakshmipur", "লক্ষ্মীপুর", "Lakshmipur", "চট্টগ্রাম", 22.9425, 90.8412),
        District("chandpur", "চাঁদপুর", "Chandpur", "চট্টগ্রাম", 23.2333, 90.6667),
        District("rangamati", "রাঙ্গামাটি", "Rangamati", "চট্টগ্রাম", 22.7324, 92.2985),
        District("bandarban", "বান্দরবান", "Bandarban", "চট্টগ্রাম", 22.1953, 92.2184),
        District("khagrachhari", "খাগড়াছড়ি", "Khagrachhari", "চট্টগ্রাম", 23.1193, 91.9847),

        // রাজশাহী বিভাগ
        District("rajshahi", "রাজশাহী", "Rajshahi", "রাজশাহী", 24.3636, 88.6241),
        District("bogra", "বগুড়া", "Bogura", "রাজশাহী", 24.8465, 89.3770),
        District("pabna", "পাবনা", "Pabna", "রাজশাহী", 24.0064, 89.2372),
        District("sirajganj", "সিরাজগঞ্জ", "Sirajganj", "রাজশাহী", 24.4534, 89.7008),
        District("naogaon", "নওগাঁ", "Naogaon", "রাজশাহী", 24.7936, 88.9318),
        District("natore", "নাটোর", "Natore", "রাজশাহী", 24.4206, 88.9322),
        District("chapainawabganj", "চাঁপাইনবাবগঞ্জ", "Chapainawabganj", "রাজশাহী", 24.5965, 88.2775),
        District("joypurhat", "জয়পুরহাট", "Joypurhat", "রাজশাহী", 25.1015, 89.0277),

        // খুলনা বিভাগ
        District("khulna", "খুলনা", "Khulna", "খুলনা", 22.8456, 89.5403),
        District("jessore", "যশোর", "Jashore", "খুলনা", 23.1664, 89.2081),
        District("kushtia", "কুষ্টিয়া", "Kushtia", "খুলনা", 23.9013, 89.1205),
        District("satkhira", "সাতক্ষীরা", "Satkhira", "খুলনা", 22.7185, 89.0705),
        District("bagerhat", "বাগেরহাট", "Bagerhat", "খুলনা", 22.6516, 89.7859),
        District("chuadanga", "চুয়াডাঙ্গা", "Chuadanga", "খুলনা", 23.6402, 88.8418),
        District("jhenaidah", "ঝিনাইদহ", "Jhenaidah", "খুলনা", 23.5448, 89.1539),
        District("magura", "মাগুরা", "Magura", "খুলনা", 23.4873, 89.4198),
        District("meherpur", "মেহেরপুর", "Meherpur", "খুলনা", 23.7622, 88.6318),
        District("narail", "নড়াইল", "Narail", "খুলনা", 23.1725, 89.5127),

        // বরিশাল বিভাগ
        District("barisal", "বরিশাল", "Barishal", "বরিশাল", 22.7010, 90.3535),
        District("bhola", "ভোলা", "Bhola", "বরিশাল", 22.6859, 90.6482),
        District("patuakhali", "পটুয়াখালী", "Patuakhali", "বরিশাল", 22.3596, 90.3299),
        District("pirojpur", "পিরোজপুর", "Pirojpur", "বরিশাল", 22.5841, 89.9720),
        District("barguna", "বরগুনা", "Barguna", "বরিশাল", 22.0953, 90.1121),
        District("jhalokati", "ঝালকাঠি", "Jhalokati", "বরিশাল", 22.6406, 90.1987),

        // সিলেট বিভাগ
        District("sylhet", "সিলেট", "Sylhet", "সিলেট", 24.8949, 91.8687),
        District("moulvibazar", "মৌলভীবাজার", "Moulvibazar", "সিলেট", 24.4829, 91.7774),
        District("habiganj", "হবিগঞ্জ", "Habiganj", "সিলেট", 24.3749, 91.4155),
        District("sunamganj", "সুনামগঞ্জ", "Sunamganj", "সিলেট", 25.0658, 91.3950),

        // রংপুর বিভাগ
        District("rangpur", "রংপুর", "Rangpur", "রংপুর", 25.7439, 89.2752),
        District("dinajpur", "দিনাজপুর", "Dinajpur", "রংপুর", 25.6217, 88.6355),
        District("kurigram", "কুড়িগ্রাম", "Kurigram", "রংপুর", 25.8054, 89.6362),
        District("gaibandha", "গাইবান্ধা", "Gaibandha", "রংপুর", 25.3288, 89.5408),
        District("nilphamari", "নীলফামারী", "Nilphamari", "রংপুর", 25.9318, 88.8560),
        District("panchagarh", "পঞ্চগড়", "Panchagarh", "রংপুর", 26.3411, 88.5542),
        District("thakurgaon", "ঠাকুরগাঁও", "Thakurgaon", "রংপুর", 26.0337, 88.4617),
        District("lalmonirhat", "লালমনিরহাট", "Lalmonirhat", "রংপুর", 25.9923, 89.2847),

        // ময়মনসিংহ বিভাগ
        District("mymensingh", "ময়মনসিংহ", "Mymensingh", "ময়মনসিংহ", 24.7471, 90.4203),
        District("jamalpur", "জামালপুর", "Jamalpur", "ময়মনসিংহ", 24.9375, 89.9378),
        District("netrokona", "নেত্রকোণা", "Netrokona", "ময়মনসিংহ", 24.8709, 90.7279),
        District("sherpur", "শেরপুর", "Sherpur", "ময়মনসিংহ", 25.0205, 90.0153)
    )

    fun getSelectedDistrict(context: Context): District {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = prefs.getString(KEY_DISTRICT_ID, defaultDistrict.id) ?: defaultDistrict.id
        return districts.find { it.id == id } ?: defaultDistrict
    }

    fun saveSelectedDistrict(context: Context, district: District) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_DISTRICT_ID, district.id).apply()
    }

    fun isPermissionRequested(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_PERM_REQUESTED, false)
    }

    fun setPermissionRequested(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_PERM_REQUESTED, true).apply()
    }

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun findClosestDistrict(lat: Double, lon: Double): District {
        var closest = defaultDistrict
        var minDistance = Double.MAX_VALUE

        for (d in districts) {
            val dist = calculateDistanceKm(lat, lon, d.lat, d.lon)
            if (dist < minDistance) {
                minDistance = dist
                closest = d
            }
        }
        return closest
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // পৃথিবীর ব্যাসার্ধ (কিমি)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    @SuppressLint("MissingPermission")
    fun detectDeviceLocation(context: Context, onResult: (District) -> Unit) {
        if (!hasLocationPermission(context)) {
            onResult(getSelectedDistrict(context))
            return
        }
        try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val lastGps = lm?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastNetwork = lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            val bestLocation: Location? = when {
                lastGps != null && lastNetwork != null -> if (lastGps.time > lastNetwork.time) lastGps else lastNetwork
                lastGps != null -> lastGps
                else -> lastNetwork
            }

            if (bestLocation != null) {
                val matched = findClosestDistrict(bestLocation.latitude, bestLocation.longitude)
                saveSelectedDistrict(context, matched)
                onResult(matched)
            } else {
                onResult(getSelectedDistrict(context))
            }
        } catch (_: Exception) {
            onResult(getSelectedDistrict(context))
        }
    }
}
