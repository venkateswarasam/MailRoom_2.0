package com.xcarriermaterialdesign.utils

import android.content.Context
import android.graphics.Typeface
import android.widget.Toast

object TypefaceUtil {


    fun overrideFont(context: Context, defaultFontNameToOverride: String, customFontFileNameInAssets: String) {
        try {
            val customFontTypeface: Typeface? = Typeface.createFromAsset(context.assets, customFontFileNameInAssets)
            val defaultFontTypefaceField = Typeface::class.java.getDeclaredField(defaultFontNameToOverride)
            defaultFontTypefaceField.isAccessible = true
            defaultFontTypefaceField.set(null, customFontTypeface)
        } catch (e: Exception) {
           // Toast.makeText(context,e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}