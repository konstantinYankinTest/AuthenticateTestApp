package com.lookout.authenticatetestapp

import net.openid.appauth.ResponseTypeValues

object AuthConfig {
    const val AUTH_URI = "https://github.com/login/oauth/authorize"
    const val TOKEN_URI = "https://github.com/login/oauth/access_token"
    const val END_SESSION_URI = "https://github.com/logout"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE
    const val SCOPE = "user,repo"
    const val CLIENT_ID = "356d226097b57bf27c50"
    const val CLIENT_SECRET = "7f42da12bfa134c948d35246f271f91795d80f87"
    const val CALLBACK_URL = "authenticatetestapp://lookout.callback"
    const val LOGOUT_CALLBACK_URL = "authenticatetestapp://lookout.logout_callback"
}