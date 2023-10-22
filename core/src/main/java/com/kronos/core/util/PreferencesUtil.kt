package com.kronos.core.util

import android.content.Context
import androidx.preference.PreferenceManager

class PreferencesUtil{

    companion object{
        fun getPreference(context:Context,key:String,default:String) = PreferenceManager.getDefaultSharedPreferences(context).getString(key,default)
        fun getPreference(context:Context,key:String,default:Boolean) = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key,default)
        fun getPreference(context:Context,key:String,default:Int) = PreferenceManager.getDefaultSharedPreferences(context).getInt(key,default)
        fun getPreference(context:Context,key:String,default:Float) = PreferenceManager.getDefaultSharedPreferences(context).getFloat(key,default)
        fun getPreference(context:Context,key:String,default:Long) = PreferenceManager.getDefaultSharedPreferences(context).getLong(key,default)
    }

}

