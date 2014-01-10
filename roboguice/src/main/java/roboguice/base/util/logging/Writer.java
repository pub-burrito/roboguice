package roboguice.base.util.logging;

import com.google.inject.Inject;

public class Writer 
{
    @Inject
    protected static Config config;
    
    public int write(LogLevel priority, String tag, String msg )
    {
        System.out.println(String.format("%s - %s | %s", priority, tag, msg));
        return 0;
    }
}
