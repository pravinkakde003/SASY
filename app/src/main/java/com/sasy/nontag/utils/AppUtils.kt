package com.sasy.nontag.utils

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sasy.nontag.model.ConnectedHistory
import com.sasy.nontag.model.DetailsMenuItem
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object AppUtils {
    fun getArrayListFromJson(context: Context, @RawRes resourceId: Int): String {
        lateinit var jsonString: String
        try {
            jsonString =
                context.resources.openRawResource(resourceId)
                    .bufferedReader()
                    .use { it.readText() }
        } catch (ioException: IOException) {
            Log.e("AppUtils", ioException.toString())
        }
        return jsonString
    }

    fun getDummyHistoryList(jsonString: String): List<ConnectedHistory> {
        val listType = object : TypeToken<List<ConnectedHistory>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    fun getMenuList(jsonString: String): List<DetailsMenuItem> {
        val listType = object : TypeToken<List<DetailsMenuItem>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    fun getCurrentDateTime(): String? {
        val df = SimpleDateFormat("ddMMyyHHmm")
        return df.format(Calendar.getInstance().time)
    }

    fun getDisplayCurrentDateTime(): String? {
        val df = SimpleDateFormat("dd-MM-yyyy HH:mm")
        return df.format(Calendar.getInstance().time)
    }
}