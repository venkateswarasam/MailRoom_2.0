package com.xcarriermaterialdesign.activities.camera

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import com.xcarriermaterialdesign.R

open class CustomAlert(
    context: Context,
    title: String?,
    message: String?,
    posCaption: String?,
    negCaption: String?,
    setCancelable: Boolean,
    listener: DeleteAlertListener
) : AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme)) {

    init {
        if (!TextUtils.isEmpty(title)) {
            this.setTitle(title)
        }
        setData(context, message, posCaption, negCaption, setCancelable, listener)
    }



    private fun setData(
        context: Context,
        message: CharSequence?,
        posCaption: String?,
        negCaption: String?,
        setCancelable: Boolean,
        listener: DeleteAlertListener
    ) {

        setCancelable(setCancelable)
        this.setMessage(message)
        this.setIcon(0)

        if (!TextUtils.isEmpty(posCaption)) {
            setPositiveButton(posCaption) { _, _ -> listener.onYes() }
        }
        if (!TextUtils.isEmpty(negCaption)) {
            setNegativeButton(negCaption) { _, _ -> listener.onNo() }
        }

        if (context is Activity && context.isFinishing) {
            return
        }

        create().apply {
            setOnShowListener { getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED) }
            show()
        }
    }

    abstract class DeleteAlertListener : BaseDeleteAlertListener {
        override fun onYes() {}
        override fun onNo() {}
    }

    private interface BaseDeleteAlertListener {
        fun onYes()
        fun onNo()
    }
}