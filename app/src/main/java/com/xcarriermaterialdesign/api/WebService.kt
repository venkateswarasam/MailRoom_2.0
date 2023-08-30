package com.xcarriermaterialdesign.api

import com.xcarriermaterialdesign.activities.login.Authenticate_Response
import com.xcarriermaterialdesign.activities.login.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


const val BASE_URL = "https://mailroom-dev.myxcarrier.com/MobileAppService/api/"

interface WebService {



    @GET(value = "Authenticate1")
    suspend fun authenticate(@Body request: LoginRequest, @Header("Authorization") token: String):Response<Authenticate_Response>?


}