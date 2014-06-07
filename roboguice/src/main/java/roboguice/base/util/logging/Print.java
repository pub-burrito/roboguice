package roboguice.base.util.logging;

import java.io.IOException;
import java.io.StringReader;

import jregex.Pattern;
import jregex.Replacer;

import com.google.inject.Inject;


public class Print {
    
    // 4=Class.method() > 3=Ln.d > 2=Print.println > 1=Print.getScope, 0=debugScope 
    protected static final int LOG_CALLER_DEPTH = 4;
    private static final String SCOPE_SEPARATOR = "/";
    
    private static final String REMOVED_FOR_BREVITY = "...<removed-for-brevity-in-logs>...";
    
    //at least 3 char string, repeated at least 2 more times, ie: abcabcabc would be replaced, abababab would not just like abcabc.
    public static final Pattern PATTERN_REPEATED_CHARS = new Pattern( "(.{3,4}?)\\1\\1+" );
    public static final Replacer REPLACER_REPEATED_CHARS = PATTERN_REPEATED_CHARS.replacer( "$1" + REMOVED_FOR_BREVITY );
    
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
        String msgWithoutLongRepeatedLetters = msg != null ? replace(msg) : msg;
        
        StringBuffer abbreviatedMsg = new StringBuffer();
        
        if ( msgWithoutLongRepeatedLetters != null )
        {
            if ( msgWithoutLongRepeatedLetters.length() > MAX_LINE_LENGTH )
            {
                abbreviatedMsg.append( msgWithoutLongRepeatedLetters.substring(0, MAX_LINE_LENGTH / 2) ); 
                abbreviatedMsg.append( REMOVED_FOR_BREVITY ); 
                abbreviatedMsg.append( msgWithoutLongRepeatedLetters.substring( msgWithoutLongRepeatedLetters.length() - MAX_LINE_LENGTH / 2) ); 
            }
            else
            {
                abbreviatedMsg.append( msgWithoutLongRepeatedLetters ); 
            }
        }
                    
        return abbreviatedMsg.toString();
    }

    private String replace(String msg) {
        try {
            return REPLACER_REPEATED_CHARS.replace( new StringReader( msg ), msg.length() );
            
        } catch (IOException e) {
            e.printStackTrace();
            
            return msg;
        }
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
        String className = trace.getClassName();
        
        return config.hasCustomScope() ? config.scope() : className.substring( 0, className.lastIndexOf(".") >= 0 ? className.lastIndexOf(".") : className.length() );
    }

    protected String scopeSeparator( StackTraceElement trace ) {
        String scope = scope( trace );
        
        boolean hasNoScope = scope == null || scope.length() == 0;
        
        return hasNoScope ? "" : SCOPE_SEPARATOR;
    }
}
