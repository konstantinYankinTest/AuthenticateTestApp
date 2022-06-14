package com.lookout.data.auth

import com.lookout.data.models.TokensModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

object AppAuth {

    private val state = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

    fun getEndSessionUrl(): String {
        return AuthConfig.END_SESSION_URI +
                "?client_id=" + AuthConfig.CLIENT_ID +
                "&scope=" + AuthConfig.SCOPE +
                "&redirect_uri=" + AuthConfig.LOGOUT_CALLBACK_URL +
                "&state=" + state
    }

    fun getAuthUrl(): String {
        return AuthConfig.AUTH_URI +
                "?client_id=" + AuthConfig.CLIENT_ID +
                "&scope=" + AuthConfig.SCOPE +
                "&redirect_uri=" + AuthConfig.CALLBACK_URL +
                "&state=" + state
    }

    suspend fun updateTokens(refreshToken: String): TokensModel {
        val grantType = "refresh_token"
        val postParams =
            "grant_type=" + grantType +
                    "&refresh_token=" + refreshToken +
                    "&client_id=" + AuthConfig.CLIENT_ID +
                    "&client_secret=" + AuthConfig.CLIENT_SECRET
        val url = URL(AuthConfig.TOKEN_URI)
        val httpsURLConnection =
            withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
        httpsURLConnection.requestMethod = "POST"
        httpsURLConnection.setRequestProperty(
            "Accept",
            "application/json"
        )
        httpsURLConnection.doInput = true
        httpsURLConnection.doOutput = true
        val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
        withContext(Dispatchers.IO) {
            outputStreamWriter.write(postParams)
            outputStreamWriter.flush()
        }
        val response = httpsURLConnection.inputStream.bufferedReader()
            .use { it.readText() }  // defaults to UTF-8
        val jsonObject = JSONTokener(response).nextValue() as JSONObject
        return TokensModel(
            accessToken = jsonObject.getString("access_token"),
            refreshToken = jsonObject.getString("refresh_token")
        )


    }


    suspend fun getTokens(
        code: String
    ): TokensModel {
        val grantType = "authorization_code"
        val postParams =
            "grant_type=" + grantType +
                    "&code=" + code +
                    "&redirect_uri=" + AuthConfig.CALLBACK_URL +
                    "&client_id=" + AuthConfig.CLIENT_ID +
                    "&client_secret=" + AuthConfig.CLIENT_SECRET
        val url = URL(AuthConfig.TOKEN_URI)
        val httpsURLConnection =
            withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
        httpsURLConnection.requestMethod = "POST"
        httpsURLConnection.setRequestProperty(
            "Accept",
            "application/json"
        )
        httpsURLConnection.doInput = true
        httpsURLConnection.doOutput = true
        val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
        withContext(Dispatchers.IO) {
            outputStreamWriter.write(postParams)
            outputStreamWriter.flush()
        }
        val response = httpsURLConnection.inputStream.bufferedReader()
            .use { it.readText() }  // defaults to UTF-8
        val jsonObject = JSONTokener(response).nextValue() as JSONObject
        return TokensModel(
            accessToken = jsonObject.getString("access_token"),
            refreshToken = jsonObject.getString("refresh_token")
        )
    }

    object AuthConfig {
        const val AUTH_URI = "https://github.com/login/oauth/authorize"
        const val TOKEN_URI = "https://github.com/login/oauth/access_token"
        const val END_SESSION_URI = "https://github.com/logout"
        const val SCOPE = "user,repo"
        const val CLIENT_ID = "Iv1.7bbef208d72b5593"
        const val CLIENT_SECRET = "e890aa3683f34d55b8944e8d355c760df07bab3b"
        const val CALLBACK_URL = "authenticatetestapp://lookout.callback"
        const val LOGOUT_CALLBACK_URL = "authenticatetestapp://lookout.logout_callback"
    }
}