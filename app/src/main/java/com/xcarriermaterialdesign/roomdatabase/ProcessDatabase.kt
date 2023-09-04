package com.xcarriermaterialdesign.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xcarriermaterialdesign.dbmodel.CarrierDao
import com.xcarriermaterialdesign.dbmodel.CarrierPackage
import com.xcarriermaterialdesign.dbmodel.LocationDao
import com.xcarriermaterialdesign.dbmodel.LocationPackage
import com.xcarriermaterialdesign.dbmodel.ReasonDao
import com.xcarriermaterialdesign.dbmodel.ReasonPackage
import com.xcarriermaterialdesign.dbmodel.StatusDao
import com.xcarriermaterialdesign.dbmodel.StatusPackage
import com.xcarriermaterialdesign.dbmodel.StorageLocationDao
import com.xcarriermaterialdesign.dbmodel.StorageLocationPackage


@Database(entities = [ProcessPackage::class, BulkPackage::class, StatusPackage::class,
                     LocationPackage::class, StorageLocationPackage::class,
                     ReasonPackage::class, CarrierPackage::class, CameraPackage::class], version = 1)

abstract class ProcessDatabase : RoomDatabase() {

    abstract fun processDao(): ProcessDao

    abstract fun bulkDao(): BulkDao

    abstract fun cameraDao():CamerDao

    abstract fun statusDao(): StatusDao



    abstract fun locationDao(): LocationDao

    abstract fun storageLocationDao(): StorageLocationDao

    abstract fun reasonDao(): ReasonDao

    abstract fun carrierDao(): CarrierDao





}