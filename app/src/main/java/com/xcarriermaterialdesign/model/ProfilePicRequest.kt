package com.xcarriermaterialdesign.model

data class ProfilePicRequest(
    val CompanyId: String,
    val LoginId: Int?,
    val PlantId: String,
    val strImage: String
)