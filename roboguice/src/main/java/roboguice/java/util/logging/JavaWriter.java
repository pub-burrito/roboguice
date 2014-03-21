package roboguice.java.util.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import roboguice.base.util.logging.LogLevel;
import roboguice.base.util.logging.Writer;
import roboguice.java.util.logging.JavaBaseConfig.JavaLogLevel;

public class JavaWriter extends Writer {

    @Override
    public int write( LogLevel priority, String tag, String msg ) {
        
        logger( tag ).log( level( priority ), msg );
        
        return 0;
    }

    protected Logger logger( String tag ) {
        Logger logger = Logger.getLogger( tag );
        
        logger.setLevel( level( config.getLoggingLevel() ) );
        
        return logger;
    }
    
    protected Level level( LogLevel priority ) {
        return JavaLogLevel.from( priority ).javaLevel();
    }

}
