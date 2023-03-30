package com.xcarriermaterialdesign.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ProcessPackage::class], version = 1)
abstract class ProcessDatabase : RoomDatabase() {

    abstract fun processDao(): ProcessDao





}