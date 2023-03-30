package com.xcarriermaterialdesign.utils

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.os.StrictMode
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatDelegate
import java.util.*

class AnalyticsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()

            .build())
        enableStrictMode(context = this)
        //super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        //   MultiDex.install(this)
        instance = this
        // Foreground.init(this)
        // FirebaseApp.initializeApp(this)
        val old: StrictMode.ThreadPolicy? = StrictMode.getThreadPolicy()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder(old)
                .permitDiskWrites()
                .build())
        StrictMode.setThreadPolicy(old)

    }


    fun setLocaleFa(context: Context) {
        val res: Resources = context.resources
        val configuration: Configuration = res.configuration
        val dm: DisplayMetrics? = res.displayMetrics
       val current: Locale

       // val current = resources.configuration.locale


        current = Locale("en_IN")
       TypefaceUtil.overrideFont(applicationContext, "SERIF", "fonts/SFNS Display Regular.ttf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(current)
            val localeList = LocaleList(current)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            res.updateConfiguration(configuration, dm)
        }
        //  return context;
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(current)
            res.updateConfiguration(configuration, dm)
        } else {
            configuration.locale = current
            res.updateConfiguration(configuration, dm)
        }
    }






    companion object {
        @get:Synchronized
        var instance: AnalyticsApplication? = null
            private set
        fun enableStrictMode(context: Context) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .build())
        }

    }
}