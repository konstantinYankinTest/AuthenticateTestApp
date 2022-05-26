package com.lookout.data.di

import com.lookout.data.repositories.GitHubRepositoryImpl
import com.lookout.domain.repositories.GithubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoriesModule {

    @Binds
    fun bindGithubRepository(impl: GitHubRepositoryImpl): GithubRepository
}