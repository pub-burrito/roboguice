package roboguice.java.util.logging;

import org.apache.log4j.Logger;

import roboguice.base.util.logging.LogLevel;
import roboguice.base.util.logging.Writer;
import roboguice.java.util.logging.JavaBaseConfig.JavaLogLevel;

public class JavaWriter extends Writer {

    @Override
    public int write( LogLevel priority, String tag, String msg ) {
        Logger logger = Logger.getLogger(tag);
        logger.setLevel( JavaLogLevel.from( config.getLoggingLevel() ).javaLevel() );
        
        logger.log(JavaLogLevel.from(priority).javaLevel(), msg);
        return 0;
    }
}
