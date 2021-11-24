package com.ariari.mowoori.di

import com.ariari.mowoori.data.local.datasource.MoWooriPrefDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private const val FCM_URL = "https://fcm.googleapis.com"

    @Provides
    @Singleton
    @Named("FcmRetrofit")
    fun providesFcmInterceptor(moWooriPrefDataSource: MoWooriPrefDataSource) =
        Interceptor { chain ->
            with(chain) {
                proceed(
                    request()
                        .newBuilder()
                        .addHeader("Authorization", moWooriPrefDataSource.getFcmServerKey())
                        .build()
                )
            }
        }

    @Provides
    @Singleton
    @Named("FcmRetrofit")
    fun providesFcmOkHttpClient(@Named("FcmRetrofit") interceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()


    @Provides
    @Singleton
    @Named("FcmRetrofit")
    fun providesFcmRetrofit(@Named("FcmRetrofit") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(FCM_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}
