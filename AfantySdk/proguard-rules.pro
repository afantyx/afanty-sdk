# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

#保留属性不被混淆
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod
#异常时，输入更详细日志
-verbose
-flattenpackagehierarchy 'aft'

-keepparameternames
-dontoptimize

# Common
-keepclassmembers enum *  { * ;}
-keep class **.R$* {*;}
-keep public class * extends android.app.Activity

## For RoomDatabase
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# AFT
## Init
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.InputMerger
# Keep all constructors on ListenableWorker, Worker (also marked with @Keep)
-keep public class * extends androidx.work.ListenableWorker {
    public <init>(...);
}
# We need to keep WorkerParameters for the ListenableWorker constructor
-keep class androidx.work.WorkerParameters

# BasicSDK
-keep class com.afanty.internal.action.type.** { public *; }

# AfantySDK
## API
-keep class com.afanty.ads.** { public *; }
-keep class com.afanty.ads.render.** { public *; }
-keep class com.afanty.api.** { public *; }
-keep class com.afanty.request.CustomBidRequest{ *; }
-keep class com.afanty.request.CustomBidRequest$**{ *; }
-keep class com.afanty.request.CustomBidRequest$*{ *; }
-keep class com.afanty.request.CustomBidRequest$App{ *; }
-keep class com.afanty.request.CustomBidRequest$DeviceInfo{ *; }
## Ad Load
-keep class * extends com.afanty.ads.base.RTBBaseAd { *; }
## Mediation
-keep class com.afanty.ads.base.** { public *; }

# ActionSDK

# EX-SDK
## For ActionType
-keep class com.afanty.internal.action.type.ActionTypeLandingPage { public *; }
## Widget
-keep class com.afanty.internal.view.CustomProgressButton { * ;}
-keep interface com.afanty.internal.view.CustomProgressButton$OnStateClickListener { * ;}
-keep class com.afanty.internal.view.CustomProgressButton$PackageChangedCallback { * ;}
-keep class com.afanty.internal.view.CustomProgressButton$Status { * ;}
## LanddingPage
-keep class com.afanty.land.widget.** { * ;}
-keep class com.afanty.land.BaseLandingPageActivity{ * ;}
