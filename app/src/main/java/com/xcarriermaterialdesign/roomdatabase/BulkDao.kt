package com.xcarriermaterialdesign.roomdatabase

import androidx.room.*


@Dao
interface BulkDao {



    @Insert
    fun insertBulkPackage(bulkPackage: BulkPackage)

    @Query("SELECT * FROM BulkPackage_table")
    fun getAllBulkPackages(): List<BulkPackage>

    @Query("SELECT * FROM BulkPackage_table WHERE trackingNumber = :trackingNumber")
    fun isDataExist(trackingNumber: String?): Int

    @Query("SELECT * FROM BulkPackage_table WHERE trackingNumber = :trackingNumber")
    fun isData(trackingNumber: String?): List<BulkPackage>


    @Query("SELECT * FROM BulkPackage_table WHERE binnumber = :binnumber")
    fun isbinData(binnumber: String?): List<BulkPackage>


    @Query("DELETE FROM BulkPackage_table WHERE trackingNumber = :trackingNumber")
    fun deleteBulkPackages(trackingNumber: String?)


    @Query("DELETE FROM BulkPackage_table")
    fun deleteAllBulkPackages()

    @Update
    suspend fun updateBulkPackage(bulkPackage: BulkPackage)

    @Delete
    suspend fun deleteBulkPackage(bulkPackage: BulkPackage)

}