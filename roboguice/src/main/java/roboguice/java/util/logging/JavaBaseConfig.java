package roboguice.java.util.logging;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import roboguice.base.util.logging.BaseConfig;
import roboguice.base.util.logging.LogLevel;

import com.google.inject.Inject;

public class JavaBaseConfig extends BaseConfig {

    public Logger logger;
    
    @Inject
    public JavaBaseConfig ( String name )
    {
        BasicConfigurator.configure();
        logger = Logger.getLogger(name);
        
        logger.setLevel( Level.ALL );
        
        minimumLogLevel = JavaLogLevel.ALL.getLogLevel();
    }
    
    @Override
    public void setLoggingLevel(int level) {
        super.setLoggingLevel(level);
        
        logger.setLevel( JavaLogLevel.forLogLevel(level) );
    }
    
    static enum JavaLogLevel
    {
        ALL( Level.ALL ),
        DEBUG( Level.DEBUG ),
        WARN( Level.WARN ),
        INFO( Level.INFO ), 
        ERROR( Level.ERROR ),
        TRACE( Level.TRACE )
        ;

        private Level level;
        
        private JavaLogLevel( Level level )
        {
            this.level = level;
        }
        
        public int getLogLevel()
        {
            switch( this )
            {
                case ALL:
                    return LogLevel.VERBOSE.logLevel();
                case DEBUG:
                    return LogLevel.DEBUG.logLevel();
                case WARN:
                    return LogLevel.WARN.logLevel();
                case INFO:
                    return LogLevel.INFO.logLevel();
                case ERROR:
                    return LogLevel.ERROR.logLevel();
                case TRACE:
                    return LogLevel.ASSERT.logLevel();
                default:
                    return LogLevel.VERBOSE.logLevel();
            }
        }
        
        public static Level forLogLevel( int level )
        {
            LogLevel match = null;
            for ( LogLevel logLevel : LogLevel.values() )
            {
                if ( logLevel.logLevel() == level )
                {
                    match = logLevel;
                    break;
                }
            }
            
            if ( match != null )
            {
                for ( JavaLogLevel logLevel : values() )
                {
                    if ( logLevel.getLogLevel() == level )
                    {
                        return logLevel.level;
                    }
                }
            }
            
            return Level.ALL;
        }
    }
}
