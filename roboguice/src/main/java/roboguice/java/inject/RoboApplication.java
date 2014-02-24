package roboguice.java.inject;

import java.util.Map;

import com.google.inject.Key;

import roboguice.android.util.RoboContext;

public class RoboApplication implements RoboContext {

    private String configurationLocation;
    
    public RoboApplication( String configurationLocation )
    {
        this.configurationLocation = configurationLocation;
    }
    
    public RoboApplication( RoboContext ctx )
    {
        configurationLocation = ctx.configurationLocation();
    }
    
    public String configurationLocation()
    {
        return configurationLocation;
    }
    
    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return null;
    }

}
