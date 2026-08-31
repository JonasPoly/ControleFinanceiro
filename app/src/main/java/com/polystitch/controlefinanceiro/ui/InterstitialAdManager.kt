package com.polystitch.controlefinanceiro.ui

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var isloading = false

    // ID de teste oficial do Google para Intersticial
    private val adUnitId = "ca-app-pub-3940256099942544/1033173712"

    fun loadAd() {
        if (interstitialAd == null && !isloading) {
            isloading = true
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        interstitialAd = null
                        isloading = false
                        Log.d("AdMob", "Erro ao carregar intersticial: ${adError.message}")
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isloading = false
                        Log.d("AdMob", "Intersticial carregado com sucesso.")
                    }
                }
            )
        }
    }

    fun showAd(activity: android.app.Activity, onAdDismissed: () -> Unit) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadAd() // Carrega o próximo anúncio
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    onAdDismissed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            // Se o anúncio não carregou a tempo, executa a ação normalmente
            loadAd()
            onAdDismissed()
        }
    }
}