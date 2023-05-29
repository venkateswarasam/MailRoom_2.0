package com.xcarriermaterialdesign.utils

import android.app.Application
import android.graphics.Bitmap

class MyApplication: Application() {


    private  var companyId : String? = null
    private  var plantId : String? = null
    private  var empId : String? = null
    private  var bitMapSignature : Bitmap? = null

    private  var packageImage1 : Bitmap? = null

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

    }

    companion object {
        private var instance: MyApplication? = null

        fun applicationContext(): MyApplication {
            return instance as MyApplication
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