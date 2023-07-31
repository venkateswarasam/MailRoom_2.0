package com.xcarriermaterialdesign.api

import com.xcarriermaterialdesign.activities.forgot.ForgotResponses
import com.xcarriermaterialdesign.activities.login.Authenticate_Response
import com.xcarriermaterialdesign.activities.login.LoginRequest
import com.xcarriermaterialdesign.activities.login.LoginRequestNew
import com.xcarriermaterialdesign.activities.login.LoginResponse
import com.xcarriermaterialdesign.activities.login.RefreshRequest
import com.xcarriermaterialdesign.model.ChangePasswordRequest
import com.xcarriermaterialdesign.model.ConfigRequest
import com.xcarriermaterialdesign.model.ConfigResponse
import com.xcarriermaterialdesign.model.GetProfileRequest
import com.xcarriermaterialdesign.model.GetProfileResponse
import com.xcarriermaterialdesign.model.PendingRequest
import com.xcarriermaterialdesign.model.PendingResponse
import com.xcarriermaterialdesign.model.ProfilePicRequest
import com.xcarriermaterialdesign.model.ProfilePicResponse
import com.xcarriermaterialdesign.model.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {


    @GET("Config/ForgotPassword")
    suspend fun forgotPassword(@Query("Email") email: String): ForgotResponses?







    @POST(value = "Authenticate")
    suspend fun authenticate(@Body request: LoginRequest, @Header("Authorization") token: String): Authenticate_Response?

/*

    @GET(value = "Authenticate1")
    suspend fun authenticate1(@Body request: LoginRequest, @Header("Authorization") token: String): Authenticate_Response?
*/




    @POST(value = "GetRefreshToken")
    suspend fun refreshtoken(@Body request: RefreshRequest, @Header("Authorization") token: String): Authenticate_Response?



    @POST(value = "Config/Login")
    suspend fun login(@Body request: LoginRequestNew, @Header("Authorization") token: String): LoginResponse?



    @POST(value = "Config/Configuration")
    suspend fun configuration(@Body request: ConfigRequest, @Header("Authorization") token: String): ConfigResponse?





    @POST(value = "Config/UpdateProfileImage")
    suspend fun uploadprofilepic(@Body request: ProfilePicRequest, @Header("Authorization") token: String): ProfilePicResponse?





    @POST(value = "Config/GetProfile")
    suspend fun getprofile(@Body request: GetProfileRequest, @Header("Authorization") token: String): GetProfileResponse?




    @POST(value = "Config/ChangePassword")
    suspend fun changepassword(@Body request: ChangePasswordRequest, @Header("Authorization") token: String): ProfilePicResponse?




    @POST(value = "Config/UpdateProfile")
    suspend fun updateprofile(@Body request: UpdateProfileRequest, @Header("Authorization") token: String): ProfilePicResponse?




    @POST(value = "Package/PendingDeliveries")
    suspend fun getpackagedetails(@Body request: PendingRequest, @Header("Authorization") token: String): PendingResponse?



}