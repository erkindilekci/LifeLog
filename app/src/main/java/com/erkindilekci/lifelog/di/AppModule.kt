package com.erkindilekci.lifelog.di

import android.content.Context
import androidx.room.Room
import com.erkindilekci.lifelog.data.connectivity.NetworkConnectivityObserver
import com.erkindilekci.lifelog.data.local.ImagesDatabase
import com.erkindilekci.lifelog.data.local.dao.ImageToDeleteDao
import com.erkindilekci.lifelog.data.local.dao.ImageToUploadDao
import com.erkindilekci.lifelog.util.Constants.IMAGES_DATABASE
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
