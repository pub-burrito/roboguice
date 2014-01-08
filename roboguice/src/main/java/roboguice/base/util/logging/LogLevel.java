package roboguice.base.util.logging;

public enum LogLevel {

    VERBOSE ( 2 ),
    DEBUG   ( 3 ),
    INFO    ( 4 ),
    WARN    ( 5 ),
    ERROR   ( 6 ),
    ASSERT  ( 7 )
    ;
    
    private int logLevel;
    
    private LogLevel( int logLevel )
    {
        this.logLevel = logLevel;
    }
    
    public int logLevel()
    {
        return this.logLevel;
    }
}
