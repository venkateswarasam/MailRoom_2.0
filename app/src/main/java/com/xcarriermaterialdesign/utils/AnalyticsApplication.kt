package com.xcarriermaterialdesign.utils

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Build
import android.os.LocaleList
import android.os.StrictMode
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatDelegate
import java.util.*

class AnalyticsApplication : Application() {


    private  var companyId : String? = null
    private  var plantId : String? = null
    private  var empId : String? = null
    private  var bitMapSignature : Bitmap? = null

    private  var packageImage1 : Bitmap? = null
    private  var packageImage2 : Bitmap? = null
    private  var packageImage3 : Bitmap? = null

    private  var image1Base64Str : String? = null

    private  var image2Base64Str : String? = null

    private  var image3Base64Str : String? = null

    private  var digitalSignBase64Str : String? = null

    private  var userInfo : Boolean? = null

    private  var mailNotify : Boolean? = null

    private  var latitudeStr : String? = null

    private  var longitudeStr : String? = null

    private  var locationNameStr : String? = null






    init {

        instance = this
    }





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
       TypefaceUtil.overrideFont(applicationContext, "SERIF", "fonts/opensans.ttf")
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


    fun setCompanyId(companyId:String?) {

        this.companyId = companyId

    }

    fun getCompanyId(): String?
    {
        return companyId
    }
    fun setPlantId(plantId:String?) {

        this.plantId = plantId

    }

    fun getPlantId(): String?
    {
        return plantId
    }

    fun setEmpId(empId:String?) {

        this.empId = empId

    }

    fun getEmpId(): String?
    {
        return empId
    }

    fun setBitmapSign(bitmap: Bitmap?) {

        this.bitMapSignature = bitmap

    }

    fun getBitmapSign(): Bitmap?
    {
        return bitMapSignature
    }


    fun setPackageImage1(bitmap: Bitmap?) {

        this.packageImage1 = bitmap

    }

    fun getPackageImage1(): Bitmap?
    {
        return packageImage1
    }












    fun setPackageImage2(bitmap: Bitmap?) {

        this.packageImage2 = bitmap

    }

    fun getPackageImage2(): Bitmap?
    {
        return packageImage2
    }



    fun setPackageImage3(bitmap: Bitmap?) {

        this.packageImage3 = bitmap

    }

    fun getPackageImage3(): Bitmap?
    {
        return packageImage3
    }



    fun setImage1Base64Str(base64:String?) {

        this.image1Base64Str = base64

    }

    fun getImage1Base64Str(): String?
    {
        return image1Base64Str
    }
    fun setImage2Base64Str(base64:String?) {

        this.image2Base64Str = base64

    }

    fun getImage2Base64Str(): String?
    {
        return image2Base64Str
    }

    fun setImage3Base64Str(base64:String?) {

        this.image3Base64Str = base64

    }

    fun getImage3Base64Str(): String?
    {
        return image3Base64Str
    }

    fun setDigitalSignBase64(base64:String?) {

        this.digitalSignBase64Str = base64

    }

    fun getDigitalSignBase64(): String?
    {
        return digitalSignBase64Str
    }

    fun setUserInfoFlag(userinfo:Boolean?) {

        this.userInfo = userinfo

    }

    fun getUserInfoFlag(): Boolean?
    {
        return userInfo
    }

    fun setMailNotifyInfoFlag(mailNotify:Boolean?) {

        this.mailNotify = mailNotify

    }

    fun getMailNotifyFlag(): Boolean?
    {
        return mailNotify
    }

    fun setLatitudeStr(latitude:String?) {

        this.latitudeStr = latitude

    }

    fun getLatitudeStr(): String?
    {
        return latitudeStr
    }

    fun setLongitudeStr(longitude:String?) {

        this.longitudeStr = longitude

    }

    fun getLongitudeStr(): String?
    {
        return longitudeStr
    }

    fun setLocationStr(locationStr:String?) {

        this.locationNameStr = locationStr

    }

    fun getLocationStr(): String?
    {
        return locationNameStr
    }
}