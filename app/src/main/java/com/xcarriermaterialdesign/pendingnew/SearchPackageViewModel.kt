package com.xcarriermaterialdesign.pendingnew

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.api.ApiInterface
import com.xcarriermaterialdesign.api.ApiUtilities
import com.xcarriermaterialdesign.model.PendingRequest
import com.xcarriermaterialdesign.model.PendingResponse
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SearchPackageViewModel: ViewModel()  {


    lateinit var  activity : Activity

    var pendingResponse =  MutableLiveData<PendingResponse>()


    fun config(activity: Activity)
    {
        this.activity = activity
    }




    fun loadingpendingdeliveries(pendingRequest: PendingRequest){

        if (!NetworkConnection().isNetworkAvailable(activity))
        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))

            return
        }



        LoadingView.displayLoadingWithText(activity,"Please wait...",false)

        var repsonse = ApiUtilities.getInstance()?.create(ApiInterface::class.java)

        GlobalScope.launch {

            try {


                val result: PendingResponse? = repsonse!!.getpackagedetails(pendingRequest,"Bearer"+ ApplicationSharedPref.read(
                    ApplicationSharedPref.TOKEN,""))
                pendingResponse?.postValue(result!!)

            }
            catch (throwable:Throwable){

                when(throwable){

                    is HttpException ->{

                        LoadingView.hideLoading()

                        Handler(Looper.getMainLooper()).post {


                            ServiceDialog.ShowDialog(activity, "The server encountered an internal error or misconfiguration and was unable to complete your request.")




                            /*  SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("The server encountered an internal error or misconfiguration and was unable to complete your request.")
                                  .show()*/
                        }





                    }

                }
            }

        }

    }



    fun getBeforeDate(day: Boolean = false, month: Boolean = false, year: Boolean = false, count: Int = 0): String{

        val currentCalendar = Calendar.getInstance()
        val myFormat = "yyyy-MM-dd"  // you can use your own date format
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        if (day){
            currentCalendar.add(Calendar.DAY_OF_YEAR, -count)
        }else if(month){
            currentCalendar.add(Calendar.MONTH, -count)
        }else if(year){
            currentCalendar.add(Calendar.YEAR, -count)
        }else{
            // if user not provide any value then give current date
            currentCalendar.add(Calendar.DAY_OF_YEAR, 0)
            // or you can throw Exception
            //throw Exception("Please provide at least one value")
        }

        return sdf.format(currentCalendar.time)
    }


}