# Basic
## For ActionType
-keep class com.afanty.internal.action.type.** { public *; }
## For RoomDatabase
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Afanty
-keepclassmembers class * extends androidx.work.Worker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}
## For Ad Load
-keep class * extends com.afanty.ads.base.RTBBaseAd { *; }

# ActionSDK
## For ActionType
-keep class com.afanty.internal.action.type.** { public *; }
-keep class aft.** {  *; }
-keep class com.afanty.** {  *; }
-keep class com.fort.** {  *; }
-dontwarn okio.**

-keep class okio.**{*;}

-keep interface okio.**{*;}

#OkHttp3

-dontwarn okhttp3.**

-keep class okhttp3.**{*;}

-keep interface okhttp3.**{*;}
-dontwarn module-info
