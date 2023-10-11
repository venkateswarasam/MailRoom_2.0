package com.xcarriermaterialdesign.activities.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.activities.login.Authenticate_Response
import com.xcarriermaterialdesign.activities.login.LoginRequest
import com.xcarriermaterialdesign.activities.login.LoginResponse
import com.xcarriermaterialdesign.activities.login.RefreshRequest
import com.xcarriermaterialdesign.api.ApiInterface
import com.xcarriermaterialdesign.api.ApiUtilities
import com.xcarriermaterialdesign.model.ConfigRequest
import com.xcarriermaterialdesign.model.ConfigResponse
import com.xcarriermaterialdesign.model.ForgotResponse
import com.xcarriermaterialdesign.model.GetProfileRequest
import com.xcarriermaterialdesign.model.GetProfileResponse
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DashboardViewModel : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    lateinit var activity: Activity


    var configResponse =  MutableLiveData<ConfigResponse>()

    var authenticateResponse =  MutableLiveData<Authenticate_Response>()

    var getprofileResponse =  MutableLiveData<GetProfileResponse>()


    fun config(activity: Activity) {
        this.activity = activity
    }


    fun configinformation(configRequest: ConfigRequest){

        if (!NetworkConnection().isNetworkAvailable(activity))
        {
            //  SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Network Error").setContentText("You are offline.Internet Connection is not available ").show()


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))

            return
        }



      //  LoadingView.displayLoadingWithText(activity,"Please wait...",false)

        val response = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {

            try {


                var result: ConfigResponse? = response.configuration(configRequest,"Bearer"+ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,""))
                configResponse?.postValue(result!!)

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



    @OptIn(DelicateCoroutinesApi::class)
    fun refresh(refreshRequest: RefreshRequest){



        if (!NetworkConnection().isNetworkAvailable(activity))
        {

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))

            //  SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Network Error").setContentText("You are offline.Internet Connection is not available ").show()
            return
        }



        LoadingView.displayLoadingWithText(activity,"Please wait...",false)

        val repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {

            try {


                var result: Authenticate_Response? = repsonse.refreshtoken(refreshRequest,"APIKey"+" suchi.123")
                authenticateResponse.postValue(result!!)



            }
            catch (throwable:Throwable){

                when(throwable){

                    is HttpException ->{

                        LoadingView.hideLoading()

                        Handler(Looper.getMainLooper()).post {

                            ServiceDialog.ShowDialog(activity, "The server encountered an internal error or misconfiguration and was unable to complete your request.")





                        }





                    }


                    /* is SSLHandshakeException->{

                          LoadingView.hideLoading()

                          Handler(Looper.getMainLooper()).post {
                              SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("The server encountered an internal SSL error or misconfiguration and was unable to complete your request.")
                                  .show()
                          }
                      }
                      */
                    else->{

                        LoadingView.hideLoading()

                        Handler(Looper.getMainLooper()).post {




                            ServiceDialog.ShowDialog(activity, "The server encountered an internal error or misconfiguration and was unable to complete your request.")




                            /* SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("The server encountered an internal error or misconfiguration and was unable to complete your request.")
                                 .show()*/


                            // SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("The server encountered an internal error or misconfiguration and was unable to complete your request.")
                            //   .show()
                        }
                    }

                }
            }

        }

    }







}