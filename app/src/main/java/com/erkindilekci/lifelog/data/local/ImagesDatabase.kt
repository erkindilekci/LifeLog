package com.erkindilekci.lifelog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erkindilekci.lifelog.data.local.dao.ImageToDeleteDao
import com.erkindilekci.lifelog.data.local.dao.ImageToUploadDao
import com.erkindilekci.lifelog.data.local.entity.ImageToDelete
import com.erkindilekci.lifelog.data.local.entity.ImageToUpload

@Database(entities = [ImageToUpload::class, ImageToDelete::class], version = 1, exportSchema = false)
abstract class ImagesDatabase : RoomDatabase() {

    abstract fun imageToUploadDao(): ImageToUploadDao
    abstract fun imageToDeleteDao(): ImageToDeleteDao
}
