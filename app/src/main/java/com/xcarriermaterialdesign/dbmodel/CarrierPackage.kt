package com.xcarriermaterialdesign.dbmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CarrierPackage(

    @ColumnInfo(name = "CarrierDescription")
    var CarrierDescription: String,

    @ColumnInfo(name = "CarrierId")
    var CarrierId: String,



    ){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int?= null
}