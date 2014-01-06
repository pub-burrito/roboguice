package roboguice.base.util.logging;


import android.util.Log;


public class BaseConfig implements Config {

    protected int minimumLogLevel = LogLevel.VERBOSE.logLevel();
    protected String packageName = "";
    protected String scope = "";
    
    public BaseConfig() {
        try {
            packageName = "";
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
