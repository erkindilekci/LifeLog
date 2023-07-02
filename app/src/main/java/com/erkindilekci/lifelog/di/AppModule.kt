package com.erkindilekci.lifelog.di

import android.content.Context
import androidx.room.Room
import com.erkindilekci.mongo.local.ImagesDatabase
import com.erkindilekci.mongo.local.dao.ImageToDeleteDao
import com.erkindilekci.mongo.local.dao.ImageToUploadDao
import com.erkindilekci.util.Constants.IMAGES_DATABASE
import com.erkindilekci.util.connectivity.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ImagesDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = ImagesDatabase::class.java,
            name = IMAGES_DATABASE
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideImageToUploadDao(
        db: ImagesDatabase
    ): ImageToUploadDao = db.imageToUploadDao()

    @Provides
    @Singleton
    fun provideImageToDeleteDao(
        db: ImagesDatabase
    ): ImageToDeleteDao = db.imageToDeleteDao()

    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context
    ) = NetworkConnectivityObserver(context)
}
