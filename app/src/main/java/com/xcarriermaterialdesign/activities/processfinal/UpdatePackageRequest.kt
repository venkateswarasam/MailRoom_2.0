package com.xcarriermaterialdesign.activities.processfinal

import com.xcarriermaterialdesign.model.CheckPackage

data class UpdatePackageRequest(
    val AppType: String,
    val AppVersion: String,
    val Bin: String,
    val Building: String,
    val BulkFlag: Boolean,
    val Carrier: String,
    val CompanyId: String,
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
    val SignName: String,
    val Status: String,
    val StorageLocation: String,
    val UpdatedBy: String,
    val UpdatedOn: String,
    val checkPackageDatas: List<CheckPackage>,
    val packageImages: PackageImages
)