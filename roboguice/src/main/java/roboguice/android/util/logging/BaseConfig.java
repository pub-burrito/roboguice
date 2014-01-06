package roboguice.android.util.logging;

import roboguice.android.util.Ln;

import com.google.inject.Inject;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class BaseConfig implements Config {
    
    protected int minimumLogLevel = Log.VERBOSE;
    protected String packageName = "";
    protected String scope = "";

    public BaseConfig() {
    }

    @Inject
    public BaseConfig(Application context) {
        try {
            packageName = context.getPackageName();
            final int flags = context.getPackageManager().getApplicationInfo(packageName, 0).flags;
            minimumLogLevel = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 ? Log.VERBOSE : Log.INFO;
            scope = packageName.toUpperCase();

            Ln.d("Configuring Logging, minimum log level is %s", Ln.logLevelToString(minimumLogLevel) );

        } catch( Exception e ) {
            Log.e(packageName, "Error configuring logger", e);
        }
    }

    public int getLoggingLevel() {
        return minimumLogLevel;
    }

    public void setLoggingLevel(int level) {
        minimumLogLevel = level;
    }
    
    public String scope() {
        return scope;
    }
}
