package com.x.demo

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afanty.ads.*
import com.afanty.ads.base.FullScreenAdController
import com.afanty.ads.base.IAdObserver
import com.afanty.ads.core.RTBAd
import com.afanty.ads.render.AdViewRenderHelper
import com.afanty.ads.view.PlayerView
import com.afanty.api.AftAdSdk
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private var TAG: String = "AFT.MainActivity"
    private var mContainer: FrameLayout? = null
    private var mNativeContainer: FrameLayout? = null
    private var itlAd: RTBInterstitialAd? = null

    private var rwdAd: RTBRewardAd? = null
    private var bannerAd_50: RTBBannerAd? = null
    private var bannerAd_250: RTBBannerAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContainer = banner_container as FrameLayout
        mNativeContainer = native_render_container as FrameLayout


        initNative()

        initInterstitial()

        initRewarded()

        initBanner()

        initListener()

        initGDPR()

        initConfig();
    }

    private fun initConfig(){
        cloud_act.setOnClickListener {
            ConfigAct.start(this)
        }
    }

    private fun initNative() {
        btn_native.setOnClickListener {
            Log.i(TAG, "#requestNativeAd start")
            val nativeAd = RTBNativeAd(this, test_pid.text?.toString())
            nativeAd.setAdLoadListener(object :
                IAdObserver.AdLoadCallback {

                override fun onAdLoaded(rtbAd: RTBAd?) {
                    rtbAd as RTBNativeAd;
                    Log.i(TAG, "#requestNativeAd AdLoaded")
                    renderNativeAd(rtbAd)
                }

                override fun onAdLoadError(adError: AdError?) {
                    Log.i(TAG, "#requestNativeAd onAdLoadError $adError")
                }

            })
            val observer: IAdObserver.AdEventListener =
                object : IAdObserver.AdEventListener {
                    override fun onAdImpressionError(error: AdError) {
                        Log.i(TAG, "#onNativeAdImpressionError $error")
                    }

                    override fun onAdImpression() {
                        Log.i(TAG, "onNativeAdImpression")
                    }

                    override fun onAdClicked() {
                        Log.i(TAG, "#onNativeAdClicked")
                    }

                    override fun onAdCompleted() {
                        Log.i(TAG, "#onNativeAdCompleted")
                    }

                    override fun onAdClosed(hasRewarded: Boolean) {
                        Log.i(TAG, "#onNativeAdClosed hasRewarded = $hasRewarded")
                    }
                }
            nativeAd.setAdActionListener(observer)
            nativeAd.load()
        }
    }

    private fun initInterstitial() {
        btn_request_itl.setOnClickListener {
            Log.i(TAG, "#requestInterstitialAd start")
            itlAd = RTBInterstitialAd(this, test_pid.text?.toString())
            requestInterstitialAd()//插屏
        }
        btn_show_itl.setOnClickListener {
            showInterstitialAd()
        }
    }

    private fun initRewarded() {
        btn_request_rwd.setOnClickListener {
            rwdAd = RTBRewardAd(this, test_pid.text?.toString())
            Log.i(TAG, "#requestRewardAd start")
            requestRewardedAd()// 激励
        }
        btn_show_rwd.setOnClickListener {
            showRewardedAd()
        }
    }

    private fun initBanner() {
        btn_request_banner.setOnClickListener {
            Log.i(TAG, "#requestBannerAd start")
            bannerAd_50 = RTBBannerAd(this, test_pid.text?.toString())
            requestBannerAd50()
        }
        btn_show_banner.setOnClickListener {
            showBannerAd()
        }

        bannerAd_250 = RTBBannerAd(this, test_pid.text?.toString())
        btn_request_banner250.setOnClickListener {
            Log.i(TAG, "#requestBannerAd start")
            bannerAd_250 = RTBBannerAd(this, test_pid.text?.toString())
            requestBannerAd250()
        }
        btn_show_banner250.setOnClickListener {
            showBannerAd250()
        }
    }

    private fun initListener() {
    }

    private fun initGDPR() {
        tv_gdpr.text = getGDPRDesc()
    }

    private fun getGDPRDesc() =
        String.format(
            getString(R.string.title_gdpr),
            (if (AftAdSdk.canCollectUserInfo()) "ON" else "OFF")
        )

    private fun renderNativeAd(nativeAd: RTBNativeAd) {
        val contentView =
            LayoutInflater.from(this).inflate(R.layout.ad_native_layout, mNativeContainer, false)
        val titleText = contentView.findViewById<TextView>(R.id.native_title)
        val contentText = contentView.findViewById<TextView>(R.id.native_text)
        val buttonView = contentView.findViewById<TextView>(R.id.native_button)
        val iconImage = contentView.findViewById<ImageView>(R.id.native_icon_image)
        val mediaLayout: PlayerView = contentView.findViewById(R.id.native_main_image)

        //text
        titleText.text = nativeAd.title
        contentText.text = nativeAd.content
        buttonView.text = nativeAd.callToAction
        //icon
        AdViewRenderHelper.loadImage(iconImage.context, nativeAd.iconUrl, iconImage)
        //media view
        mediaLayout.setVideoLifecycleCallbacks(object : IAdObserver.VideoLifecycleCallbacks {
            override fun onVideoStart() {
                Log.i(TAG, "#onVideoStart")
            }

            override fun onVideoPlay() {
                Log.i(TAG, "#onVideoPlay")
            }

            override fun onVideoPause() {
                Log.i(TAG, "#onVideoPause")
            }

            override fun onVideoEnd() {
                Log.i(TAG, "#nVideoEnd")
            }

            override fun onVideoMute(isMuted: Boolean) {
                Log.i(TAG, "#onVideoMute")
            }

        })
        mediaLayout.loadMediaView(
            nativeAd.nativeAd, VideoOptions.Builder()
                .setStartMuted(false)
                .setSoundGravity(Gravity.END)
                .build()
        )
        //click list
        val clickViews: MutableList<View> = ArrayList()
        clickViews.add(titleText)
        clickViews.add(contentText)
        clickViews.add(buttonView)
        clickViews.add(iconImage)
        clickViews.add(mediaLayout)
        //prepare
        nativeAd.prepare(contentView, clickViews, null)
        mNativeContainer?.removeAllViews()
        mNativeContainer?.addView(contentView)
    }

    private fun requestInterstitialAd() {
        itlAd?.setAdLoadListener(object :
            IAdObserver.AdLoadCallback {

            override fun onAdLoaded(rtbAd: RTBAd?) {
                Log.i(TAG, "#requestInterstitialAd AdLoaded")
                if (rtbAd is RTBInterstitialAd) {
                    Toast.makeText(applicationContext, "Interstitial Loaded", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onAdLoadError(adError: AdError?) {
                Log.i(TAG, "#requestInterstitialAd onAdLoadError $adError")
            }

        })
        itlAd?.setAdActionListener(object :
            IAdObserver.AdEventListener {
            override fun onAdImpressionError(error: AdError?) {
                Log.i(TAG, "#onAdImpressionError $error")
            }

            override fun onAdImpression() {
                Log.i(TAG, "onInterstitialAdImpression")
            }

            override fun onAdClicked() {
                Log.i(TAG, "#onInterstitialAdClicked")
            }

            override fun onAdCompleted() {
                Log.i(TAG, "#onInterstitialAdCompleted")
            }

            override fun onAdClosed(hasRewarded: Boolean) {
                Log.i(TAG, "#onInterstitialAdClosed hasRewarded = $hasRewarded")
            }
        })
        itlAd?.load()
        Log.i(TAG, "#itlAd end")
    }

    private fun showInterstitialAd() {
        itlAd?.let {
            if (it.isAdReady) {
                it.show()
            } else {
                Toast.makeText(applicationContext, "Ad not ready, will reload", Toast.LENGTH_SHORT)
                    .show()
                it.load()
            }
        }
    }

    private fun requestRewardedAd() {
        rwdAd?.setAdLoadListener(object :
            IAdObserver.AdLoadCallback {

            override fun onAdLoaded(rtbAd: RTBAd) {
                Log.i(TAG, "#requestRewardedAd onRewardedVideoAdLoaded")
                Toast.makeText(applicationContext, "rewardAd Loaded", Toast.LENGTH_SHORT)
                    .show()

                if (rtbAd is FullScreenAdController) {
                    Toast.makeText(applicationContext, "Rewarded Loaded", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAdLoadError(adError: AdError?) {
                Log.i(TAG, "#requestRewardedAd onAdLoadError $adError")
            }

        })
        rwdAd?.setAdActionListener(object :
            IAdObserver.AdEventListener {
            override fun onAdImpressionError(error: AdError?) {
                Log.i(TAG, "#onRewardedAdImpressionError $error")
            }

            override fun onAdImpression() {
                Log.i(TAG, "onRewardedAdImpression")
            }

            override fun onAdClicked() {
                Log.i(TAG, "#onRewardedAdClicked")
            }

            override fun onAdCompleted() {
                Log.i(TAG, "#onRewardedAdCompleted")
            }

            override fun onAdClosed(hasRewarded: Boolean) {
                Log.i(TAG, "#onRewardedAdClosed hasRewarded = $hasRewarded")
            }
        })
        rwdAd?.load()
        Log.i(TAG, "#rewarded end")
    }

    private fun showRewardedAd() {
        if (rwdAd == null) {
            requestRewardedAd()
            return
        }
        if (rwdAd!!.isAdReady) {
            rwdAd?.show()
        } else {
            Toast.makeText(applicationContext, "Ad not ready, will reload", Toast.LENGTH_SHORT)
                .show()
            rwdAd?.load()
        }
    }

    private fun requestBannerAd50() {
        bannerAd_50?.adSize = AdSize.BANNER
        bannerAd_50?.setAdLoadListener(getBannerAdLoadListener(true))
        bannerAd_50?.load()
    }

    private fun showBannerAd() {
        bannerAd_50?.setAdActionListener(object :
            IAdObserver.AdEventListener {
            override fun onAdImpressionError(error: AdError?) {
                Log.i(TAG, "#onBannerAdImpressionError $error")
            }

            override fun onAdImpression() {
                Log.i(TAG, "onBannerAdImpression")
            }

            override fun onAdClicked() {
                Log.i(TAG, "#onBannerAdClicked")
            }

            override fun onAdCompleted() {
                Log.i(TAG, "#onBannerAdCompleted")
            }

            override fun onAdClosed(hasRewarded: Boolean) {
                Log.i(TAG, "#onBannerAdClosed hasRewarded = $hasRewarded")
            }
        })
        if (bannerAd_50 != null && bannerAd_50!!.isAdReady && mContainer != null) {
            mContainer!!.removeAllViews()
            mContainer!!.addView(bannerAd_50!!.adView)
        } else {
            Toast.makeText(applicationContext, "Ad not ready, will reload", Toast.LENGTH_SHORT)
                .show()
            requestBannerAd50()
        }
    }

    private fun requestBannerAd250() {
        bannerAd_250?.adSize = AdSize.MEDIUM_RECTANGLE

        bannerAd_250?.setAdLoadListener(getBannerAdLoadListener(false))
        bannerAd_250?.load()
    }

    private fun showBannerAd250() {
        bannerAd_250?.setAdActionListener(object :
            IAdObserver.AdEventListener {
            override fun onAdImpressionError(error: AdError?) {
                Log.i(TAG, "#onBannerAdImpressionError $error")
            }

            override fun onAdImpression() {
                Log.i(TAG, "onBannerAdImpression")
            }

            override fun onAdClicked() {
                Log.i(TAG, "#onBannerAdClicked")
            }

            override fun onAdCompleted() {
                Log.i(TAG, "#onBannerAdCompleted")
            }

            override fun onAdClosed(hasRewarded: Boolean) {
                Log.i(TAG, "#onBannerAdClosed hasRewarded = $hasRewarded")
            }
        })
        if (bannerAd_250 != null && bannerAd_250!!.isAdReady && mContainer != null) {
            mContainer!!.removeAllViews()
            mContainer!!.addView(bannerAd_250!!.adView)
        } else {
            Toast.makeText(applicationContext, "Ad not ready, will reload", Toast.LENGTH_SHORT)
                .show()
            requestBannerAd250()
        }

    }

    private fun getBannerAdLoadListener(needShow: Boolean) = object :
        IAdObserver.AdLoadCallback {

        override fun onAdLoaded(rtbAd: RTBAd?) {
            if (rtbAd!!.isAdReady) {
                Toast.makeText(applicationContext, "Banner Loaded", Toast.LENGTH_SHORT).show()
            }
            if (needShow)
                showBannerAd()
            Log.i(TAG, "#requestBannerAd onBannerLoaded")
        }

        override fun onAdLoadError(adError: AdError?) {
            Log.i(TAG, "##requestBannerAd onAdLoadError $adError ")
        }
    }
}