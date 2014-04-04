package roboguice.base.util.logging;

import java.util.regex.Pattern;

import com.google.inject.Inject;

public class Print {
    
    // 4=Class.method() > 3=Ln.d > 2=Print.println > 1=Print.getScope, 0=debugScope 
    protected static final int LOG_CALLER_DEPTH = 4;
    private static final String SCOPE_SEPARATOR = "/";
    
    public static final Pattern PATTERN_REPEATED_CHARS = Pattern.compile( "(.{3,}?)\\1\\1+" ); //at least 3 char string, repeated at least 2 more times, ie: abcabcabc would be replaced, abababab would not just like abcabc.
    private static final String REMOVED_FOR_BREVITY = "...<removed-for-brevity-in-logs>...";
    public static final String CONTINUES = "...";
    
    public static final int MAX_LINE_LENGTH = 5 * 1024; //5 KB

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
        String msgWithoutLongRepeatedLetters = msg != null ? PATTERN_REPEATED_CHARS.matcher( msg ).replaceAll( "$1" + REMOVED_FOR_BREVITY ) : msg;
        
        String abbreviatedMsg = 
                msgWithoutLongRepeatedLetters != null ?
                    msgWithoutLongRepeatedLetters.length() > MAX_LINE_LENGTH ? 
                            msgWithoutLongRepeatedLetters.substring(0, MAX_LINE_LENGTH / 2) + 
                            REMOVED_FOR_BREVITY + 
                            msgWithoutLongRepeatedLetters.substring( msgWithoutLongRepeatedLetters.length() - MAX_LINE_LENGTH / 2) :
                            msgWithoutLongRepeatedLetters :
                    null;
                    
        return abbreviatedMsg;
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
