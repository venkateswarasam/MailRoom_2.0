package com.xcarriermaterialdesign.model

data class ChangePasswordRequest(
    val CompanyId: String,
    val Email: String,
    val NewPassword: String,
    val OldPassword: String,
    val PlantId: String,
    val UserName: String
)