package com.xcarriermaterialdesign.activities.forgot

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.activities.login.LoginResponse
import com.xcarriermaterialdesign.api.ApiInterface
import com.xcarriermaterialdesign.api.ApiUtilities
import com.xcarriermaterialdesign.model.ForgotResponse
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ForgotViewModel  : ViewModel() {

    lateinit var activity: Activity

    var forgotresponse =  MutableLiveData<ForgotResponses>()




    fun config(activity: Activity) {
        this.activity = activity
    }


    fun forgotpassword(email : String)

    {

        if (!NetworkConnection().isNetworkAvailable(activity))
        {
          //  SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Network Error").setContentText("You are offline.Internet Connection is not available ").show()


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))

            return
        }

        if (email.isEmpty())
        {

            ServiceDialog.ShowDialog(activity, "Please enter Email Address")

            // SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("Please enter valid Email Address ").show()
            return
        }

        if (!email.isValidEmail())
        {


            ServiceDialog.ShowDialog(activity, "Please enter valid Email Address")


            return
        }



        LoadingView.displayLoadingWithText(activity,"Please wait...",false)

        var repsonse = ApiUtilities.getInstance()?.create(ApiInterface::class.java)

        GlobalScope.launch {

            try {


                var result: ForgotResponses = repsonse!!.forgotPassword(email)!!
                forgotresponse?.postValue(result)

            }
            catch (throwable:Throwable){

                LoadingView.hideLoading()

                when(throwable){


                    is HttpException ->{

                        LoadingView.hideLoading()

                        Handler(Looper.getMainLooper()).post {


                            ServiceDialog.ShowDialog(activity, "The server encountered an internal error or misconfiguration and was unable to complete your request.")




                          /*  SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("The server encountered an internal error or misconfiguration and was unable to complete your request.")
                                .show()*/
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



    fun String.isValidEmail() =
        isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()


}