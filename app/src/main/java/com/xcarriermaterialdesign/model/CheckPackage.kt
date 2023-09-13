package com.xcarriermaterialdesign.model

data class CheckPackage(
    val CheckExist: Boolean,
    val CurrentPlantId: String,
    val TrackingNo: String,
    val Type: String
)