package com.xcarriermaterialdesign.activities.settings

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.api.ApiInterface
import com.xcarriermaterialdesign.api.ApiUtilities
import com.xcarriermaterialdesign.model.ProfilePicRequest
import com.xcarriermaterialdesign.model.ProfilePicResponse
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SettingsViewModel : ViewModel() {

    lateinit var activity: Activity

    var profilePicResponse =  MutableLiveData<ProfilePicResponse>()




    fun config(activity: Activity) {
        this.activity = activity
    }


    fun uploadProilePic(profilePicRequest: ProfilePicRequest)
    {
        if (!NetworkConnection().isNetworkAvailable(activity))

        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))
            return
        }





        LoadingView.displayLoadingWithText(activity, text = "Please wait...",false)

        val repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {



            try {


                val result: ProfilePicResponse = repsonse!!.uploadprofilepic(
                    profilePicRequest,"Bearer"+ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,"")
                )!!
                profilePicResponse.postValue(result)


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
