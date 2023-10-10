package com.kronos.logger.exception

import android.content.Context
import android.util.Log
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import javax.inject.Inject

class ExceptionHandlerImpl @Inject constructor() :
    Thread.UncaughtExceptionHandler, ExceptionHandler {
    @Inject
    lateinit var logger: ILogger

    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    private var mContext: Context? = null

    override fun init(context: Context?) {
        mContext = context
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.e(this.javaClass.name, "uncaughtException: ", e)
        logger.write(this::javaClass.name,LoggerType.ERROR,"- Caught Exception - $e")
    }

}
