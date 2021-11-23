package com.ariari.mowoori.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private const val FCM_URL = "https://fcm.googleapis.com"

    @Provides
    @Singleton
    @Named("FcmRetrofit")
    fun providesFcmRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(FCM_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}
