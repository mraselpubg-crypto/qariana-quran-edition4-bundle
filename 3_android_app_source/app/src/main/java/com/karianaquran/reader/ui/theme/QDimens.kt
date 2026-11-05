package com.karianaquran.reader.ui.theme

import androidx.compose.ui.unit.dp

/**
 * কেন্দ্রীয় স্পেসিং স্কেল (৮pt গ্রিডের উপর ভিত্তি করে)।
 * সব padding/margin/arrangement এখান থেকে নেওয়া হবে।
 */
object Spacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val l = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
}

/**
 * কেন্দ্রীয় কর্নার-রেডিয়াস স্কেল।
 */
object Radius {
    val s = 10.dp
    val m = 14.dp
    val l = 20.dp
    val xl = 28.dp
    val pill = 999.dp
}

/**
 * কেন্দ্রীয় elevation স্কেল।
 */
object Elev {
    val none = 0.dp
    val card = 2.dp
    val raised = 4.dp
    val sheet = 8.dp
}

/**
 * অ্যাপ-স্তরের ফিক্সড ডাইমেনশন।
 */
object QDimens {
    /** রিডারের একমাত্র স্লিম টপ বার */
    val ReaderTopBarHeight = 52.dp
    /** স্ট্যান্ডার্ড ন্যূনতম টাচ টার্গেট (accessibility) */
    val MinTouchTarget = 48.dp
    /** ট্যাবলেটে কনটেন্ট-প্রস্থের সীমা (stretch রোধে) */
    val MaxContentWidth = 720.dp
}
