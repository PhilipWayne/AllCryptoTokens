package com.allcryptotokens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenDetailScreen(
    cgId: String
) {
    val ctx = LocalContext.current
    val dao = remember { AppDatabase.get(ctx).tokenDao() }

    val token by dao.observeToken(cgId).collectAsState(initial = null)
    val scroll = rememberScrollState()

    val title = token?.let { "${it.name} (${it.symbol.uppercase()})" } ?: cgId

    val desc = buildString {
        append(token?.description?.takeIf { it.isNotBlank() } ?: "No description yet.")
        append("\n\nDEBUG youtubeId=")
        append(token?.youtubeId ?: "NULL")
        append("\nDEBUG cgId=")
        append(token?.cgId ?: "NULL")
    }

    val youtubeId = token?.youtubeId?.takeIf { it.isNotBlank() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = token?.imageUrl?.takeIf { it.isNotBlank() },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (youtubeId != null) {
                YoutubeEmbedPlayer(
                    videoId = youtubeId,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(text = desc)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun YoutubeEmbedPlayer(
    videoId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewRef: WebView? = remember { null }

    val html = remember(videoId) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta
                name="viewport"
                content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"
            />
            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    background: #000000;
                    width: 100%;
                    height: 100%;
                    overflow: hidden;
                }
                .wrap {
                    position: relative;
                    width: 100%;
                    height: 100%;
                }
                iframe {
                    position: absolute;
                    inset: 0;
                    width: 100%;
                    height: 100%;
                    border: 0;
                }
            </style>
        </head>
        <body>
            <div class="wrap">
                <iframe
                    src="https://www.youtube.com/embed/$videoId?autoplay=0&modestbranding=1&rel=0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    allowfullscreen
                ></iframe>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(16.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = {
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()

                    loadDataWithBaseURL(
                        "https://www.youtube.com",
                        html,
                        "text/html",
                        "utf-8",
                        null
                    )

                    webViewRef = this
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL(
                    "https://www.youtube.com",
                    html,
                    "text/html",
                    "utf-8",
                    null
                )
                webViewRef = webView
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.apply {
                stopLoading()
                loadUrl("about:blank")
                clearHistory()
                removeAllViews()
                destroy()
            }
            webViewRef = null
        }
    }
}