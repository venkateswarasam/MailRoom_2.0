package com.xcarriermaterialdesign.roomdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray


@Entity(tableName = "CameraPackage_table")

data class CameraPackage(

    @ColumnInfo(name = "country")
    var country: String,


    @ColumnInfo(name = "alpha2Code")
    var alpha2Code: String,


    @ColumnInfo(name = "alpha3Code")
    var alpha3Code: String,



    @ColumnInfo(name = "numberCode")
    var numberCode: String,



    @ColumnInfo(name = "states")
    var states: String

    )


{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?= null
}