# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Kotlin
-dontwarn kotlin.**

# OrmLite
-dontwarn com.j256.**
-keep class com.j256.**
-keepclassmembers class com.j256.** {*;}
-keep enum com.j256.**
-keepclassmembers enum com.j256.** {*;}
-keep interface com.j256.**
-keepclassmembers interface com.j256.** {*;}
-keepattributes *Annotation*
-keep class com.stedi.shoppinglist.model.repository.DatabaseShoppingRepository
-keepclassmembers com.stedi.shoppinglist.model.repository.DatabaseShoppingRepository
-keep class com.stedi.shoppinglist.model.ShoppingItem
-keepclassmembers com.stedi.shoppinglist.model.ShoppingItem
-keep class com.stedi.shoppinglist.model.ShoppingList
-keepclassmembers com.stedi.shoppinglist.model.ShoppingList

# RxJava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent

# Otto
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}