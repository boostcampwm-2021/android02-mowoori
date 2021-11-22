package com.ariari.mowoori.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ariari.mowoori.data.local.datasource.MoWooriPrefDataSource
import com.ariari.mowoori.data.local.datasource.MoWooriPrefDataSourceImpl
import com.ariari.mowoori.data.remote.datasource.FcmDataSource
import com.ariari.mowoori.data.remote.datasource.FcmDataSourceImpl
import com.ariari.mowoori.data.remote.service.FcmService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            context.packageName,
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    @Provides
    fun provideMowooriPrefDataSource(preferences: SharedPreferences): MoWooriPrefDataSource =
        MoWooriPrefDataSourceImpl(preferences)

    @Provides
    fun provideFcmDataSource(fcmService: FcmService): FcmDataSource =
        FcmDataSourceImpl(fcmService)
}
