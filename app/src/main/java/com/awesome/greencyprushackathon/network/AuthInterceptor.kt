package com.awesome.greencyprushackathon.network

import com.awesome.greencyprushackathon.features.carbon_footprint.domain.DataStoreRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Exclude the `authorize` endpoint
        if (request.url.encodedPath.contains("auth", ignoreCase = true)) {
            return chain.proceed(request)
        }

        // Add the Authorization header if a session exists
        val session = runBlocking { dataStoreRepository.getSession().firstOrNull() }
        val newRequest = if (session != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $session")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}