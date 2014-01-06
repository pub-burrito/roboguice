package roboguice.android.util.logging;

import com.google.inject.Inject;

import roboguice.base.util.logging.LogLevel;

import android.util.Log;

/** Default implementation logs to android.util.Log */
public class Print {
    
    /**
     * config is initially set to BaseConfig() with sensible defaults, then replaced
     * by BaseConfig(ContextSingleton) during guice static injection pass.
     */
    @Inject protected static BaseConfig config = new BaseConfig();
    
    public int println(int priority, String msg ) {
        return Log.println(priority,getScope(5), processMessage(msg));
    }

    protected String processMessage(String msg) {
        if( config.getLoggingLevel() <= LogLevel.DEBUG.logLevel() )
            msg = String.format("%s %s", Thread.currentThread().getName(), msg);
        return msg;
    }

    protected static String getScope(int skipDepth) {
        if( config.getLoggingLevel() <= LogLevel.DEBUG.logLevel() ) {
            final StackTraceElement trace = Thread.currentThread().getStackTrace()[skipDepth];
            return config.scope() + "/" + trace.getFileName() + ":" + trace.getLineNumber();
        }

        return config.scope();
    }
}
