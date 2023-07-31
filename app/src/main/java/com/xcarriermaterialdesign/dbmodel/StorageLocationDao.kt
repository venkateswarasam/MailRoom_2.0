package com.xcarriermaterialdesign.dbmodel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao

interface StorageLocationDao {


    @Insert
    suspend fun insertStorageLocationPackage(storageLocationPackage: List<StorageLocationPackage>)


    @Query("DELETE FROM StorageLocationPackage")
    suspend fun deleteStorageLocationPackage()




    @Query("SELECT * FROM StorageLocationPackage")
    fun getAllStorageLocationPackages(): List<StorageLocationPackage>


    @Query("SELECT * FROM StorageLocationPackage WHERE StorageLocationId = :storagelocationId")
    fun isData(storagelocationId: String?): List<StorageLocationPackage>

}