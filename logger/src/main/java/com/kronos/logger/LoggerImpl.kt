package com.kronos.logger

import com.kronos.core.io.PersistenceOptions
import com.kronos.core.io.getBasePath
import com.kronos.core.persistance.di.InternalLogsPersistenceOptions
import com.kronos.logger.interfaces.ILogger
import de.mindpipe.android.logging.log4j.LogConfigurator
import org.apache.log4j.Logger
import javax.inject.Inject


class LoggerImpl @Inject constructor(
    @InternalLogsPersistenceOptions private val persistenceOptions: PersistenceOptions
) : ILogger {
    
    lateinit var logger: Logger
    private var logConfigurator = LogConfigurator()
    var fileName = "log.log"
    private var filePattern = "%d - [%c] - %p : %m%n"

    // set max. number of backed up log files
    var maxBackupSize = 10

    // set max. size of log file
    var maxFileSize = (1024 * 1024).toLong()

    override fun getLogger(klass: String):Logger {
        if (!this::logger.isInitialized){
            logger = Logger.getLogger(klass)
        }
        return logger
    }

    override fun configure() {

        // set the name of the log file
        logConfigurator.fileName = "${persistenceOptions.getBasePath().absolutePath}/$fileName"

        // set output format of the log line
        // see :
        // http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
        logConfigurator.filePattern = filePattern

        // set immediateFlush = true, if you want output stream will be flushed
        // at the end of each append operation
        // default value is true
        // _logConfigurator.setImmediateFlush(immediateFlush);

        // set output format of the LogCat line
        // see :
        // http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
        // _logConfigurator.setLogCatPattern(logCatPattern);

        // Maximum number of backed up log files
        logConfigurator.maxBackupSize = maxBackupSize

        // Maximum size of log file until rolling
        logConfigurator.maxFileSize = maxFileSize;

        // set true to appends log events to a file, otherwise set false
        // default value is true
        // _logConfigurator.setUseFileAppender(useFileAppender);

        // set true to appends log events to a LogCat, otherwise set false
        // default value is true
        // _logConfigurator.setUseLogCatAppender(useLogCatAppender);

        // configure
        logConfigurator.configure();
    }

    override fun write(klass: String, type: LoggerType, logText: String) {
        getLogger(klass)
        when(type){
             LoggerType.ERROR ->{
                 logger.error(logText)
             }
            LoggerType.WARNING ->{
                logger.warn(logText)
            }
            LoggerType.INFO ->{
                logger.info(logText)
            }
        }
    }
}