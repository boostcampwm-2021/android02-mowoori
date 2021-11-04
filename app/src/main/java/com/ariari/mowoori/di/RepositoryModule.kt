package com.ariari.mowoori.di

import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.data.repository.IntroRepositoryImpl
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesIntroRepository(databaseReference: DatabaseReference): IntroRepository =
        IntroRepositoryImpl(databaseReference)
}
