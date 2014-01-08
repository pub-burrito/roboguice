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
        //TODO add system prop to determine debug build from prod build
        logger.setLevel( Level.ALL );
        
        minimumLogLevel = JavaLogLevel.ALL.getLogLevel();
    }
    
    @Override
    public void setLoggingLevel(LogLevel level) {
        super.setLoggingLevel(level);
        
        logger.setLevel( JavaLogLevel.from( level ).javaLevel() );
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
