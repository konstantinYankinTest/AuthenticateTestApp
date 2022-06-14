package com.lookout.authenticatetestapp.presentation.main.views

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.lookout.data.auth.AppAuth


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AndroidWebView(
    url: String,
    onAuthCodeReceived: (String) -> Unit = {},
    onWebLogoutCompleted: () -> Unit = {}
) {
    val visibility = remember { mutableStateOf(true) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (visibility.value) {
            CircularProgressIndicator(
                color = Color.Red,
                strokeWidth = 4.dp
            )
        }
        AndroidView(factory = {
            WebView(it).apply {
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) {
                        if (request?.isForMainFrame == true) {
                            //TODO Error Impl
                        }

                        super.onReceivedHttpError(view, request, errorResponse)
                    }

                    override fun onPageStarted(
                        view: WebView, url: String,
                        favicon: Bitmap?
                    ) {
                        visibility.value = true
                    }

                    override fun onPageFinished(
                        view: WebView, url: String
                    ) {
                        visibility.value = false
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val requestUrl = request?.url.toString()
                        if (requestUrl.startsWith(AppAuth.AuthConfig.CALLBACK_URL)) {
                            onAuthCodeReceived(requestUrl)
                            return true
                        }
                        if (!requestUrl.contains("login", true)) {
                            onWebLogoutCompleted()
                        }
                        return false
                    }
                }
                loadUrl(url)
            }
        })
    }
}