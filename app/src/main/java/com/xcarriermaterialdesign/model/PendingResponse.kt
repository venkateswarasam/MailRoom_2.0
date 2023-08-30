package com.xcarriermaterialdesign.model

data class PendingResponse(
    val Message: String,
    val Result: ResultXXX,
    val StatusCode: Int,
    val Version: String
)