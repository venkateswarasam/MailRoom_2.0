package com.xcarriermaterialdesign.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao

interface TrackingDao {




    @Insert
    fun insertProcessPackage(processPackage: TrackingNumbersRequestItem)


    @Query("SELECT * FROM TrackingNumber_table")
    fun getAllProcessPackages(): List<TrackingNumbersRequestItem>








    @Query("SELECT * FROM TrackingNumber_table WHERE TrackingNumber = :trackingNumber")
    fun isDataExist(trackingNumber: String?): Boolean

    @Query("SELECT * FROM TrackingNumber_table WHERE TrackingNumber = :trackingNumber")
    fun isData(trackingNumber: String?): List<TrackingNumbersRequestItem>

    @Query("DELETE FROM TrackingNumber_table WHERE TrackingNumber = :trackingNumber")
    fun deleteProcessPackages(trackingNumber: String?)


    @Query("UPDATE TrackingNumber_table SET TrackingNumber = :trackingNumber WHERE id = :id")
    fun updateProcessPackages(id: String?,trackingNumber: String?)




    @Query("DELETE FROM TrackingNumber_table")
    fun deleteAllProcessPackages()

  

    // get values from specific column

    @Query("SELECT trackingNumber FROM TrackingNumber_table")
    fun getColumnValues(): List<String?>?

}