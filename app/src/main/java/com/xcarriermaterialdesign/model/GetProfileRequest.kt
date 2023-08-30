package com.xcarriermaterialdesign.model

data class GetProfileRequest(
    val CompanyId: String,
    val Email: String,
    val LoginId: Int,
    val PlantId: String
)