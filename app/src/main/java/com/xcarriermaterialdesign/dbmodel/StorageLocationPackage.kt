package com.xcarriermaterialdesign.dbmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class StorageLocationPackage(

    @ColumnInfo(name = "StorageLocationId")
    var StorageLocationId: String,

    @ColumnInfo(name = "StorageLocationDescription")
    var StorageLocationDescription: String,



    ){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?= null
}