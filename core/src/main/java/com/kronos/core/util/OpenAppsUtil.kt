package com.kronos.core.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri


private fun intentTelegram(context: Context, id: String) {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addCategory(Intent.CATEGORY_BROWSABLE)
    if (isAppInstalled(context, "org.telegram.messenger")) {
        intent.setPackage("org.telegram.messenger")
        intent.data = Uri.parse("tg://resolve?domain=$id") //id = netdata_chat
    } else {
        intent.data = Uri.parse("https://t.me/joinchat/$id") //id = netdata_chat
    }
    context.startActivity(intent)
}

private fun intentYoutube(context: Context, id: String) {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.data = Uri.parse(String.format("http://youtube.com/channel/%s", id))
    if (isAppInstalled(context, "com.google.android.youtube")) {
        intent.setPackage("com.google.android.youtube")
    }
    context.startActivity(intent)
}

private fun intentInstagram(context: Context, id: String) {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.data = Uri.parse("http://instagram.com/_u/$id")
    if (isAppInstalled(context, "com.instagram.android")) {
        intent.setPackage("com.instagram.android")
    }
    context.startActivity(intent)
}

fun intentFacebook(context: Context, id: String) {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addCategory(Intent.CATEGORY_BROWSABLE)
    if (isAppInstalled(context, "com.facebook.katana")) {
        intent.setPackage("com.facebook.katana")
        var versionCode = 0
        try {
            versionCode =
                context.packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val uri: Uri = if (versionCode >= 3002850) {
            Uri.parse("fb://facewebmodal/f?href=http://m.facebook.com/$id")
        } else {
            Uri.parse("fb://page/$id")
        }
        intent.data = uri
    } else {
        intent.data = Uri.parse("http://m.facebook.com/$id")
    }
    context.startActivity(intent)
}

fun intentTwitter(context: Context, id: String?) {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addCategory(Intent.CATEGORY_BROWSABLE)
    if (isAppInstalled(context, "com.twitter.android")) {
        intent.setPackage("com.twitter.android")
        intent.data = Uri.parse(String.format("twitter://user?screen_name=%s", id))
    } else {
        intent.data =
            Uri.parse(String.format("http://twitter.com/intent/user?screen_name=%s", id))
    }
    context.startActivity(intent)
}

fun intentBrowser(context: Context, url: String?) {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.data = Uri.parse(url)
    context.startActivity(intent)
}

fun isAppInstalled(context: Context, appName: String?): Boolean {
    val pm: PackageManager = context.packageManager
    val installed: Boolean = try {
        pm.getPackageInfo(appName!!, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
    return installed
}