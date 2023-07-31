package com.xcarriermaterialdesign.dbmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StatusPackage(

    @ColumnInfo(name = "StatusDescription")
    var StatusDescription: String,

    @ColumnInfo(name = "StatusCode")
    var StatusCode: String,



    ){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?= null
}