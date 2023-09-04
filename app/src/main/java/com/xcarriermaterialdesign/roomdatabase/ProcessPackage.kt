package com.xcarriermaterialdesign.roomdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "ProcessPackage_table")

data class ProcessPackage(




    @ColumnInfo(name = "trackingNumber")
    var trackingNumber: String,


    @ColumnInfo(name = "carriername")
    var carriername: String,


    @ColumnInfo(name = "count")
    var count: Int


    )
{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?= null
}