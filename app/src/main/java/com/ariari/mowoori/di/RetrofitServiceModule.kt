package com.ariari.mowoori.di

import com.ariari.mowoori.data.remote.service.FcmService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitServiceModule {
    @Provides
    @Singleton
    fun providesFcmService(@Named("FcmRetrofit") retrofit: Retrofit): FcmService =
        retrofit.create(FcmService::class.java)
}
