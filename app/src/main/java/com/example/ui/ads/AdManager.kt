package com.example.ui.ads

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed interface AdState {
    data object Loading : AdState
    data class Loaded(val nativeAd: NativeAd) : AdState
    data class Error(val message: String) : AdState
}

const val NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

object AdManager {

    private val _adState = MutableStateFlow<AdState>(AdState.Loading)
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    private var isInitialized = false

    suspend fun initialize(context: Context) {
        if (isInitialized) return
        MobileAds.initialize(context) { isInitialized = true }
    }

    suspend fun loadNativeAd(context: Context, adUnitId: String) {
        _adState.value = AdState.Loading

        val previousAd = (_adState.value as? AdState.Loaded)?.nativeAd
        previousAd?.destroy()

        val ad = suspendCancellableCoroutine<NativeAd?> { continuation ->
            val adLoader = AdLoader.Builder(context, adUnitId)
                .forNativeAd { nativeAd ->
                    continuation.resume(nativeAd)
                }
                .withAdListener(object : com.google.android.gms.ads.AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        continuation.resume(null)
                    }
                })
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        _adState.value = if (ad != null) {
            AdState.Loaded(ad)
        } else {
            AdState.Error("Failed to load ad")
        }
    }

    fun destroyAd() {
        val ad = (_adState.value as? AdState.Loaded)?.nativeAd
        ad?.destroy()
        _adState.value = AdState.Loading
    }
}
