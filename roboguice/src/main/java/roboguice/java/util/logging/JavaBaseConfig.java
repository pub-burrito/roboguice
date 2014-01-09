package roboguice.java.util.logging;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;

import roboguice.base.util.logging.BaseConfig;
import roboguice.base.util.logging.LogLevel;

public class JavaBaseConfig extends BaseConfig {

    public JavaBaseConfig()
    {
        BasicConfigurator.configure();
        
        minimumLogLevel = JavaLogLevel.ALL.getLogLevel();
    }
    
    static enum JavaLogLevel
    {
        ALL( Level.ALL, LogLevel.VERBOSE ),
        DEBUG( Level.DEBUG ),
        WARN( Level.WARN ),
        INFO( Level.INFO ), 
        ERROR( Level.ERROR ),
        FATAL( Level.FATAL )
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
            return ALL;
        }
    }
}
