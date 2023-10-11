package com.xcarriermaterialdesign.activities.processfinal

import com.xcarriermaterialdesign.model.CheckPackage

data class CreateUpdateRequest(
    val AppType: String,
    val AppVersion: String,
    val Bin: String,
    val Building: String,
    val BulkFlag: Boolean,
    val Carrier: String,
    val CompanyId: String,
    val CreatedBy: String,
    val CreatedOn: String,
    val DeviceName: String,
    val DockNo: String,
    val DriverName: String,
    val Latitude: String,
    val Locker: String,
    val Longitude: String,
    val MailStop: String,
    val Notes: String,
    val OSVersion: String,
    val PlantId: String,
    val Reason: String,
    val Route: String,
    val Status: String,
    val StorageLocation: String,
    val checkPackageDatas: List<CheckPackage>,
    val packageImages: PackageImages,
    val SignName: String,

    )