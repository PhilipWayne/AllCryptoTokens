package com.allcryptotokens.ui.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allcryptotokens.App

private const val BANNER_AD_UNIT_ID = "ca-app-pub-5252933140466741/1627322069"
private val BannerContentHeight = 58.dp // 50dp AdMob banner + 4dp vertical padding on each side.

/**
 * Bottom space that scrollable screens can add INSIDE their scrolling content.
 * The app-level banner itself is an overlay, so content is allowed to scroll behind it.
 */
val LocalAdOverlayBottomPadding = staticCompositionLocalOf<Dp> { 0.dp }

/**
 * App-level ad shell. Free users get one banner overlay on every screen;
 * Remove Ads users never create the banner.
 *
 * The banner does not resize/clip the screen content. Scrollable screens can use
 * LocalAdOverlayBottomPadding as trailing content padding so their final item can
 * still be scrolled completely above the banner.
 */
@Composable
fun AdSupportedContent(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val billingManager = remember {
        (context.applicationContext as App).billingManager
    }
    val billingState by billingManager.state.collectAsStateWithLifecycle()
    val showBanner = !billingState.isLoading && !billingState.isAdFree
    val navigationBarPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
    val bottomOverlayPadding = navigationBarPadding +
        if (showBanner) BannerContentHeight else 0.dp

    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            LocalAdOverlayBottomPadding provides bottomOverlayPadding
        ) {
            content()
        }

        if (showBanner) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                tonalElevation = 2.dp
            ) {
                BannerAd(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    adUnitId = BANNER_AD_UNIT_ID
                )
            }
        }
    }
}
