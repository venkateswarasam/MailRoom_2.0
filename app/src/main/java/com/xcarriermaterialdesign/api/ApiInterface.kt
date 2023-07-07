package com.xcarriermaterialdesign.api

import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {


    @POST("config/forgotpwd")

    suspend fun forgotPassword(@Query("EmailAddress") email: String)
}