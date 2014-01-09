package roboguice.android.util.logging;

import roboguice.base.util.logging.LogLevel;
import roboguice.base.util.logging.Writer;

import android.util.Log;

/** Default implementation logs to android.util.Log */
public class AndroidWriter extends Writer {
    
    @Override
    public int write( LogLevel priority, String tag, String msg ) {
        return Log.println( priority.logLevel(), tag, msg );
    }
    
    
}
