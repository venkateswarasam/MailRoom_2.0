package com.xcarriermaterialdesign.process

data class CheckingResponse(
    val Message: String,
    val Result: List<Result>,
    val StatusCode: Int,
    val Version: String
)