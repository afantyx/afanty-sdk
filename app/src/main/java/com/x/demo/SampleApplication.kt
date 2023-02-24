package com.x.demo

import android.os.Build
import androidx.multidex.MultiDexApplication
import com.afanty.api.AftAdSdk
import com.afanty.request.CustomBidRequest

class SampleApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        SettingConfig.init(this)
        initAdSdk()
    }

    private fun initAdSdk() {
        val appInfo = CustomBidRequest.App()
            .appendAppName("demos")
            .appendPkg("com.x.demo")
            .appendPublisher("x")
            .appendVerCode(1)
            .appendVerName("1.0")
        val deviceInfo = CustomBidRequest.DeviceInfo()
            .appendUA("Mozilla\\/5.0 (Linux; Android 11; Pixel 4a Build\\/RQ3A.210605.005; wv) AppleWebKit\\/537.36 (KHTML, like Gecko) Version\\/4.0 Chrome\\/106.0.5249.126 Mobile ")
            .appendABI("armeabi-v7a")
            .appendCPUBit(getDeviceByte())
            .appendGaid("b6c5cd19-edf6-4ada-9fee-c9ff3d742fa9")
            .appendH(1920)
            .appendW(1080)
            .appendImei("test-imei")
            .appendLanguage("en")
            .appendMacAdress("test-mp")
            .appendPPI(660)

        val adSettings = AftAdSdk.getDefaultAdSettingsBuilder()
            .setApp(appInfo)
            .setDeviceInfo(deviceInfo)
            .setBidHost("http://your-bid-host.com")
            .build()
        AftAdSdk.init(this, adSettings)
        AftAdSdk.setGDPRStatus(this, true)
    }


    private fun getDeviceByte():String {
        if (Build.CPU_ABI.contains("64")) {
            return "64";
        } else {
            return "32";
        }
    }
}