package com.allcryptotokens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allcryptotokens.ui.ads.AdSupportedContent
import com.allcryptotokens.ui.theme.AllCryptoTokensTheme

class MainActivity : ComponentActivity() {
    private val billingManager: BillingManager by lazy { (application as App).billingManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AllCryptoTokensTheme {
                AdSupportedContent {
                    val billingState by billingManager.state.collectAsStateWithLifecycle()
                    var showRemoveAds by rememberSaveable { mutableStateOf(false) }

                    if (showRemoveAds) {
                        RemoveAdsScreen(
                            state = billingState,
                            onBack = { showRemoveAds = false },
                            onRestore = billingManager::restorePurchases,
                            onPurchase = { billingManager.launchPurchase(this, it) }
                        )
                    } else {
                        TokenListScreen(
                            isAdFree = billingState.isAdFree,
                            onOpenToken = { cgId ->
                                startActivity(
                                    Intent(this, TokenDetailActivity::class.java).apply {
                                        putExtra("cgId", cgId)
                                    }
                                )
                            },
                            onOpenRemoveAds = { showRemoveAds = true },
                            onOpenAbout = {
                                startActivity(Intent(this, AboutActivity::class.java))
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        billingManager.refreshPurchasesSilently()
    }
}
