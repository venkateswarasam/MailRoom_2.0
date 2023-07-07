package com.xcarriermaterialdesign.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiUtilities {


    fun getInstance(): Retrofit
    {
        return Retrofit.Builder().baseUrl("").addConverterFactory(
            MoshiConverterFactory.create()).build()
    }

}