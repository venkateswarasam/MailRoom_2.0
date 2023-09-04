package com.xcarriermaterialdesign.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.xcarriermaterialdesign.R
import java.util.*

@SuppressLint("StaticFieldLeak")
object ServiceDialog{

    private var tts: TextToSpeech? = null

    var permissionMessage: TextView? = null

    fun ShowDialog(context: Context?, message: String?) {
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
        window.attributes = lp
        dialog.show()

        oktext.setOnClickListener {

            dialog.dismiss()
        }

    }

}
