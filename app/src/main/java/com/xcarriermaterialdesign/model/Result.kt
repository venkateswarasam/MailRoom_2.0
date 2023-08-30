package com.xcarriermaterialdesign.model

data class Result(
    val Carriers: List<Carrier>,
    val Locations: List<Location>,
    val MobileUserInfo: MobileUserInfo,
    val Reasons: List<Reason>,
    val ReturnMsg: String,
    val Statuses: List<Statuse>,
    val StorageLocations: List<StorageLocation>
)