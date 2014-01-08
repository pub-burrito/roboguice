package roboguice.java.util.logging;

import roboguice.base.util.logging.LogLevel;
import roboguice.base.util.logging.Print;
import roboguice.java.util.logging.JavaBaseConfig.JavaLogLevel;

public class JavaPrint extends Print {

    @Override
    public int println(LogLevel priority, String msg) {
        ( ( JavaBaseConfig ) config ).logger.log( JavaLogLevel.from( priority ).javaLevel(), msg );
        return 0;
    }
}
