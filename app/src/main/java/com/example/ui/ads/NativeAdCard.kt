package com.example.ui.ads

import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R

@Composable
fun NativeAdCard(
    modifier: Modifier = Modifier,
    adState: AdState
) {
    when (val state = adState) {
        is AdState.Loading -> {
            Card(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cargando anuncio...",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        is AdState.Loaded -> {
            AdView(ad = state.nativeAd, modifier = modifier)
        }
        is AdState.Error -> {
            // Silently fail
        }
    }
}

@Composable
private fun AdView(
    ad: com.google.android.gms.ads.nativead.NativeAd,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        factory = { ctx ->
            val nativeAdView = LayoutInflater.from(ctx)
                .inflate(R.layout.native_ad_layout, null) as com.google.android.gms.ads.nativead.NativeAdView

            nativeAdView.findViewById<TextView>(R.id.ad_headline).apply {
                text = ad.headline ?: ""
            }
            nativeAdView.findViewById<TextView>(R.id.ad_body).apply {
                text = ad.body ?: ""
                visibility = if (ad.body != null) android.view.View.VISIBLE else android.view.View.GONE
            }
            nativeAdView.findViewById<Button>(R.id.ad_call_to_action).apply {
                text = ad.callToAction ?: ""
                visibility = if (ad.callToAction != null) android.view.View.VISIBLE else android.view.View.GONE
            }
            nativeAdView.findViewById<TextView>(R.id.ad_advertiser).apply {
                text = ad.advertiser ?: ""
                visibility = if (ad.advertiser != null) android.view.View.VISIBLE else android.view.View.GONE
            }
            nativeAdView.findViewById<ImageView>(R.id.ad_icon).apply {
                val icon = ad.icon
                if (icon != null) {
                    setImageDrawable(icon.drawable)
                    visibility = android.view.View.VISIBLE
                } else {
                    visibility = android.view.View.GONE
                }
            }
            nativeAdView.findViewById<TextView>(R.id.ad_price).apply {
                text = ad.price ?: ""
                visibility = if (ad.price != null) android.view.View.VISIBLE else android.view.View.GONE
            }
            nativeAdView.findViewById<TextView>(R.id.ad_store).apply {
                text = ad.store ?: ""
                visibility = if (ad.store != null) android.view.View.VISIBLE else android.view.View.GONE
            }

            nativeAdView.setNativeAd(ad)
            nativeAdView
        }
    )
}
