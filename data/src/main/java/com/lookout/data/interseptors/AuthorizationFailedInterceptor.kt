package com.lookout.data.interseptors

import android.util.Log
import com.lookout.data.auth.AppAuth
import com.lookout.data.interseptors.AuthorizationInterceptor.Companion.AUTHORIZATION_HEADER
import com.lookout.data.local.Preferences
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthorizationFailedInterceptor @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val preferences: Preferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequestTimestamp = System.currentTimeMillis()
        val originalResponse = chain.proceed(chain.request())
        return originalResponse
            .takeIf { it.code != UNAUTHORIZED_CODE }
            ?: handleUnauthorizedResponse(chain, originalResponse, originalRequestTimestamp)
    }

    private fun handleUnauthorizedResponse(
        chain: Interceptor.Chain,
        originalResponse: Response,
        requestTimestamp: Long
    ): Response {
        val latch = getLatch()
        return when {
            latch != null && latch.count > 0 -> {
                handleTokenIsUpdating(chain, latch, requestTimestamp) ?: originalResponse
            }
            tokenUpdateTime > requestTimestamp -> {
                updateTokenAndProceedChain(chain)
            }
            else -> {
                handleTokenNeedRefresh(chain) ?: originalResponse
            }
        }
    }

    private fun handleTokenIsUpdating(
        chain: Interceptor.Chain,
        latch: CountDownLatch,
        requestTimestamp: Long
    ): Response? {
        return if (latch.await(
                REQUEST_TIMEOUT,
                TimeUnit.SECONDS
            ) && tokenUpdateTime > requestTimestamp
        ) {
            updateTokenAndProceedChain(chain)
        } else {
            null
        }
    }

    private fun handleTokenNeedRefresh(
        chain: Interceptor.Chain
    ): Response? {
        return if (refreshToken()) {
            updateTokenAndProceedChain(chain)
        } else {
            null
        }
    }

    private fun refreshToken(): Boolean {
        initLatch()

        val tokenRefreshed = runBlocking {
            runCatching {
                val refreshRequest =
                    AppAuth.getRefreshTokenRequest(preferences.refreshToken.orEmpty())
                AppAuth.performTokenRequestSuspend(authorizationService, refreshRequest)
            }
                .getOrNull()
                ?.let {
                    preferences.accessToken = it.accessToken
                    preferences.refreshToken = it.refreshToken
                    preferences.idToken = it.idToken
                    true
                } ?: false
        }

        if (tokenRefreshed) {
            tokenUpdateTime = System.currentTimeMillis()
        } else {
            Log.e("AUTH_ERROR", "logout after token refresh failure")
        }
        getLatch()?.countDown()
        return tokenRefreshed
    }

    private fun updateTokenAndProceedChain(
        chain: Interceptor.Chain
    ): Response {
        val newRequest = updateOriginalCallWithNewToken(chain.request())
        return chain.proceed(newRequest)
    }

    private fun updateOriginalCallWithNewToken(
        request: Request
    ): Request = preferences.accessToken?.let { newAccessToken ->
        request
            .newBuilder()
            .header(AUTHORIZATION_HEADER, newAccessToken)
            .build()
    } ?: request

    companion object {

        private const val UNAUTHORIZED_CODE = 401
        private const val REQUEST_TIMEOUT = 30L

        private var countDownLatch: CountDownLatch? = null

        @Volatile
        private var tokenUpdateTime: Long = 0L

        @Synchronized
        fun initLatch() {
            countDownLatch = CountDownLatch(1)
        }

        @Synchronized
        fun getLatch() = countDownLatch
    }
}