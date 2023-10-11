package com.xcarriermaterialdesign.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiUtilities {


    fun getInstance(): Retrofit
    {
        return Retrofit.Builder().baseUrl("https://mailroom.myxcarrier.com/MobileAppService/api/")
           // .addConverterFactory(ScalarsConverterFactory.create())
          //  .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))

            .addConverterFactory(MoshiConverterFactory.create()).build()
    }

}