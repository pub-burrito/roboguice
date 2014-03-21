package roboguice.android.util.logging;

import roboguice.base.util.logging.BaseConfig;
import roboguice.base.util.logging.LogLevel;

import com.google.inject.Inject;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class AndroidBaseConfig extends BaseConfig {

    public AndroidBaseConfig() {
    }

    @Inject
    public AndroidBaseConfig(Application context) {
        try {
            packageName = context.getPackageName();
            final int flags = context.getPackageManager().getApplicationInfo(packageName, 0).flags;
            minimumLogLevel = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 ? LogLevel.VERBOSE : LogLevel.INFO;
            scope = packageName;

            //Ln.d("Configuring Logging, minimum log level is %s", Ln.logLevelToString(minimumLogLevel.logLevel()) );

        } catch( Exception e ) {
            Log.e(packageName, "Error configuring logger", e);
        }
    }
}
