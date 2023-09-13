package com.xcarriermaterialdesign.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.xcarriermaterialdesign.BottomNavigationActivity
import com.xcarriermaterialdesign.R
import com.xcarriermaterialdesign.activities.login.MainActivity

object ServiceDialogBottomNavigation {



    fun ShowDialog(context: Context?, message: String?) {

        ApplicationSharedPref.init(context!!)
        val ok: Button
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.info_layout_logout)

        val header_title = dialog.findViewById<TextView>(R.id.header_title)
        val oktext = dialog.findViewById<TextView>(R.id.oktext)
        val cancel = dialog.findViewById<TextView>(R.id.cancel)



        header_title.text = message



        //   tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null)

        val lp = WindowManager.LayoutParams()
        val window = dialog.window
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        dialog.show()

        oktext.setOnClickListener {

            dialog.dismiss()



            val intent = Intent(context, BottomNavigationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)

            /* val intent = Intent(context, MainActivity::class.java)
             context.startActivity(intent)*/
        }


        cancel.setOnClickListener {


            dialog.dismiss()
        }

    }

}