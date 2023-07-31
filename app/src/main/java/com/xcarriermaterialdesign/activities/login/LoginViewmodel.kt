package com.xcarriermaterialdesign.activities.login

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.api.ApiInterface
import com.xcarriermaterialdesign.api.ApiUtilities
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class LoginViewmodel : ViewModel() {

    lateinit var activity: Activity

    var result: Authenticate_Response?= null

    var loginResponse =  MutableLiveData<LoginResponse>()
    var authenticateResponse =  MutableLiveData<Authenticate_Response>()


    fun config(activity: Activity) {
        this.activity = activity
    }


    fun authenticate(loginrequest: LoginRequest, email:String, password:String){



        if (!NetworkConnection().isNetworkAvailable(activity))
        {

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))

            //  SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Network Error").setContentText("You are offline.Internet Connection is not available ").show()
            return
        }

        if (email.isEmpty() && password.isEmpty())
        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.enteremail))

            // SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("Please enter valid Email Address ").show()
            return
        }


        if (email.isEmpty())
        {


            ServiceDialog.ShowDialog(activity, "Please enter Email Address")




           // SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("Please enter valid Email Address ").show()
            return
        }


        if (!email.isValidEmail()){

            ServiceDialog.ShowDialog(activity, "Please enter valid Email Address")

            return

        }

        if (password.isEmpty())
        {

            ServiceDialog.ShowDialog(activity, "Please enter password")

         //   SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("Please enter password").show()
            return
        }

        LoadingView.displayLoadingWithText(activity,"Please wait...",false)

        var repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {


            /*result = repsonse.authenticate(loginrequest,"APIKey"+" suchi.123")!!
            authenticateResponse?.postValue(result!!)*/


             try {


                  result = repsonse.authenticate(loginrequest,"APIKey"+" suchi.123")!!
                 authenticateResponse?.postValue(result!!)



             }
             catch (throwable:Throwable){

                 println("==throw==$throwable")
                 when(throwable){

                     is HttpException ->{

                         LoadingView.hideLoading()

                         Handler(Looper.getMainLooper()).post {

                             ServiceDialog.ShowDialog(activity, "The server encountered an internal error or misconfigurations and was unable to complete your request.")





                        }





                    }



                    else->{

                        LoadingView.hideLoading()

                        Handler(Looper.getMainLooper()).post {

                               ServiceDialog.ShowDialog(activity, "Invalid Credentials")







                        }
                    }

                }
            }

        }

    }










    fun login(loginrequest: LoginRequestNew, email:String, password:String, token:String)

    {

        if (!NetworkConnection().isNetworkAvailable(activity))
        {

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))

            return
        }

        if (email.isEmpty() && password.isEmpty())
        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.enteremail))

            // SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("Please enter valid Email Address ").show()
            return
        }

        if (email.isEmpty()){

            ServiceDialog.ShowDialog(activity, "Please Enter Email Address")

            return
        }



        if (!email.isValidEmail()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.validemail))


        }

        if (password.isEmpty())
        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.passempty))

          //  SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText("Please enter password").show()
            return
        }

        LoadingView.displayLoadingWithText(activity,"Please wait...",false)

        var repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {

            try {

                println("==token==$token")
                println("==lginrequest==$loginrequest")

                var result: LoginResponse? = repsonse.login(loginrequest, "Bearer$token")
                loginResponse?.postValue(result!!)

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

                        }
                    }

                }
            }

        }

    }

    fun String.isValidEmail() =
        isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()




}