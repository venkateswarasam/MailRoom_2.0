package com.xcarriermaterialdesign.roomdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "TrackingNumber_table")

data class TrackingNumbersRequestItem(



    @ColumnInfo(name = "TrackingNumber")
    var TrackingNumber: String,


    ){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?= null
}