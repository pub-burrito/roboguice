package roboguice.base.util.logging;

import org.apache.maven.artifact.ant.shaded.StringUtils;

import com.google.inject.Inject;

public class Print {
    
    protected static final int LOG_CALLER_DEPTH = 5;

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
        final StackTraceElement trace = Thread.currentThread().getStackTrace()[skipDepth];
        
        return 
           String
               .format(
                   "%s%s%s:%d [%s]", 
                   config.scope(), 
                   scopeSeparator(), 
                   trace.getFileName(), 
                   trace.getLineNumber(), 
                   Thread.currentThread().getName()
               );
    }

    protected String scopeSeparator() {
        return !StringUtils.isEmpty( config.scope() ) ? "/" : "";
    }
}
