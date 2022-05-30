package com.lookout.data.interseptors

import com.lookout.data.local.Preferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    private val preferences: Preferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request()
            .addTokenHeader()
            .let { chain.proceed(it) }
    }

    private fun Request.addTokenHeader(): Request {
        return newBuilder()
            .apply {
                val token = preferences.accessToken
                if (token != null) {
                    header(AUTHORIZATION_HEADER, token.withBearer())
                }
            }
            .build()
    }

    private fun String.withBearer() = "Bearer $this"

    companion object{

        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
