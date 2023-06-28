package com.xcarriermaterialdesign.roomdatabase

import androidx.room.*


@Dao
interface ProcessDao{



    @Insert
    fun insertProcessPackage(processPackage: ProcessPackage)

    @Query("SELECT * FROM ProcessPackage_table")
    fun getAllProcessPackages(): List<ProcessPackage>

    @Query("SELECT * FROM ProcessPackage_table WHERE trackingNumber = :trackingNumber")
    fun isDataExist(trackingNumber: String?): Int

    @Query("SELECT * FROM ProcessPackage_table WHERE trackingNumber = :trackingNumber")
    fun isData(trackingNumber: String?): List<ProcessPackage>

    @Query("DELETE FROM processpackage_table WHERE trackingNumber = :trackingNumber")
    fun deleteProcessPackages(trackingNumber: String?)


    @Query("UPDATE ProcessPackage_table SET trackingNumber = :trackingNumber WHERE id = :id")
    fun updateProcessPackages(id: String?,trackingNumber: String?)

    @Query("UPDATE ProcessPackage_table SET carriername = :carriername WHERE id = :id")
    fun updateProcessPackageCarrier(id: String?,carriername: String?)





    @Query("DELETE FROM ProcessPackage_table")
    fun deleteAllProcessPackages()

    @Update
    suspend fun updateProcessPackage(processPackage: ProcessPackage)

    @Delete
    suspend fun deleteProcessPackage(processPackage: ProcessPackage)


}