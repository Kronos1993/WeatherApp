package com.kronos.logger.interfaces

import com.kronos.logger.LoggerType
import org.apache.log4j.Logger

interface ILogger {
    fun getLogger(klass:String): Logger
    fun configure()
    fun write(klass:String, type: LoggerType, logText:String)
}