package com.xcarriermaterialdesign.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object ApplicationSharedPref {

    const val LATTITUDE = "LATTITUDE"
    const val LONGITUDE = "LONGITUDE"
    const val LOGINCHECK = "LOGINCHECK"
    const val MS_EMAIL = "MS_EMAIL"
    const val MS_PASSWORD= "MS_PASSWORD"
    const val PLANT_ID= "PLANT_ID"
    const val LOGINID= "LOGINID"
    const val COMPANY_ID= "COMPANY_ID"
    const val EMP_ID= "EMP_ID"
    const val LOCATIONNAME= "LOCATIONNAME"
    const val TOKEN= "TOKEN"
    const val REFRESH_TOKEN= "REFRESH_TOKEN"
    const val USERNAME= "USERNAME"
    const val USERALIAS= "USERALIAS"
    const val USERROLE= "USERROLE"
    const val PROFILEIMAGE= "PROFILEIMAGE"
    const val ROLEID= "ROLEID"
    const val PLANTNAME= "PLANTNAME"
    const val BUILDING= "BUILDING"


    const val CUSTOMERNAME= "CUSTOMERNAME"
    const val DEPARTMENT= "DEPARTMENT"
    const val DESIGNATION= "DESIGNATION"

    const val SMSCHECK= false
    const val BUILDINGCHECK= false













    private var mSharedPref: SharedPreferences? = null



    fun init(context: Context): SharedPreferences? {
        if (mSharedPref == null) {
            mSharedPref = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        }
        return mSharedPref
    }

    fun read(key: String?, defValue: String?): String? {
        return mSharedPref!!.getString(key, defValue)
    }

    fun write(key: String?, value: String?) {
        val prefsEditor: SharedPreferences.Editor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
        prefsEditor.commit()
    }


    fun readboolean(key: Boolean, defValue: Boolean?): Boolean? {
        return mSharedPref!!.getBoolean(key.toString(), defValue!!)
    }

    fun writeboolean(key: Boolean, value: Boolean?) {
        val prefsEditor: SharedPreferences.Editor = mSharedPref!!.edit()
        prefsEditor.putBoolean(key.toString(), value!!)
        prefsEditor.apply()
        prefsEditor.commit()
    }




}