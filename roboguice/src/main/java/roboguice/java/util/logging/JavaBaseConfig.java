package roboguice.java.util.logging;

import org.apache.log4j.Level;

import roboguice.base.util.logging.BaseConfig;
import roboguice.base.util.logging.LogLevel;

public class JavaBaseConfig extends BaseConfig {

    public JavaBaseConfig()
    {
        //BasicConfigurator.configure();
        //will read from log4j.properties automatically
        
        minimumLogLevel = JavaLogLevel.TRACE.getLogLevel();
    }
    
    static enum JavaLogLevel
    {
        TRACE   ( Level.TRACE,  LogLevel.VERBOSE ),
        DEBUG   ( Level.DEBUG,  LogLevel.DEBUG ),
        WARN    ( Level.WARN,   LogLevel.WARN ),
        INFO    ( Level.INFO,   LogLevel.INFO ), 
        ERROR   ( Level.ERROR,  LogLevel.ERROR ),
        FATAL   ( Level.FATAL,  LogLevel.ERROR )
        ;

        private Level level;
        private LogLevel logLevel;
        
        private JavaLogLevel( Level level )
        {
            this.level = level;
        }
        
        private JavaLogLevel( Level level, LogLevel logLevel )
        {
            this.level = level;
            this.logLevel = logLevel;
        }
        
        public LogLevel getLogLevel()
        {
            return logLevel;
        }
        
        public Level javaLevel()
        {
            return level;
        }
        
        public static JavaLogLevel from( LogLevel canidate )
        {
            for ( JavaLogLevel level : JavaLogLevel.values() )
            {
                if ( level.logLevel == canidate )
                {
                    return level;
                }
            }
            return TRACE;
        }
    }
}
