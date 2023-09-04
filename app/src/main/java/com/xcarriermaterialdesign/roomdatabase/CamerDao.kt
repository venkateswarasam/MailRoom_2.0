package com.xcarriermaterialdesign.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface CamerDao {




    @Insert
    fun insertCameraPackage(cameraPackage: CameraPackage)


    @Query("SELECT * FROM CameraPackage_table")
    fun getAllCameraPackages(): List<CameraPackage>



}