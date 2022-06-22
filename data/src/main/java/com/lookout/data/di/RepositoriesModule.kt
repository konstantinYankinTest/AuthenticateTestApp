package com.lookout.data.di

import com.lookout.data.repositories.AuthRepositoryImpl
import com.lookout.data.repositories.UserRepositoryImpl
import com.lookout.domain.repositories.AuthRepository
import com.lookout.domain.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoriesModule {

    @Binds
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}