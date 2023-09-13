package com.xcarriermaterialdesign.api

import com.xcarriermaterialdesign.activities.forgot.ForgotResponses
import com.xcarriermaterialdesign.activities.login.Authenticate_Response
import com.xcarriermaterialdesign.activities.login.LoginRequest
import com.xcarriermaterialdesign.activities.login.LoginRequestNew
import com.xcarriermaterialdesign.activities.login.LoginResponse
import com.xcarriermaterialdesign.activities.login.RefreshRequest
import com.xcarriermaterialdesign.model.BuildingRequest
import com.xcarriermaterialdesign.model.BuildingResponse
import com.xcarriermaterialdesign.model.ChangePasswordRequest
import com.xcarriermaterialdesign.model.ChangePasswordResponse
import com.xcarriermaterialdesign.model.CheckRequest
import com.xcarriermaterialdesign.model.CheckTrackingResponse
import com.xcarriermaterialdesign.model.CheckingTrackRequest
import com.xcarriermaterialdesign.model.CheckinoutResponse
import com.xcarriermaterialdesign.model.ConfigRequest
import com.xcarriermaterialdesign.model.ConfigResponse
import com.xcarriermaterialdesign.model.GetProfileRequest
import com.xcarriermaterialdesign.model.GetProfileResponse
import com.xcarriermaterialdesign.model.GetProfileResponse1
import com.xcarriermaterialdesign.model.PendingRequest
import com.xcarriermaterialdesign.model.PendingResponse
import com.xcarriermaterialdesign.model.ProfilePicRequest
import com.xcarriermaterialdesign.model.ProfilePicResponse
import com.xcarriermaterialdesign.model.TrackReportResponse
import com.xcarriermaterialdesign.model.TrackingNumbersRequest
import com.xcarriermaterialdesign.model.TrackingNumbersRequestItem
import com.xcarriermaterialdesign.model.UpdateProfileRequest
import com.xcarriermaterialdesign.model.UpdateResponse
import com.xcarriermaterialdesign.process.CheckingResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {


    @GET("Config/ForgotPassword")
    suspend fun forgotPassword(@Query("Email") email: String): ForgotResponses?


    @POST(value = "Authenticate")
    suspend fun authenticate(@Body request: LoginRequest, @Header("Authorization") token: String): Authenticate_Response?






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




    @POST(value = "Config/GetProfile")
    suspend fun getprofile_data(@Body request: GetProfileRequest, @Header("Authorization") token: String): GetProfileResponse1?



    @POST(value = "Config/ChangePassword")
    suspend fun changepassword(@Body request: ChangePasswordRequest, @Header("Authorization") token: String): ChangePasswordResponse?




    @POST(value = "Config/UpdateProfile")
    suspend fun updateprofile(@Body request: UpdateProfileRequest, @Header("Authorization") token: String): UpdateResponse?




    @POST(value = "Package/PendingDeliveries")
    suspend fun getpackagedetails(@Body request: PendingRequest, @Header("Authorization") token: String): PendingResponse?






    @POST(value = "Package/SaveCheckinCheckOut")
    suspend fun savecheckbuilding(@Body request: CheckRequest, @Header("Authorization") token: String): CheckinoutResponse?





    @POST(value = "Package/CheckBuilding")
    suspend fun checkbuilding(@Body request: BuildingRequest, @Header("Authorization") token: String): BuildingResponse?







    @GET("Package/TrackReport")
    suspend fun trackreport(@Query("TrackingNo") trackingnumber: String, @Header("Authorization") token: String): TrackReportResponse?





    @POST(value = "Package/CheckTrackingNo")
    suspend fun checktrackingnumbers(@Body trackingNumbers: List<TrackingNumbersRequestItem>, @Header("Authorization") token: String): CheckTrackingResponse?








}