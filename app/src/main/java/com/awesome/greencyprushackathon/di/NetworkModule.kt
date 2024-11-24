package com.awesome.greencyprushackathon.di

import com.awesome.greencyprushackathon.features.carbon_footprint.domain.DataStoreRepository
import com.awesome.greencyprushackathon.network.ApiService
import com.awesome.greencyprushackathon.network.AuthInterceptor
import com.awesome.greencyprushackathon.network.MockNetworkRepository
import com.awesome.greencyprushackathon.network.NetworkRepository
import com.awesome.greencyprushackathon.network.RemoteNetworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(dataStoreRepository: DataStoreRepository): AuthInterceptor =
        AuthInterceptor(dataStoreRepository)

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://greency.eu-central-1.elasticbeanstalk.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    @RealNetworkRepository
    fun provideRemoteNetworkRepository(apiService: ApiService, dataStoreRepository: DataStoreRepository): NetworkRepository {
        return RemoteNetworkRepository(apiService, dataStoreRepository)
    }

    // Local Repository
    @Provides
    @Singleton
    @LocalNetworkRepository
    fun provideLocalNetworkRepository(dataStoreRepository: DataStoreRepository): NetworkRepository {
        return MockNetworkRepository(dataStoreRepository)
    }
}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RealNetworkRepository

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalNetworkRepository
