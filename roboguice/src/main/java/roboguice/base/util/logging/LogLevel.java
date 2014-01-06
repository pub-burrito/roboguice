package roboguice.base.util.logging;

public enum LogLevel {

    VERBOSE ( 0 ),
    DEBUG   ( 1 ),
    INFO    ( 2 ),
    WARN    ( 3 ),
    ERROR   ( 4 ),
    ASSERT  ( 5 )
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
