package com.xcarriermaterialdesign.dbmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class LocationPackage(

    @ColumnInfo(name = "LocationId")
    var LocationId: String,

    @ColumnInfo(name = "LocationName")
    var LocationName: String,



    ){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?= null
}