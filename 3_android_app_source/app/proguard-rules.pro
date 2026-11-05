# Data, Model & Reader Classes (প্রিজার্ভ করা আবশ্যক যাতে JSON ও রিডার নিখুঁত কাজ করে)
-keep class com.karianaquran.reader.data.** { *; }
-keep class com.karianaquran.reader.data.model.** { *; }
-keep class com.karianaquran.reader.data.crypto.** { *; }
-keepclassmembers class com.karianaquran.reader.data.model.** { *; }

# R8 অপটিমাইজেশন ও অবফাসকেশন রুলস
-allowaccessmodification
-repackageclasses 'com.karianaquran.reader.sec'

-keepattributes Signature,*Annotation*
-keep class com.google.firebase.** { *; }
-dontwarn org.slf4j.**
# WebView ও JavaScript ইন্টারফেস (কুরআন রিডার ও শব্দ স্পর্শ ইন্টারঅ্যাকশন)
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.karianaquran.reader.ui.reader.AndroidMushafBridge { *; }
-keep class com.karianaquran.reader.ui.reader.MushafJsBridge { *; }
-keep class com.karianaquran.reader.ui.reader.** { *; }
-keep class androidx.webkit.** { *; }

# Crashlytics স্ট্যাক ট্রেস
-keep public class * extends java.lang.Exception
