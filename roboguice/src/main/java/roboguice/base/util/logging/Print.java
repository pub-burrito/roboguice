package roboguice.base.util.logging;

import com.google.inject.Inject;

public class Print {
    /**
     * config is initially set to BaseConfig() with sensible defaults, then replaced
     * by BaseConfig(ContextSingleton) during guice static injection pass.
     */
    @Inject protected static BaseConfig config = new BaseConfig();
     
    public int println(LogLevel priority, String msg )
    {
        return 0;
    }

    protected String processMessage(String msg) {
        if( config.getLoggingLevel().logLevel() <= LogLevel.DEBUG.logLevel() )
            msg = String.format("%s %s", Thread.currentThread().getName(), msg);
        return msg;
    }

    protected static String getScope(int skipDepth) {
        if( config.getLoggingLevel().logLevel() <= LogLevel.DEBUG.logLevel() ) {
            final StackTraceElement trace = Thread.currentThread().getStackTrace()[skipDepth];
            return config.scope() + "/" + trace.getFileName() + ":" + trace.getLineNumber();
        }

        return config.scope();
    }
}
