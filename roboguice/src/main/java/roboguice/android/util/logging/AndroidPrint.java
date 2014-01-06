package roboguice.android.util.logging;

import roboguice.base.util.logging.Print;

import android.util.Log;

/** Default implementation logs to android.util.Log */
public class AndroidPrint extends Print {
    
    @Override
    public int println(int priority, String msg ) {
        return Log.println(priority,getScope(5), processMessage(msg));
    }
}
