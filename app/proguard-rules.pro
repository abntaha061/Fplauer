# ProGuard rules for FinalPlayer

# Preserve line numbers and source file attributes for stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve Annotations & Signatures
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Jetpack Compose
-keepclassmembers class * extends androidx.compose.ui.node.LayoutNode { *; }
-dontwarn androidx.compose.**

# Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Data Classes & Domain Models (Broad Protection)
-keep class com.finalplayer.app.domain.model.** { *; }
-keepclassmembers class com.finalplayer.app.domain.model.** { *; }
-keep class **.*Model { *; }
-keep class **.*Entity { *; }
-keep class **.*Dto { *; }
-keep class **.*Response { *; }

# ViewModels (Crucial for DI and Navigation)
-keep class * extends androidx.lifecycle.ViewModel { *; }

# ----------------------------------------------------------------------------
# CRUCIAL: Protect Parcelable and Serializable classes for Navigation
# ----------------------------------------------------------------------------
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Room Database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Koin Dependency Injection
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Media & MPV
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**
-keep class is.xyz.mpv.** { *; }
-dontwarn is.xyz.mpv.**

# SMBJ & Apache Commons Net
-keep class com.hierynomus.** { *; }
-dontwarn com.hierynomus.**
-keep class org.apache.commons.net.** { *; }
-dontwarn org.apache.commons.net.**

# ----------------------------------------------------------------------------
# Fix for MBassador Event Bus (Missing javax.el classes on Android)
# ----------------------------------------------------------------------------
-dontwarn javax.el.**
-dontwarn java.beans.**
-keep class javax.el.** { *; }

# Keep MBassador classes safe from shrinking if heavily relying on reflection
-keep class net.engio.mbassy.** { *; }
-dontwarn net.engio.mbassy.**
