package com.xcarriermaterialdesign.model

data class LstResponse(
    val BuildingId: String,
    val CarrierDescription: String,
    val CurrentStatus: String,
    val InternalTrackNo: String,
    val ReciepentName: String,
    val RouteId: String,
    val UpdatedOn: String
)