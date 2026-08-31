package com.polystitch.controlefinanceiro.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.ui.platform.LocalContext

@Composable
fun AdMobBanner(modifier: Modifier = Modifier) {
    // ATENÇÃO: Durante os testes use o ID de teste do Google.
    // Só substitua pelo seu ID real ("ca-app-pub-...") quando for publicar na Play Store para evitar banimento por cliques acidentais.
    val adUnitId = "ca-app-pub-3940256099942544/6300978111" // Este é o ID oficial de TESTE do Google para Banners
    val context = LocalContext.current

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}