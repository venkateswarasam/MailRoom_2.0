package com.xcarriermaterialdesign.roomdatabase

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface CamerDao {




    @Insert
    fun insertCameraPackage(cameraPackage: CameraPackage)


    @Query("SELECT * FROM CameraPackage_table")
    fun getAllCameraPackages(): List<CameraPackage>


    @Query("DELETE FROM CameraPackage_table WHERE ImageString = :ImageString")
    fun deleteProcessPackages(ImageString: String?)



    @Query("DELETE FROM CameraPackage_table")
    fun deleteAllBulkPackages()



}