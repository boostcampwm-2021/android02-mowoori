package com.ariari.mowoori.di

import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.data.repository.IntroRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.Binds
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
    fun providesIntroRepository(
        databaseReference: DatabaseReference,
        storageReference: StorageReference,
        firebaseAuth: FirebaseAuth
    ): IntroRepository =
        IntroRepositoryImpl(databaseReference, storageReference,firebaseAuth)
}
