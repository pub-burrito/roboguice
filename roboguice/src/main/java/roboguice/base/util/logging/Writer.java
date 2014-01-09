package roboguice.base.util.logging;

import com.google.inject.Inject;

public class Writer 
{
    @Inject
    protected static Config config;
    
    public int write(LogLevel priority, String tag, String msg )
    {
        return 0;
    }
}
