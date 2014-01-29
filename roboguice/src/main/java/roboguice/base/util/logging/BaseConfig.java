package roboguice.base.util.logging;




public class BaseConfig implements Config {

    protected LogLevel minimumLogLevel = LogLevel.VERBOSE;
    protected String packageName = "";
    protected String scope = "";
    
    public BaseConfig() {
        try {
            packageName = BaseConfig.class.getPackage().getName();
            scope = packageName.toUpperCase();

            //can't really log with Ln just yet as building BaseConfig is done while initializing Ln and BaseConfig is not initialized as an instance in Ln (so Ln.* would all fail)
            //Ln.d("Configuring Logging, minimum log level is %s", Ln.logLevelToString(minimumLogLevel.logLevel()) );

        } catch( Exception e ) {
            System.err.println(
                  String.format( 
                        "%s - %s", 
                        packageName, 
                        "Error configuring logger"
                  ) 
            );
            e.printStackTrace();
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
