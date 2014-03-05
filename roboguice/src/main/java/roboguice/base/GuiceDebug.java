package roboguice.base;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;


/**
* Enable or disable Guice debug output
* on the console.
*/
public class GuiceDebug {
    private static Handler handler;
    private static boolean enabled;
    
    protected static StreamHandler handler() {
        StreamHandler streamHandler = new StreamHandler(System.out, new Formatter() {
            public String format(LogRecord record) {
                return String.format("[Guice %s] %s%n",
                                  record.getLevel().getName(),
                                  record.getMessage());
            }
        });
        
        streamHandler.setLevel(Level.ALL);
        
        return streamHandler;
    }

    private GuiceDebug() {}

    public static Logger getLogger() {
        return Logger.getLogger("com.google.inject");
    }

    public static void enable() {
        if ( !enabled )
        {
            Logger guiceLogger = getLogger();
            guiceLogger.addHandler(GuiceDebug.handler = handler());
            guiceLogger.setLevel(Level.ALL);
            
            enabled = true;
        }
    }

    public static void disable() {
        enabled = false;
        
        Logger guiceLogger = getLogger();
        guiceLogger.setLevel(Level.OFF);
        guiceLogger.removeHandler(GuiceDebug.handler);
        
        GuiceDebug.handler = null;
    }
}