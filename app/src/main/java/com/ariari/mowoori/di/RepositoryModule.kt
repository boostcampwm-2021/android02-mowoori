package com.ariari.mowoori.di

import android.content.SharedPreferences
import com.ariari.mowoori.data.local.datasource.MoWooriPrefDataSource
import com.ariari.mowoori.data.repository.GroupRepository
import com.ariari.mowoori.data.repository.GroupRepositoryImpl
import com.ariari.mowoori.data.repository.HomeRepository
import com.ariari.mowoori.data.repository.HomeRepositoryImpl
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.data.repository.IntroRepositoryImpl
import com.ariari.mowoori.data.repository.MembersRepository
import com.ariari.mowoori.data.repository.MembersRepositoryImpl
import com.ariari.mowoori.data.repository.MissionsRepository
import com.ariari.mowoori.data.repository.MissionsRepositoryImpl
import com.ariari.mowoori.data.repository.StampsRepository
import com.ariari.mowoori.data.repository.StampsRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
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
        firebaseAuth: FirebaseAuth,
        prefs: MoWooriPrefDataSource
    ): IntroRepository =
        IntroRepositoryImpl(databaseReference, storageReference, firebaseAuth, prefs)

    @Provides
    @Singleton
    fun providesMissionsRepository(
        databaseReference: DatabaseReference,
        firebaseAuth: FirebaseAuth
    ): MissionsRepository =
        MissionsRepositoryImpl(databaseReference, firebaseAuth)

    @Provides
    @Singleton
    fun providesMainRepository(
        databaseReference: DatabaseReference,
        firebaseAuth: FirebaseAuth
    ): HomeRepository = HomeRepositoryImpl(databaseReference, firebaseAuth)

    @Provides
    @Singleton
    fun providesGroupRepository(
        databaseReference: DatabaseReference,
        firebaseAuth: FirebaseAuth
    ): GroupRepository = GroupRepositoryImpl(databaseReference, firebaseAuth)

    @Provides
    @Singleton
    fun providesStampsRepository(
        databaseReference: DatabaseReference,
        storageReference: StorageReference,
        firebaseAuth: FirebaseAuth
    ): StampsRepository =
        StampsRepositoryImpl(databaseReference, storageReference, firebaseAuth)

    @Provides
    @Singleton
    fun providesMembersRepository(
        databaseReference: DatabaseReference,
        firebaseAuth: FirebaseAuth
    ): MembersRepository = MembersRepositoryImpl(databaseReference, firebaseAuth)
}
