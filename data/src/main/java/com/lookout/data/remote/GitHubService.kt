package com.lookout.data.remote

import com.lookout.data.local.Preferences
import com.lookout.data.models.GitHubUserDTO
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json as serializer
import javax.inject.Inject

class GitHubService @Inject constructor(
    private val preferences: Preferences
) {

    private val client = HttpClient{
        val token = preferences.getGitHubToken()
        defaultRequest {
            header("Authorization", "token $token")
        }

        install(Logging){
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        install(JsonFeature){
            serializer = KotlinxSerializer(serializer{
                isLenient = true
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getProfile(): GitHubUserDTO{
        return client.get("https://api.github.com/user")
    }
}