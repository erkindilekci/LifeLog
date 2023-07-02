package com.erkindilekci.mongo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erkindilekci.mongo.local.dao.ImageToDeleteDao
import com.erkindilekci.mongo.local.dao.ImageToUploadDao
import com.erkindilekci.mongo.local.entity.ImageToDelete
import com.erkindilekci.mongo.local.entity.ImageToUpload

@Database(entities = [ImageToUpload::class, ImageToDelete::class], version = 2)
abstract class ImagesDatabase : RoomDatabase() {

    abstract fun imageToUploadDao(): ImageToUploadDao
    abstract fun imageToDeleteDao(): ImageToDeleteDao
}
