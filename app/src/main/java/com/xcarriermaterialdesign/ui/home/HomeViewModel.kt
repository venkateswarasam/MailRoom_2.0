package com.xcarriermaterialdesign.ui.home


import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.api.ApiInterface
import com.xcarriermaterialdesign.api.ApiUtilities
import com.xcarriermaterialdesign.model.GetProfileRequest
import com.xcarriermaterialdesign.model.GetProfileResponse
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException


class HomeViewModel : ViewModel() {

    lateinit var activity: Activity

    var getprofileResponse =  MutableLiveData<GetProfileResponse>()


    fun config(activity: Activity) {
        this.activity = activity
    }






    fun getprofiledata(getProfileRequest: GetProfileRequest){


        if (!NetworkConnection().isNetworkAvailable(activity))

        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))
            return
        }



      //  LoadingView.displayLoadingWithText(activity, text = "Please wait...",false)

        val repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {



            try {


                val result: GetProfileResponse = repsonse.getprofile(
                    getProfileRequest,"Bearer"+ ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,"")
                )!!
                getprofileResponse.postValue(result)


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