package com.xcarriermaterialdesign.activities.forgot

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.WebActvity
import com.xcarriermaterialdesign.activities.login.MainActivity
import com.xcarriermaterialdesign.databinding.ActivityForgotPasswordBinding
import com.xcarriermaterialdesign.utils.AnalyticsApplication
import com.xcarriermaterialdesign.utils.LoadingView
import com.xcarriermaterialdesign.utils.ServiceDialog

class ForgotPassword : AppCompatActivity() {


    private lateinit var binding: ActivityForgotPasswordBinding

    val model: ForgotViewModel by viewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsApplication.instance!!.setLocaleFa(this)

        //setContentView(R.layout.activity_forgot_password)

        supportActionBar!!.hide()

        model.config(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)






        binding.toolbar.setNavigationOnClickListener {

            finish()
        }


        binding.resetpassword.setOnClickListener {





            model.forgotpassword(binding.emailtext.text.toString())


        }




        binding.privacy.setOnClickListener {


            val intent = Intent(this, WebActvity::class.java)


            startActivity(intent)
        }




        model.forgotresponse.observe(this, Observer<ForgotResponses> { item ->

            LoadingView.hideLoading()

            println("==statuscode${item.StatusCode}")

            if (item.StatusCode == 200) {


                ShowDialog(this, item.Result.ReturnMsg)



                return@Observer

            }

            else{



                ServiceDialog.ShowDialog(this, item.Result.ReturnMsg)

                binding.emailtext.clearFocus()




                return@Observer
            }






        })



    }




    fun ShowDialog(context: Context?, message: String?) {
        val ok: Button
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.servicedialog)

        val header_title = dialog.findViewById<TextView>(R.id.header_title)
        val oktext = dialog.findViewById<TextView>(R.id.oktext)



        header_title.text = message



        //   tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null)

        val lp = WindowManager.LayoutParams()
        val window = dialog.window
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val isFinishing = false
        window.attributes = lp
        dialog.show()

        oktext.setOnClickListener {


            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
            finish()

        }

    }







}