package com.xcarriermaterialdesign.trackreport

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.activities.forgot.ForgotResponses
import com.xcarriermaterialdesign.api.ApiInterface
import com.xcarriermaterialdesign.api.ApiUtilities
import com.xcarriermaterialdesign.model.TrackReportResponse
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TrackReportViewModel : ViewModel() {

    lateinit var  activity : Activity


    var trackReportResponse =  MutableLiveData<TrackReportResponse>()



    fun config(activity: Activity)
    {
        this.activity = activity
    }





    fun trackreport(trackreport : String)

    {

        if (!NetworkConnection().isNetworkAvailable(activity))
        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))

            return
        }




        LoadingView.displayLoadingWithText(activity,"Please wait...",false)

        var repsonse = ApiUtilities.getInstance()?.create(ApiInterface::class.java)

        GlobalScope.launch {

            try {


                var result: TrackReportResponse = repsonse!!.trackreport(trackreport,
                    "Bearer"+ ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,""))!!
                trackReportResponse?.postValue(result)

            }
            catch (throwable:Throwable){

                LoadingView.hideLoading()

                when(throwable){


                    is HttpException ->{

                        LoadingView.hideLoading()

                        Handler(Looper.getMainLooper()).post {


                            ServiceDialog.ShowDialog(activity, "The server encountered an internal error or misconfiguration and was unable to complete your request.")





                        }





                    }

                    else->{


                        Handler(Looper.getMainLooper()).post {


                            ServiceDialog.ShowDialog(activity, "The server encountered an internal error or misconfiguration and was unable to complete your request.")




                        }
                    }

                }
            }



        }

    }
}