package roboguice.java.util.logging;

import roboguice.base.util.logging.LogLevel;
import roboguice.base.util.logging.Writer;
import roboguice.java.util.logging.JavaBaseConfig.JavaLogLevel;

public class JavaWriter extends Writer {

    @Override
    public int write( LogLevel priority, String tag, String msg ) {
        ( ( JavaBaseConfig ) config ).logger.log( JavaLogLevel.from( priority ).javaLevel(), msg );
        return 0;
    }
}
