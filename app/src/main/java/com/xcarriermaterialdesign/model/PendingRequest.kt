package com.xcarriermaterialdesign.model

data class PendingRequest(
    val BuildingId: String,
    val CompanyId: String,
    val FromDate: String,
    val InternalTrackNo: String,
    val LoginId: Int,
    val PlantId: String,
    val ReciepentName: String,
    val RouteId: String,
    val StatusCode: String,
    val Todate: String,
    val rowStartIndex: Int
)