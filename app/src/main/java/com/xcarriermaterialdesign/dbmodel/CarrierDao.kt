package com.xcarriermaterialdesign.dbmodel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao

interface CarrierDao {


    @Insert
    suspend fun insertCarrierPackage(carrierPackage: List<CarrierPackage>)


    @Query("DELETE FROM CarrierPackage")
    suspend fun deleteCarrierPackage()

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    // fun insertStatusList(statuseX:StatusPackage)


    @Query("SELECT * FROM CarrierPackage")
    fun getAllCarrierPackages(): List<CarrierPackage>


    @Query("SELECT * FROM CarrierPackage WHERE CarrierId = :carrierid")
    fun isData(carrierid: String?): List<CarrierPackage>


}