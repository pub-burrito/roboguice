package roboguice.base.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

import roboguice.base.util.Strings;

import com.google.inject.Inject;


/**
 * A more natural android logging facility.
 *
 * WARNING: CHECK OUT COMMON PITFALLS BELOW
 *
 * Unlike {@link android.util.Log}, Log provides sensible defaults.
 * Debug and Verbose logging is enabled for applications that
 * have "android:debuggable=true" in their AndroidManifest.xml.
 * For apps built using SDK Tools r8 or later, this means any debug
 * build.  Release builds built with r8 or later will have verbose
 * and debug log messages turned off.
 *
 * The default tag is automatically set to your app's packagename,
 * and the current context (eg. activity, service, application, etc)
 * is appended as well.  You can add an additional parameter to the
 * tag using {@link #Log(String)}.
 *
 * Log-levels can be programatically overridden for specific instances
 * using {@link #Log(String, boolean, boolean)}.
 *
 * Log messages may optionally use {@link String#format(String, Object...)}
 * formatting, which will not be evaluated unless the log statement is output.
 * Additional parameters to the logging statement are treated as varrgs parameters
 * to {@link String#format(String, Object...)}
 *
 * Also, the current file and line is automatically appended to the tag
 * (this is only done if debug is enabled for performance reasons).
 *
 * COMMON PITFALLS:
 * * Make sure you put the exception FIRST in the call.  A common
 *   mistake is to place it last as is the android.util.Log convention,
 *   but then it will get treated as varargs parameter.
 * * vararg parameters are not appended to the log message!  You must
 *   insert them into the log message using %s or another similar
 *   format parameter
 *
 * Usage Examples:
 *
 * Ln.v("hello there");
 * Ln.d("%s %s", "hello", "there");
 * Ln.e( exception, "Error during some operation");
 * Ln.w( exception, "Error during %s operation", "some other");
 *
 *
 */
public class Ln  {
    /**
     * config is initially set to BaseConfig() with sensible defaults, then replaced
     * by BaseConfig(ContextSingleton) during guice static injection pass.
     */
    @Inject protected static BaseConfig config = new BaseConfig();

    /**
     * print is initially set to Print(), then replaced by guice during
     * static injection pass.  This allows overriding where the log message is delivered to.
     */
    @Inject protected static Print print = new Print();

    private Ln() {}



    public static int v(Throwable t) {
        return config.getLoggingLevel().logLevel() <= LogLevel.VERBOSE.logLevel() ? print.println(LogLevel.VERBOSE, getStackTraceString(t)) : 0;
    }

    public static int v(Object s1, Object... args) {
        if( config.getLoggingLevel().logLevel() > LogLevel.VERBOSE.logLevel() )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(LogLevel.VERBOSE, message);
    }

    public static int v(Throwable throwable, Object s1, Object... args ) {
        if( config.getLoggingLevel().logLevel() > LogLevel.VERBOSE.logLevel() )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + getStackTraceString(throwable);
        return print.println(LogLevel.VERBOSE, message);
    }

    public static int d(Throwable t) {
        return config.getLoggingLevel().logLevel() <= LogLevel.DEBUG.logLevel() ? print.println(LogLevel.DEBUG, getStackTraceString(t)) : 0;
    }

    public static int d(Object s1, Object... args) {
        if( config.getLoggingLevel().logLevel() > LogLevel.DEBUG.logLevel() )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(LogLevel.DEBUG, message);
    }

    public static int d(Throwable throwable, Object s1, Object... args) {
        if( config.getLoggingLevel().logLevel() > LogLevel.DEBUG.logLevel() )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + getStackTraceString(throwable);
        return print.println(LogLevel.DEBUG, message);
    }

    public static int i(Throwable t) {
        return config.getLoggingLevel().logLevel() <= LogLevel.INFO.logLevel() ? print.println(LogLevel.INFO, getStackTraceString(t)) : 0;
    }

    public static int i( Object s1, Object... args) {
        if( config.getLoggingLevel().logLevel() > LogLevel.INFO.logLevel() )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(LogLevel.INFO, message);
    }

    public static int i(Throwable throwable, Object s1, Object... args) {
        if( config.getLoggingLevel().logLevel() > LogLevel.INFO.logLevel() )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + getStackTraceString(throwable);
        return print.println(LogLevel.INFO, message);
    }

    public static int w(Throwable t) {
        return config.getLoggingLevel().logLevel() <= LogLevel.WARN.logLevel() ? print.println(LogLevel.WARN, getStackTraceString(t)) : 0;
    }

    public static int w( Object s1, Object... args) {
        if( config.getLoggingLevel().logLevel() > LogLevel.WARN.logLevel() )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(LogLevel.WARN, message);
    }

    public static int w( Throwable throwable, Object s1, Object... args) {
        if( config.getLoggingLevel().logLevel() > LogLevel.WARN.logLevel() )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + getStackTraceString(throwable);
        return print.println(LogLevel.WARN, message);
    }

    public static int e(Throwable t) {
        return config.getLoggingLevel().logLevel() <= LogLevel.ERROR.logLevel() ? print.println(LogLevel.ERROR, getStackTraceString(t)) : 0;
    }

    public static int e( Object s1, Object... args) {
        if( config.getLoggingLevel().logLevel() > LogLevel.ERROR.logLevel() )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(LogLevel.ERROR, message);
    }

    public static int e( Throwable throwable, Object s1, Object... args) {
        if( config.getLoggingLevel().logLevel() > LogLevel.ERROR.logLevel() )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + getStackTraceString(throwable);
        return print.println(LogLevel.ERROR, message);
    }

    public static boolean isDebugEnabled() {
        return config.getLoggingLevel().logLevel() <= LogLevel.DEBUG.logLevel();
    }

    public static boolean isVerboseEnabled() {
        return config.getLoggingLevel().logLevel() <= LogLevel.VERBOSE.logLevel();
    }

    public static Config getConfig() {
        return config;
    }

    public static String logLevelToString( int loglevel ) {
        
        if ( loglevel == LogLevel.VERBOSE.logLevel() )
        {
            return "VERBOSE";
        }
        else if ( loglevel == LogLevel.DEBUG.logLevel() )
        {
            return "DEBUG";
        }
        else if ( loglevel == LogLevel.INFO.logLevel() )
        {
            return "INFO";
        }
        else if ( loglevel == LogLevel.WARN.logLevel() )
        {
            return "WARN";
        }
        else if ( loglevel == LogLevel.ERROR.logLevel() )
        {
            return "ERROR";
        }
        else if ( loglevel == LogLevel.ASSERT.logLevel() )
        {
            return "ASSERT";
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Handy function to get a loggable stack trace from a Throwable
     * @param tr An exception to log
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }
}
