package roboguice.base.util.logging;




public class BaseConfig implements Config {

    protected LogLevel minimumLogLevel = LogLevel.VERBOSE;
    protected String packageName = "";
    protected String scope = "";
    
    public BaseConfig() {
        try {
            packageName = "";
            scope = packageName.toUpperCase();

            Ln.d("Configuring Logging, minimum log level is %s", Ln.logLevelToString(minimumLogLevel.logLevel()) );

        } catch( Exception e ) {
            System.err.println(String.format("%s - %s %s",packageName, "Error configuring logger", e) );
        }
    }

    public LogLevel getLoggingLevel() {
        return minimumLogLevel;
    }

    public void setLoggingLevel(LogLevel level) {
        minimumLogLevel = level;
    }
    
    public String scope() {
        return scope;
    }

}
