package com.karianaquran.reader.security

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * SecurityGuard ইউনিট টেস্ট:
 * ১. ডিফল্ট নিরাপদ ডিভাইসে TamperState.Clean
 * ২. রুটেড ডিভাইসে TamperState.RootedDevice
 * ৩. ADB / ptrace TracerPid / হুকিং / ডেভেলপার অপশনে TamperState.SecurityRiskOrDevEnvironment
 * ৪. ডিক্রিপশন অনুমোদন (isAllowedToDecrypt) শুধুমাত্র Clean স্টেটে সত্য, অন্যথায় মিথ্যা
 * ৫. সম্পূর্ণ অ্যাপ লেভেলে shouldBlock সবসময় false (হোম ও সেটিংস খোলা থাকে)
 */
class SecurityGuardTest {

    @Before
    fun setUp() {
        SecurityGuard.resetTestOverrides()
    }

    @After
    fun tearDown() {
        SecurityGuard.resetTestOverrides()
    }

    @Test
    fun testDefaultCleanStateYieldsClean() {
        // কোনো হুমকি না থাকলে Clean স্টেট
        SecurityGuard.testOverrideRoot = false
        SecurityGuard.testOverrideAdb = false
        SecurityGuard.testOverrideDevOptions = false
        SecurityGuard.testOverrideTracerPid = 0
        SecurityGuard.testOverrideHooking = false
        SecurityGuard.testOverrideEmulator = false
        SecurityGuard.testOverrideSuInPath = false
        SecurityGuard.testOverrideMounts = false

        val state = SecurityGuard.checkReaderAccess(null)
        assertEquals(SecurityGuard.TamperState.Clean, state)
        assertTrue("Clean স্টেটে ডিক্রিপশন অনুমোদিত হওয়া উচিত", SecurityGuard.isAllowedToDecrypt(null))
    }

    @Test
    fun testRootDetectionViaSuInPathYieldsRootedDevice() {
        SecurityGuard.testOverrideSuInPath = true

        val isRoot = SecurityGuard.isRooted(null)
        assertTrue("PATH-এ su বাইনারি থাকলে রুট সনাক্ত হবে", isRoot)

        val state = SecurityGuard.checkReaderAccess(null)
        assertEquals(SecurityGuard.TamperState.RootedDevice, state)
        assertFalse("রুটেড ডিভাইসে ডিক্রিপশন উইথহোল্ড হবে", SecurityGuard.isAllowedToDecrypt(null))
    }

    @Test
    fun testRootDetectionViaMountsYieldsRootedDevice() {
        SecurityGuard.testOverrideMounts = true

        val isRoot = SecurityGuard.isRooted(null)
        assertTrue("সন্দেহজনক মাউন্টে রুট সনাক্ত হবে", isRoot)

        val state = SecurityGuard.checkReaderAccess(null)
        assertEquals(SecurityGuard.TamperState.RootedDevice, state)
        assertFalse(SecurityGuard.isAllowedToDecrypt(null))
    }

    @Test
    fun testRootOverrideYieldsRootedDevice() {
        SecurityGuard.testOverrideRoot = true

        val state = SecurityGuard.checkReaderAccess(null)
        assertEquals(SecurityGuard.TamperState.RootedDevice, state)
        assertFalse(SecurityGuard.isAllowedToDecrypt(null))
    }

    @Test
    fun testPtraceTracerPidYieldsSecurityRiskOrDevEnvironment() {
        SecurityGuard.testOverrideRoot = false
        SecurityGuard.testOverrideTracerPid = 1337 // ptrace সংযুক্ত PID > 0

        assertTrue("TracerPid > 0 হলে ptrace ট্রেসিং সক্রিয়", SecurityGuard.isPtraceTraced())

        val state = SecurityGuard.checkReaderAccess(null)
        assertEquals(SecurityGuard.TamperState.SecurityRiskOrDevEnvironment, state)
        assertFalse("ডিবাগার ট্রেসিং অবস্থায় ডিক্রিপশন উইথহোল্ড হবে", SecurityGuard.isAllowedToDecrypt(null))
    }

    @Test
    fun testAdbEnabledYieldsSecurityRiskOrDevEnvironment() {
        SecurityGuard.testOverrideRoot = false
        SecurityGuard.testOverrideAdb = true

        val state = SecurityGuard.checkReaderAccess(null)
        assertEquals(SecurityGuard.TamperState.SecurityRiskOrDevEnvironment, state)
        assertFalse(SecurityGuard.isAllowedToDecrypt(null))
    }

    @Test
    fun testDevOptionsEnabledYieldsSecurityRiskOrDevEnvironment() {
        SecurityGuard.testOverrideRoot = false
        SecurityGuard.testOverrideDevOptions = true

        val state = SecurityGuard.checkReaderAccess(null)
        assertEquals(SecurityGuard.TamperState.SecurityRiskOrDevEnvironment, state)
        assertFalse(SecurityGuard.isAllowedToDecrypt(null))
    }

    @Test
    fun testHookingYieldsSecurityRiskOrDevEnvironment() {
        SecurityGuard.testOverrideRoot = false
        SecurityGuard.testOverrideHooking = true

        val state = SecurityGuard.checkReaderAccess(null)
        assertEquals(SecurityGuard.TamperState.SecurityRiskOrDevEnvironment, state)
        assertFalse(SecurityGuard.isAllowedToDecrypt(null))
    }

    @Test
    fun testEmulatorYieldsSecurityRiskOrDevEnvironment() {
        SecurityGuard.testOverrideRoot = false
        SecurityGuard.testOverrideEmulator = true

        val state = SecurityGuard.checkReaderAccess(null)
        assertEquals(SecurityGuard.TamperState.SecurityRiskOrDevEnvironment, state)
        assertFalse(SecurityGuard.isAllowedToDecrypt(null))
    }

    @Test
    fun testRootTakesPrecedenceOverDevEnvironment() {
        // যদি রুট এবং এডিবি দুটোই থাকে, রুট নির্দিষ্টভাবে প্রাধান্য পাবে
        SecurityGuard.testOverrideRoot = true
        SecurityGuard.testOverrideAdb = true

        val state = SecurityGuard.checkReaderAccess(null)
        assertEquals(SecurityGuard.TamperState.RootedDevice, state)
        assertFalse(SecurityGuard.isAllowedToDecrypt(null))
    }

    @Test
    fun testShouldBlockAlwaysReturnsFalse() {
        // সম্পূর্ণ অ্যাপ কখনোই ক্র্যাশ বা বন্ধ করা হবে না; হোম ও সেটিংস স্ক্রিন সবসময় সচল থাকবে
        val resultRooted = SecurityGuard.Result(
            rooted = true,
            emulator = false,
            adb = false,
            hooking = false,
            state = SecurityGuard.TamperState.RootedDevice
        )
        assertFalse("রুট থাকা সত্ত্বেও সম্পূর্ণ অ্যাপ লক হবে না", SecurityGuard.shouldBlock(resultRooted))

        val resultDev = SecurityGuard.Result(
            rooted = false,
            emulator = true,
            adb = true,
            hooking = true,
            state = SecurityGuard.TamperState.SecurityRiskOrDevEnvironment
        )
        assertFalse("ডিভ পরিবেশে সম্পূর্ণ অ্যাপ লক হবে না", SecurityGuard.shouldBlock(resultDev))
    }

    @Test
    fun testTamperStateEnumCompleteness() {
        val values = SecurityGuard.TamperState.values().map { it.name }
        assertTrue(values.contains("Clean"))
        assertTrue(values.contains("RootedDevice"))
        assertTrue(values.contains("SecurityRiskOrDevEnvironment"))
        assertEquals(3, values.size)
    }

    @Test
    fun testNoInternalTelemetryLeakageInStateNames() {
        // কোনো অভ্যন্তরীণ এক্সপ্লয়েট নাম (frida, ptrace, tracer, port ইত্যাদি) স্টেট নেমে নেই
        for (state in SecurityGuard.TamperState.values()) {
            val nameLower = state.name.lowercase()
            assertFalse(nameLower.contains("frida"))
            assertFalse(nameLower.contains("tracer"))
            assertFalse(nameLower.contains("ptrace"))
            assertFalse(nameLower.contains("xposed"))
            assertFalse(nameLower.contains("27042"))
        }
    }
}
