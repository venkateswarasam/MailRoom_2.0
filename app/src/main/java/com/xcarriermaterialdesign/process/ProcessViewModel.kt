package com.xcarriermaterialdesign.process

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.api.ApiInterface
import com.xcarriermaterialdesign.api.ApiUtilities
import com.xcarriermaterialdesign.model.CheckRequest
import com.xcarriermaterialdesign.model.CheckTrackingResponse
import com.xcarriermaterialdesign.model.CheckingTrackRequest
import com.xcarriermaterialdesign.model.CheckinoutResponse
import com.xcarriermaterialdesign.model.TrackingNumbersRequest
import com.xcarriermaterialdesign.model.TrackingNumbersRequestItem
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProcessViewModel: ViewModel() {

    lateinit var  activity : Activity

    var checkinoutResponse = MutableLiveData<CheckTrackingResponse>()



    fun config(activity: Activity)
    {
        this.activity = activity
    }



    fun checktrackingnumbers(trackingNumbersRequest: List<TrackingNumbersRequestItem>){
        if (!NetworkConnection().isNetworkAvailable(context = activity ))

        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))
            return
        }



        LoadingView.displayLoadingWithText(activity, text = "Please wait...",false)

        val repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {



            try {


                val result: CheckTrackingResponse = repsonse.checktrackingnumbers(
                    trackingNumbersRequest,"Bearer"+ ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,"")
                )!!
                checkinoutResponse.postValue(result)

                println("==result==$result")


            }
            catch (throwable:Exception){

                when(throwable){

                    is HttpException ->{

                        LoadingView.hideLoading()

                        Handler(Looper.getMainLooper()).post {

                            ServiceDialog.ShowDialog(activity, activity.getString(R.string.servererror))

                        }



                    }


                    else->{


                        LoadingView.hideLoading()

                        Handler(Looper.getMainLooper()).post {

                            ServiceDialog.ShowDialog(activity, throwable.toString())

                        }
                    }

                }
            }

        }

    }



}
