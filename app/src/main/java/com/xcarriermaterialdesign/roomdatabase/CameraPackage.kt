package com.xcarriermaterialdesign.roomdatabase

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray


@Entity(tableName = "CameraPackage_table")

data class CameraPackage(

    @ColumnInfo(name = "ImageString")
    var ImageString: String,

/*
    @ColumnInfo(name = "ImageBitmap")
    var ImageBitmap: Bitmap*/




    )


{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?= null
}