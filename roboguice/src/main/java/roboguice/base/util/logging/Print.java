package roboguice.base.util.logging;

import com.google.inject.Inject;

public class Print {
    
    // 4=Class.method() > 3=Ln.d > 2=Print.println > 1=Print.getScope, 0=debugScope 
    protected static final int LOG_CALLER_DEPTH = 4;
    
    private static final String SCOPE_SEPARATOR = "/";

    /**
     * config is initially set to BaseConfig() with sensible defaults, then replaced
     * by BaseConfig(ContextSingleton) during guice static injection pass.
     */
    @Inject protected static BaseConfig config = new BaseConfig();
    
    @Inject protected static Writer writer = new Writer();
     
    public int println(LogLevel priority, String msg)
    {
        return writer.write(priority, getScope( LOG_CALLER_DEPTH ), processMessage(msg) );
    }

    protected String processMessage(String msg) {
        return msg;
    }

    protected String getScope(int skipDepth) {
        if( config.getLoggingLevel().logLevel() <= LogLevel.DEBUG.logLevel() ) {
            return debugScope(skipDepth);
        }

        return config.scope();
    }

    protected String debugScope(int skipDepth) {
        final StackTraceElement trace = new Throwable().getStackTrace()[skipDepth];
        
        return 
           String
               .format(
                   "%s%s%s:%d [%s]", 
                   scope( trace ), 
                   scopeSeparator( trace ), 
                   trace.getFileName(), 
                   trace.getLineNumber(), 
                   Thread.currentThread().getName()
               );
    }

    protected String scope(final StackTraceElement trace) {
        return config.hasCustomScope() ? config.scope() : trace.getClassName().substring( 0, trace.getClassName().lastIndexOf(".") );
    }

    protected String scopeSeparator( StackTraceElement trace ) {
        String scope = scope( trace );
        
        boolean hasNoScope = scope == null || scope.length() == 0;
        
        return hasNoScope ? "" : SCOPE_SEPARATOR;
    }
}
