package com.xcarriermaterialdesign.model

data class CheckRequest(
    val BuildingId: String,
    val CheckInTime: String,
    val CheckOutTime: String,
    val CompanyId: String,
    val DiffTime: String,
    val Latitude: String,
    val LoginId: String,
    val Longitude: String,
    val Operation: String,
    val PlantId: String
)