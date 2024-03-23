package com.example.spygamers.services

import com.example.spygamers.services.authentication.AuthenticationService
import com.example.spygamers.services.friendship.FriendshipService
import com.example.spygamers.services.profilechanger.ProfileChangerService
import com.example.spygamers.services.profilesearch.ProfileSearchService
import com.example.spygamers.services.recommendations.RecommendationService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceFactory() {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://spygamers.servehttp.com:44414/app-api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Deprecated("Use createService instead.")
    internal fun createAuthenticationService(): AuthenticationService {
        return retrofit.create(AuthenticationService::class.java)
    }

    @Deprecated("Use createService instead.")
    internal fun createFriendshipService(): FriendshipService {
        return retrofit.create(FriendshipService::class.java)
    }

    @Deprecated("Use createService instead.")
    internal fun createRecommendationService(): RecommendationService {
        return retrofit.create(RecommendationService::class.java)
    }

    @Deprecated("Use createService instead.")
    internal fun createProfileChangerService(): ProfileChangerService {
        return retrofit.create(ProfileChangerService::class.java)
    }

    internal fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}