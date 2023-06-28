package com.xcarriermaterialdesign.roomdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "BulkPackage_table")

data class BulkPackage(

    @ColumnInfo(name = "trackingNumber")
    var trackingNumber: String,


    @ColumnInfo(name = "packagestatus")
    var packagestatus: String,

    @ColumnInfo(name = "datetime")
    var datetime: String,

    @ColumnInfo(name = "routeid")
    var routeid: String,

    @ColumnInfo(name = "binnumber")
    var binnumber: String,

    @ColumnInfo(name = "carriername")
    var carriername: String

)


{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?= null
}