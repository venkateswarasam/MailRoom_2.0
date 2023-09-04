package com.xcarriermaterialdesign.ui.notifications

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.api.ApiInterface
import com.xcarriermaterialdesign.api.ApiUtilities
import com.xcarriermaterialdesign.model.BuildingRequest
import com.xcarriermaterialdesign.model.BuildingResponse
import com.xcarriermaterialdesign.model.CheckRequest
import com.xcarriermaterialdesign.model.CheckinoutResponse
import com.xcarriermaterialdesign.model.GetProfileRequest
import com.xcarriermaterialdesign.model.GetProfileResponse
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NotificationsViewModel : ViewModel() {

    lateinit var activity: Activity


    var buildingResponse =  MutableLiveData<BuildingResponse>()

    var checkinoutResponse = MutableLiveData<CheckinoutResponse>()


   fun config(activity: Activity) {
        this.activity = activity
    }



    fun savebuilding(checkRequest: CheckRequest){
        if (!NetworkConnection().isNetworkAvailable(context = activity ))

        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))
            return
        }



        LoadingView.displayLoadingWithText(activity, text = "Please wait...",false)

        val repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {



            try {


                val result: CheckinoutResponse = repsonse.savecheckbuilding(
                    checkRequest,"Bearer"+ ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,"")
                )!!
                checkinoutResponse.postValue(result)


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



    fun check_building(buildingname:String,buildingRequest: BuildingRequest){


        if (!NetworkConnection().isNetworkAvailable(activity))

        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))
            return
        }


        if (buildingname.isEmpty()){


            ServiceDialog.ShowDialog(activity, "Please Enter or Scan Building Name")
            return
        }



          LoadingView.displayLoadingWithText(activity, text = "Please wait...",false)

        val repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {



            try {


                val result: BuildingResponse = repsonse.checkbuilding(
                    buildingRequest,"Bearer"+ ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,"")
                )!!
                buildingResponse.postValue(result)


            }
            catch (throwable:Throwable){

                when(throwable){

                    is HttpException ->{

                        LoadingView.hideLoading()

                        Handler(Looper.getMainLooper()).post {

                            ServiceDialog.ShowDialog(activity, activity.getString(R.string.servererror))

                        }





                    }

                }
            }

        }
    }


}