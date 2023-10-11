package com.xcarriermaterialdesign.model

import java.io.Serializable

data class CheckPackage(
    var CheckExist: Boolean,
    val CurrentPlantId: String,
    val TrackingNo: String,
    val Type: String
):Serializable