package com.lookout.authenticatetestapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService

@Module
@InstallIn(SingletonComponent::class)
object AuthServiceModule {

    @Provides
    fun providesAuthService(
        @ApplicationContext context: Context
    ): AuthorizationService = AuthorizationService(context)
}