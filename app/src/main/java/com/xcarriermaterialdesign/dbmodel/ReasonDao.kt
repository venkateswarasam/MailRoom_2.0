package com.xcarriermaterialdesign.dbmodel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao

interface ReasonDao {


    @Insert
    suspend fun insertReasonPackage(reasonPackage: List<ReasonPackage>)


    @Query("DELETE FROM ReasonPackage")
    suspend fun deleteReasonPackage()

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    // fun insertStatusList(statuseX:StatusPackage)


    @Query("SELECT * FROM ReasonPackage")
    fun getAllReasonPackages(): List<ReasonPackage>


    @Query("SELECT * FROM ReasonPackage WHERE ReasonId = :reasonid")
    fun isData(reasonid: String?): List<ReasonPackage>
}