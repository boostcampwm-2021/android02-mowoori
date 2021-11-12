package com.ariari.mowoori.di

import android.content.Context
import android.content.SharedPreferences
import com.ariari.mowoori.data.preference.MoWooriPreference
import com.ariari.mowoori.data.preference.MoWooriPreferenceImpl
import dagger.Binds
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
        context.getSharedPreferences(context.applicationInfo.packageName, Context.MODE_PRIVATE)

    @Provides
    fun provideMowooriPreference(preferences: SharedPreferences): MoWooriPreference =
        MoWooriPreferenceImpl(preferences)
}
