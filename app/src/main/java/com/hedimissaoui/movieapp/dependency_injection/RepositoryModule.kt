package com.hedimissaoui.movieapp.dependency_injection

import com.hedimissaoui.movieapp.network.APIService
import com.hedimissaoui.movieapp.repository.Repository
import com.hedimissaoui.movieapp.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        apiservice: APIService,
    ): Repository {
        return RepositoryImpl(apiService = apiservice)
    }

    @Singleton
    @Provides
    fun provideAPIService(): APIService {
        return Retrofit.Builder().baseUrl("https://api.themoviedb.org/")
            .client(
                OkHttpClient().newBuilder()
                    .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }

}