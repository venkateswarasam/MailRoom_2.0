package com.xcarriermaterialdesign.dbmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity

data class ReasonPackage(

    @ColumnInfo(name = "ReasonId")
    var ReasonId: String,

    @ColumnInfo(name = "ReasonDescription")
    var ReasonDescription: String,



    ){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?= null
}