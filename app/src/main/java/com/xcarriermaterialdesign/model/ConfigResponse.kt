package com.xcarriermaterialdesign.model

data class ConfigResponse(
    val Message: String,
    val Result: Result,
    val StatusCode: Int,
    val Version: String
)