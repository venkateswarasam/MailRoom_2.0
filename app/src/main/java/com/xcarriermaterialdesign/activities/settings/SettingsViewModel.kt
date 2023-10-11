package com.xcarriermaterialdesign.activities.settings

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.api.ApiInterface
import com.xcarriermaterialdesign.api.ApiUtilities
import com.xcarriermaterialdesign.model.ChangePasswordRequest
import com.xcarriermaterialdesign.model.ChangePasswordResponse
import com.xcarriermaterialdesign.model.CheckRequest
import com.xcarriermaterialdesign.model.CheckinoutResponse
import com.xcarriermaterialdesign.model.GetProfileRequest
import com.xcarriermaterialdesign.model.GetProfileResponse
import com.xcarriermaterialdesign.model.GetProfileResponse1
import com.xcarriermaterialdesign.model.ProfilePicRequest
import com.xcarriermaterialdesign.model.ProfilePicResponse
import com.xcarriermaterialdesign.model.UpdateProfileRequest
import com.xcarriermaterialdesign.model.UpdateResponse
import com.xcarriermaterialdesign.utils.ApplicationSharedPref
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.NetworkConnection
import com.xcarriermaterialdesign.utils.ServiceDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.regex.Pattern

class SettingsViewModel : ViewModel() {

    lateinit var activity: Activity

    var profilePicResponse =  MutableLiveData<ProfilePicResponse>()
    var updateResponse =  MutableLiveData<UpdateResponse>()
    var changePasswordResponse =  MutableLiveData<ChangePasswordResponse>()
    var getprofileResponse =  MutableLiveData<GetProfileResponse>()
    var getprofileResponse1 =  MutableLiveData<GetProfileResponse1>()
    var checkinoutResponse = MutableLiveData<CheckinoutResponse>()


    private val PASSWORD_PATTERNNEW= Pattern.compile( "^"+
            "(?=.*[0-9])"+
            "(?=.*[a-z])"+
            "(?=.*[A-Z])"+
            "(?=.*[!@#$%()'*,-./:;<=>?['\']^_'{|}&+=])"+
            "(?=\\S+$)"+
            ".{8,16}"+
            "$")




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


    fun updateprofile(updateProfileRequest: UpdateProfileRequest, firstname:String,
    lastname:String, address1:String, address2:String, city:String, state:String,
    postalcode:String, country:String, email:String, mobile:String){

        if (!NetworkConnection().isNetworkAvailable(activity))

        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))
            return
        }

        if (firstname.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.enterfirstname))

            return

        }


        if (lastname.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.enterlastname))



            return

        }




        if (address1.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.enteraddress1))



            return

        }

        if (address2.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.enteraddress2))



            return

        }


        if (city.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.entercity))



            return

        }


        if (state.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.stateemp))



            return

        }


        if (country.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.countryemp))



            return

        }


        if (postalcode.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.postalemp))



            return

        }

        if (mobile.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.mobilemp))



            return

        }


        if (email.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.enteremail))



            return

        }





        LoadingView.displayLoadingWithText(activity, text = "Please wait...",false)

        val repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {

            println("==profilerequest==$updateProfileRequest")
            println("==token==${ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,"")}")


            try {


                val result: UpdateResponse = repsonse.updateprofile(
                    updateProfileRequest,"Bearer"+ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,"")
                )!!
                updateResponse.postValue(result)


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


    fun updatepassword(changePasswordRequest: ChangePasswordRequest, oldpassword:String,
    newpass:String, confirmpass:String){




        if (!NetworkConnection().isNetworkAvailable(activity))

        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))
            return
        }

        if (oldpassword.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.oldpasswordemp))

            return

        }



        if (!PASSWORD_PATTERNNEW.matcher(oldpassword).matches()){
            ServiceDialog.ShowDialog(activity, activity.getString(R.string.passwordcontains))

            return
        }


        if (newpass.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.newpassemp))

            return

        }



        if (!PASSWORD_PATTERNNEW.matcher(newpass).matches()){
            ServiceDialog.ShowDialog(activity, activity.getString(R.string.passwordcontains))

            return
        }

        if (confirmpass.isEmpty()){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.confirmpassemp))

            return

        }




        if (!PASSWORD_PATTERNNEW.matcher(confirmpass).matches()){
            ServiceDialog.ShowDialog(activity, activity.getString(R.string.passwordcontains))

            return
        }

        if (oldpassword == newpass){

            ServiceDialog.ShowDialog(activity,"Old password and New password are same")

            return
        }





        if (newpass!= confirmpass){

            ServiceDialog.ShowDialog(activity, activity.getString(R.string.newconfirmdiff))

            return

        }



        LoadingView.displayLoadingWithText(activity, text = "Please wait...",false)

        val repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {



            try {


                val result: ChangePasswordResponse = repsonse.changepassword(
                    changePasswordRequest,"Bearer"+ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,"")
                )!!
                changePasswordResponse.postValue(result)


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


                val result: GetProfileResponse = repsonse!!.getprofile(
                    getProfileRequest,"Bearer"+ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,"")
                )!!
                getprofileResponse.postValue(result)


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



    fun getprofiledata1(getProfileRequest: GetProfileRequest){


        if (!NetworkConnection().isNetworkAvailable(activity))

        {


            ServiceDialog.ShowDialog(activity, activity.getString(R.string.networkmsg))
            return
        }



        LoadingView.displayLoadingWithText(activity, text = "Please wait...",false)

        val repsonse = ApiUtilities.getInstance().create(ApiInterface::class.java)

        GlobalScope.launch {



            try {


                val result: GetProfileResponse1 = repsonse!!.getprofile_data(
                    getProfileRequest,"Bearer"+ApplicationSharedPref.read(ApplicationSharedPref.TOKEN,"")
                )!!
                getprofileResponse1.postValue(result)


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


    // checkout

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



}
