package com.xcarriermaterialdesign.dbmodel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StatusDao {



    @Insert
    suspend fun insertStatusPackage(statusPackage: List<StatusPackage>)


    @Query("DELETE FROM StatusPackage")
    suspend fun deleteStatusPackage()

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    // fun insertStatusList(statuseX:StatusPackage)


    @Query("SELECT * FROM StatusPackage")
    fun getAllStatusPackages(): List<StatusPackage>


    @Query("SELECT * FROM StatusPackage WHERE StatusCode = :statuscode")
    fun isData(statuscode: String?): List<StatusPackage>



}