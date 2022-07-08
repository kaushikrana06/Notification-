package com.appnotification.notificationhistorylog.ui

import android.content.Context
import com.appnotification.notificationhistorylog.misc.Util.getAppNameFromPackage
import timber.log.Timber
import org.json.JSONObject

class GroupedDataItem internal constructor(context: Context?, str: String) {
    private val id: Long = 0
    var packageName: String
    var appName: String?
    var text: String
    var preview: String

    init {
        Timber.e("GroupedDataItem: %s : ", str)
        val json = JSONObject(str)
        Timber.e("Json: %s : ", json.toString())
        packageName = json.getString("packageName")
        appName = getAppNameFromPackage(context!!, packageName, false)
        text = str
        val title = json.optString("title")
        val text = json.optString("text")
        preview = """$title
$text""".trim { it <= ' ' }
    }
}