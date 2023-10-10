package com.kronos.core.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast


fun copyText(context: Context, text: String?) {
    val myClipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val myClip: ClipData = ClipData.newPlainText("Copy", text)
    myClipboard.setPrimaryClip(myClip)
    Toast.makeText(context,"Text copied",Toast.LENGTH_SHORT).show()
}