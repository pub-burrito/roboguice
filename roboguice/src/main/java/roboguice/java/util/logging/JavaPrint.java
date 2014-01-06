package roboguice.java.util.logging;

import roboguice.base.util.logging.Print;
import roboguice.java.util.logging.JavaBaseConfig.JavaLogLevel;

public class JavaPrint extends Print {

    @Override
    public int println(int priority, String msg) {
        ( ( JavaBaseConfig ) config ).logger.log( JavaLogLevel.forLogLevel( priority ), msg );
        return 0;
    }
}
