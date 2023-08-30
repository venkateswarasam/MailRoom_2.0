package com.xcarriermaterialdesign.activities.login

data class LoginRequest(
    val Email: String,
    val Password: String,
    val DeviceId:String,
    val PackageName:String
)