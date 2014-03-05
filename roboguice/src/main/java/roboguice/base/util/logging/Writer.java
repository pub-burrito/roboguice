package roboguice.base.util.logging;

import java.util.Calendar;

import com.google.inject.Inject;

public class Writer 
{
    @Inject
    protected static Config config;
    
    public int write(LogLevel priority, String tag, String msg )
    {
        String log = String.format("%s %s %s %s", Calendar.getInstance().getTime(), priority, tag, msg);
        
        System.out.println(log);
        
        return log.length();
    }
}
