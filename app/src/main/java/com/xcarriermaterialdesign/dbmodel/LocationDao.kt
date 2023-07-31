package com.xcarriermaterialdesign.dbmodel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao

interface LocationDao {


    @Insert
    suspend fun insertLocationPackage(locationPackage: List<LocationPackage>)


    @Query("DELETE FROM LocationPackage")
    suspend fun deleteLocationPackage()

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    // fun insertStatusList(statuseX:StatusPackage)


    @Query("SELECT * FROM LocationPackage")
    fun getAllLocationPackages(): List<LocationPackage>


    @Query("SELECT * FROM LocationPackage WHERE LocationId = :locationid")
    fun isData(locationid: String?): List<LocationPackage>




}